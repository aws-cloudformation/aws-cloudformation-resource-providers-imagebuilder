package software.amazon.imagebuilder.image;

import software.amazon.awssdk.services.imagebuilder.model.Ami;
import software.amazon.awssdk.services.imagebuilder.model.CreateImageResponse;
import software.amazon.awssdk.services.imagebuilder.model.Image;
import software.amazon.awssdk.services.imagebuilder.model.ImageStatus;
import software.amazon.awssdk.services.imagebuilder.model.InvalidParameterException;
import software.amazon.awssdk.services.imagebuilder.model.ResourceAlreadyExistsException;
import software.amazon.awssdk.services.imagebuilder.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.Objects;

public class CreateHandler extends BaseHandler<CallbackContext> {
    private AmazonWebServicesClientProxy clientProxy;
    private Logger logger;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        this.clientProxy = proxy;
        this.logger = logger;

        if (callbackContext != null && callbackContext.isImageCreationInvoked()) {
            return stabilizeImageCreation(proxy, callbackContext, request);
        } else {
            return createImage(proxy, request);
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> createImage(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request) {

        ResourceModel model = request.getDesiredResourceState();
        CreateImageResponse response;
        try {
            response = proxy.injectCredentialsAndInvokeV2(RequestUtil.generateCreateImageRequest(model),
                    ClientBuilder.getImageBuilderClient()::createImage);
        } catch (final ResourceAlreadyExistsException e) {
            logger.log(e.getMessage());
            throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, Objects.toString(model.getPrimaryIdentifier()));
        } catch (final InvalidParameterException e) {
            logger.log(e.getMessage());
            throw new CfnInvalidRequestException(ResourceModel.TYPE_NAME);
        }

        model.setArn(response.imageBuildVersionArn());

        CallbackContext stabilizationContext = CallbackContext.builder()
                .imageCreationInvoked(true)
                .build();
        return ProgressEvent.defaultInProgressHandler(
                stabilizationContext,
                10,
                model);
    }
    
    private ProgressEvent<ResourceModel, CallbackContext> stabilizeImageCreation(
            final AmazonWebServicesClientProxy proxy,
            final CallbackContext callbackContext,
            final ResourceHandlerRequest<ResourceModel> request) {
        ResourceModel model = request.getDesiredResourceState();

        // read to ensure resource exists, and check the status
        try {
            Image image = proxy.injectCredentialsAndInvokeV2(RequestUtil.generateGetImageRequest(model),
                    ClientBuilder.getImageBuilderClient()::getImage).image();
            ImageStatus imageStatus = image.state().status();

            // Check the general image status, it will also cover the distribution AMI status
            if (imageStatus.name().equals(ImageStatus.AVAILABLE.name())) {
                List<Ami> amis = image.outputResources().amis();
                String currentRegion = request.getRegion();
                for (Ami ami : amis) {
                    if (ami.region().equals(currentRegion)) {
                        // Return the Image Id in Current Region
                        model.setImageId(ami.image());
                    }
                }
                return ProgressEvent.defaultSuccessHandler(model);
            } else if (imageStatus.name().equals(ImageStatus.CANCELLED.name())) {
                return ProgressEvent.defaultFailureHandler(
                        new CfnGeneralServiceException("Image creation is cancelled."),
                        HandlerErrorCode.GeneralServiceException);
            } else if (imageStatus.name().equals(ImageStatus.FAILED.name())) {
                return ProgressEvent.defaultFailureHandler(
                        new CfnGeneralServiceException(image.state().reason()),
                        HandlerErrorCode.GeneralServiceException);
            } else {
                return ProgressEvent.defaultInProgressHandler(
                        callbackContext,
                        10,
                        model);
            }
        } catch (final ResourceNotFoundException e) {
            return ProgressEvent.defaultFailureHandler(new CfnNotFoundException(ResourceModel.TYPE_NAME, e.toString()),
                    HandlerErrorCode.GeneralServiceException);
        }
    }
}
