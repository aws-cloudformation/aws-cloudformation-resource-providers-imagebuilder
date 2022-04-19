package software.amazon.imagebuilder.image;

import com.google.common.collect.ImmutableSet;
import software.amazon.awssdk.services.imagebuilder.model.Image;
import software.amazon.awssdk.services.imagebuilder.model.ImageStatus;
import software.amazon.awssdk.services.imagebuilder.model.ImagebuilderException;
import software.amazon.awssdk.services.imagebuilder.model.InvalidParameterValueException;
import software.amazon.awssdk.services.imagebuilder.model.ResourceDependencyException;
import software.amazon.awssdk.services.imagebuilder.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Set;

import static software.amazon.imagebuilder.image.CallbackContext.WorkflowStatus.IN_TERMINAL_STATE;
import static software.amazon.imagebuilder.image.CallbackContext.WorkflowStatus.NOT_DETERMINED;
import static software.amazon.imagebuilder.image.CallbackContext.WorkflowStatus.DELETED;
import static software.amazon.imagebuilder.image.CallbackContext.WorkflowStatus.RUNNING;

public class DeleteHandler extends BaseHandler<CallbackContext> {
    private static final Set<String> TERMINAL_STATES = ImmutableSet.of(
            ImageStatus.AVAILABLE.name(),
            ImageStatus.FAILED.name(),
            ImageStatus.CANCELLED.name()
    );

