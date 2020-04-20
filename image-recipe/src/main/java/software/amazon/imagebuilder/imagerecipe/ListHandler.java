package software.amazon.imagebuilder.imagerecipe;

import software.amazon.awssdk.services.imagebuilder.model.ListImageRecipesResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;


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
        final ListImageRecipesResponse response =
                proxy.injectCredentialsAndInvokeV2(RequestUtil.generateListImageRecipeRequest(request.getNextToken()),
                        ClientBuilder.getImageBuilderClient()::listImageRecipes);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(OperationStatus.SUCCESS)
                .resourceModels(Translator.translateForList(response))
                .nextToken(response.nextToken())
                .build();
    }
}