package software.amazon.imagebuilder.image;

import software.amazon.awssdk.services.imagebuilder.model.ListComponentBuildVersionsResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListImageBuildVersionsResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListImagesResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandler<CallbackContext> {
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
        final ListImageBuildVersionsResponse response =
                proxy.injectCredentialsAndInvokeV2(RequestUtil.generateListImageBuilderVersions(model, request.getNextToken()),
                        ClientBuilder.getImageBuilderClient()::listImageBuildVersions);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(OperationStatus.SUCCESS)
                .resourceModels(Translator.translateForList(response))
                .nextToken(response.nextToken())
                .build();
    }
}