    private AmazonWebServicesClientProxy clientProxy;
    private Logger logger;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            CallbackContext callbackContext,
            final Logger logger) {
        this.clientProxy = proxy;
        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();
        final CallbackContext.WorkflowStatus workflowStatus;
        if (callbackContext == null || callbackContext.getImageWorkflowStatus() == null) {
            workflowStatus = NOT_DETERMINED;
            callbackContext = CallbackContext.builder()
                    .imageWorkflowStatus(workflowStatus)
                    .build();
        } else {
            workflowStatus = callbackContext.getImageWorkflowStatus();
        }

        logger.log(String.format("Image '%s' is executing in '%s' state in delete handler.",
                model.getArn(), callbackContext.getImageWorkflowStatus()));

        switch (workflowStatus){
            case NOT_DETERMINED:
                try {
                    Image image = proxy.injectCredentialsAndInvokeV2(RequestUtil.generateGetImageRequest(model),
                            ClientBuilder.getImageBuilderClient()::getImage).image();
                    ImageStatus imageStatus = image.state().status();

                    if (isTerminalStatus(imageStatus.name())) {
                        return deleteImage(proxy, callbackContext, request);
                    } else {
                        return cancelImageCreation(proxy, callbackContext, request);
                    }
                } catch (ResourceNotFoundException e) {
                    // This could happen when the image resource has been deleted outside of CFN.
                    // Contract test requires delete on deleted resource to be FAILED with errorCode specified.
                    // CFN will retry the failed delete operation 3 times if it is failed,
                    // then marking the stack as delete succeed and expose the delete failed events to customer.
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .callbackContext(callbackContext)
                            .status(OperationStatus.FAILED)
                            .errorCode(HandlerErrorCode.NotFound)
                            .message(String.format("Attempted DELETE, but Image %s was not found", model.getArn()))
                            .build();
                } catch (ImagebuilderException ex) {
                    if (ex.statusCode() == 403 || ex.statusCode() == 401) {
                        // For users who don't have GetImage permission.. :(
                        return deleteImage(proxy, callbackContext, request);
                    } else {
                        throw ex;
                    }
                }
            case RUNNING:
                return stabilizeImageDeletion(proxy, callbackContext, request);
            case IN_TERMINAL_STATE:
                return deleteImage(proxy, callbackContext, request);
            case DELETED:
                return ProgressEvent.<ResourceModel, CallbackContext>builder()
                        .callbackContext(callbackContext)
                        .status(OperationStatus.SUCCESS)
                        .build();

            default:
                throw new CfnGeneralServiceException(String.format("Undefined workflow status %s", workflowStatus.name()));
        }
    }

    private ProgressEvent<ResourceModel, CallbackContext> deleteImage(
            final AmazonWebServicesClientProxy proxy,
            final CallbackContext callbackContext,
            final ResourceHandlerRequest<ResourceModel> request) {

        final ResourceModel model = request.getDesiredResourceState();
        try {
            proxy.injectCredentialsAndInvokeV2(RequestUtil.generateDeleteImageRequest(model),
                    ClientBuilder.getImageBuilderClient()::deleteImage);
        } catch (ResourceDependencyException e) {
            // return failure terminal progress event as the image is still in running state.
            logger.log(String.format("The image '%s' could not be deleted as it is not in terminal state.", model.getArn()));
            return ProgressEvent.defaultFailureHandler(new CfnInternalFailureException(), HandlerErrorCode.ServiceInternalError);
        } catch (ResourceNotFoundException e) {
            // Swallow this exception as the image could be deleted from outside of CFN.
            // It is required to support out-of-band resource deletion so that the stack could be deleted.
            logger.log(String.format("The image '%s' has been deleted out of band.", model.getArn()));
        }

        callbackContext.setImageWorkflowStatus(DELETED);
        return ProgressEvent.defaultInProgressHandler(
                callbackContext,
                1, // callbackDelaySeconds - sooner as it has been deleted.
                model);
    }

    private ProgressEvent<ResourceModel, CallbackContext> cancelImageCreation(
            final AmazonWebServicesClientProxy proxy,
            final CallbackContext callbackContext,
            final ResourceHandlerRequest<ResourceModel> request) {

        ResourceModel model = request.getDesiredResourceState();
        callbackContext.setImageWorkflowStatus(RUNNING);

        try {
            proxy.injectCredentialsAndInvokeV2(RequestUtil.generateCancelImageCreationRequest(model),
                    ClientBuilder.getImageBuilderClient()::cancelImageCreation);
        }  catch (ResourceNotFoundException e) {
            // This is the case when Uluru handler goes into RUNNING state but image has been deleted outside of CFN.
            // We consider the image had been deleted and send Uluru handler to the next state.
            logger.log(String.format("The image '%s' has been deleted out of band.", model.getArn()));
            callbackContext.setImageWorkflowStatus(DELETED);
            return ProgressEvent.defaultInProgressHandler(
                    callbackContext,
                    1, // callbackDelaySeconds - sooner as it has been deleted.
                    model);
        } catch (InvalidParameterValueException ex) {
            // This is the case when Uluru handler goes into RUNNING state but image hits terminal state right before cancelling.
            // We consider the image is ready to be deleted and send Uluru handler to the next state.
            logger.log(String.format("The image '%s' has been in terminal state.", model.getArn()));
            callbackContext.setImageWorkflowStatus(IN_TERMINAL_STATE);
            return ProgressEvent.defaultInProgressHandler(
                    callbackContext,
                    1, // callbackDelaySeconds - sooner as it is already in terminal state.
                    model);
        } catch (ImagebuilderException ex) {
            if (ex.statusCode() == 403 || ex.statusCode() == 401) {
                // For users who don't have CancelImageCreation permission.. :(
                return deleteImage(proxy, callbackContext, request);
            } else {
                throw ex;
            }
        }

        return ProgressEvent.defaultInProgressHandler(
                callbackContext,
                10,
                model);
    }

   private ProgressEvent<ResourceModel, CallbackContext> stabilizeImageDeletion(
           final AmazonWebServicesClientProxy proxy,
           final CallbackContext callbackContext,
           final ResourceHandlerRequest<ResourceModel> request) {

       ResourceModel model = request.getDesiredResourceState();
       try {
           Image image = proxy.injectCredentialsAndInvokeV2(RequestUtil.generateGetImageRequest(model),
                   ClientBuilder.getImageBuilderClient()::getImage).image();
           ImageStatus imageStatus = image.state().status();
           if (ImageStatus.CANCELLED.name().equals(imageStatus.name())) {
               callbackContext.setImageWorkflowStatus(IN_TERMINAL_STATE);
               return ProgressEvent.defaultInProgressHandler(
                       callbackContext,
                       1, // callbackDelaySeconds - sooner as it is already in terminal state.
                       model);
           } else {
               return ProgressEvent.defaultInProgressHandler(
                       callbackContext,
                       10,
                       model);
           }
       } catch (ImagebuilderException ex) {
           if (ex.statusCode() == 403 || ex.statusCode() == 401) {
               // Call delete Image regardless due to permissions are missing in customer role.
               return deleteImage(proxy, callbackContext, request);
           } else {
               throw ex;
           }
       }
    }

    private boolean isTerminalStatus(String imageStatus) {
        return TERMINAL_STATES.contains(imageStatus);
    }
}

