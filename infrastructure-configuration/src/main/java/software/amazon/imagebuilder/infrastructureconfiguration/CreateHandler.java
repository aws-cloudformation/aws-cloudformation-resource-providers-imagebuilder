package software.amazon.imagebuilder.infrastructureconfiguration;

import software.amazon.awssdk.services.imagebuilder.model.CreateInfrastructureConfigurationResponse;
import software.amazon.awssdk.services.imagebuilder.model.InvalidParameterException;
import software.amazon.awssdk.services.imagebuilder.model.ResourceAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
        final ResourceModel model = request.getDesiredResourceState();

        CreateInfrastructureConfigurationResponse response;
        try {
            response = proxy.injectCredentialsAndInvokeV2(RequestUtil.generateCreateInfrastructureConfigurationRequest(model),
                    ClientBuilder.getImageBuilderClient()::createInfrastructureConfiguration);
        } catch (final ResourceAlreadyExistsException e) {
            logger.log(e.getMessage());
            throw new CfnAlreadyExistsException(ResourceModel.TYPE_NAME, "InfrastructureConfigurationArn");
        } catch (final InvalidParameterException e) {
            logger.log(e.getMessage());
            throw new CfnInvalidRequestException(ResourceModel.TYPE_NAME);
        }

        model.setArn(response.infrastructureConfigurationArn());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(model)
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
