package software.amazon.imagebuilder.image;

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        // propagate input values and model to make CFN happy.
        // https://sim.amazon.com/issues/ULURU-2208
        // https://t.corp.amazon.com/P40137211/
        final ResourceModel result = ResourceModel.builder()
                .arn(model.getArn())
                .build();

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(result)
                .status(OperationStatus.SUCCESS)
                .build();
    }
}