package software.amazon.imagebuilder.infrastructureconfiguration;

import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.imagebuilder.model.ResourceNotFoundException;
import software.amazon.awssdk.services.imagebuilder.model.UpdateInfrastructureConfigurationResponse;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UpdateHandler extends BaseHandler<CallbackContext> {
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
        final ResourceModel currentModel = request.getDesiredResourceState();
        final ResourceModel previousModel = request.getPreviousResourceState();
        final String arn = previousModel.getArn();

        UpdateInfrastructureConfigurationResponse response;

        try {
            response = proxy.injectCredentialsAndInvokeV2(RequestUtil.generateUpdateInfrastructureConfigurationRequest(arn, currentModel),
                    ClientBuilder.getImageBuilderClient()::updateInfrastructureConfiguration);

            Map<String, String> previousTagMap = previousModel.getTags() == null ? new HashMap<>() : previousModel.getTags();
            Map<String, String> currentTagMap = currentModel.getTags() == null ? new HashMap<>() : currentModel.getTags();

            // No tag/untag operation when no tag in both previous and current model.
            if (previousTagMap.isEmpty() && currentTagMap.isEmpty()) {
                //No operation
            }
            else if (previousTagMap.isEmpty()) {
                // Tag all resource if no tag in previous tag map
                proxy.injectCredentialsAndInvokeV2(RequestUtil.generateTagDistributionConfigurationRequest(
                        arn, currentTagMap),
                        ClientBuilder.getImageBuilderClient()::tagResource);

            } else if (currentTagMap.isEmpty()) {
                // Untag all resource if no tag in current tag map
                List<String> keyList = new LinkedList<>();
                for (Map.Entry<String, String> previousTagMapEntry : previousTagMap.entrySet()) {
                    keyList.add(previousTagMapEntry.getKey());
                }
                proxy.injectCredentialsAndInvokeV2(RequestUtil.generateUntagDistributionConfigurationRequest(arn, keyList),
                        ClientBuilder.getImageBuilderClient()::untagResource);
            } else {
                // Untag all resource tags which are not in the updatedTagMap provided by customer.
                List<String> untagKeyList = new LinkedList<>();
                for (Map.Entry<String,String> previousTagMapEntry : previousTagMap.entrySet()) {
                    if (! currentTagMap.containsKey(previousTagMapEntry.getKey())) {
                        untagKeyList.add(previousTagMapEntry.getKey());
                    }
                }
                if (!untagKeyList.isEmpty()) {
                    proxy.injectCredentialsAndInvokeV2(RequestUtil.generateUntagDistributionConfigurationRequest(arn, untagKeyList),
                            ClientBuilder.getImageBuilderClient()::untagResource);
                }

                // Tag resource with new tags with new keys.
                Map<String, String> tagKeyMap = new HashMap<>();
                for (Map.Entry<String,String> currentTagMapEntry : currentTagMap.entrySet()) {
                    if (! previousTagMap.containsKey(currentTagMapEntry.getKey())) {
                        tagKeyMap.put(currentTagMapEntry.getKey(), currentTagMapEntry.getValue());
                    }
                }
                if (!tagKeyMap.isEmpty()) {
                    proxy.injectCredentialsAndInvokeV2(RequestUtil.generateTagDistributionConfigurationRequest(arn, tagKeyMap),
                            ClientBuilder.getImageBuilderClient()::tagResource);
                }

                // Update Tag when tag keys exist but tag values updated.
                Map<String, String> updateKeyMap = new HashMap<>();
                for (Map.Entry<String,String> currentTagMapEntry : currentTagMap.entrySet()) {
                    String currentTagEntryKey = currentTagMapEntry.getKey();
                    String currentTagEntryValue = currentTagMapEntry.getValue();

                    if (previousTagMap.containsKey(currentTagEntryKey) && ! previousTagMap.get(currentTagEntryKey).equals(currentTagEntryValue)) {
                        updateKeyMap.put(currentTagMapEntry.getKey(), currentTagMapEntry.getValue());
                    }
                }
                if (!updateKeyMap.isEmpty()) {
                    proxy.injectCredentialsAndInvokeV2(RequestUtil.generateTagDistributionConfigurationRequest(arn, updateKeyMap),
                            ClientBuilder.getImageBuilderClient()::tagResource);
                }
            }

            currentModel.setArn(arn);

        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, arn);
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(currentModel)
                .status(OperationStatus.SUCCESS)
                .build();
    }
}
