package software.amazon.imagebuilder.component;

import software.amazon.awssdk.services.imagebuilder.model.ListComponentBuildVersionsRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListComponentBuildVersionsResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListComponentsResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListImageRecipesRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListImageRecipesResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;

public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ListComponentsResponse response =
                proxy.injectCredentialsAndInvokeV2(RequestUtil.generateListComponentRequest(request.getNextToken()),
                        ClientBuilder.getImageBuilderClient()::listComponents);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(OperationStatus.SUCCESS)
                .resourceModels(Translator.translateForList(response))
                .nextToken(response.nextToken())
                .build();
    }
}
