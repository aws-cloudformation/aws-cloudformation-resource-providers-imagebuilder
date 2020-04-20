package software.amazon.imagebuilder.distributionconfiguration;

import software.amazon.awssdk.services.imagebuilder.model.CreateComponentRequest;
import software.amazon.awssdk.services.imagebuilder.model.CreateDistributionConfigurationRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteComponentRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteDistributionConfigurationRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetComponentRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetDistributionConfigurationRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListComponentsRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListDistributionConfigurationsRequest;
import software.amazon.awssdk.services.imagebuilder.model.TagResourceRequest;
import software.amazon.awssdk.services.imagebuilder.model.UntagResourceRequest;
import software.amazon.awssdk.services.imagebuilder.model.UpdateDistributionConfigurationRequest;

import java.util.List;
import java.util.Map;

import static software.amazon.imagebuilder.distributionconfiguration.Translator.translateToImageBuilderDistributions;

public class RequestUtil {
    static GetDistributionConfigurationRequest generateGetDistributionConfigurationRequest(final ResourceModel model) {
        return GetDistributionConfigurationRequest.builder()
                .distributionConfigurationArn(model.getArn())
                .build();
    }

    static ListDistributionConfigurationsRequest generateListDistributionConfigurationsRequest(final String nextToken) {
        return ListDistributionConfigurationsRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    static DeleteDistributionConfigurationRequest generateDeleteDistributionConfigurationRequest(final ResourceModel model) {
        return DeleteDistributionConfigurationRequest.builder()
                .distributionConfigurationArn(model.getArn())
                .build();
    }

    static CreateDistributionConfigurationRequest generateCreateDistributionConfigurationRequest(final ResourceModel model) {
        return CreateDistributionConfigurationRequest.builder()
                .name(model.getName())
                .description(model.getDescription())
                .distributions(translateToImageBuilderDistributions(model.getDistributions()))
                .tags(model.getTags())
                .build();
    }

    static UpdateDistributionConfigurationRequest generateUpdateDistributionConfigurationRequest(String arn, final ResourceModel currentModel) {
        return UpdateDistributionConfigurationRequest.builder()
                .distributionConfigurationArn(arn)
                .description(currentModel.getDescription())
                .distributions(translateToImageBuilderDistributions(currentModel.getDistributions()))
                .build();
    }

    static TagResourceRequest generateTagDistributionConfigurationRequest(final String arn, final Map<String, String> tag) {
        return TagResourceRequest.builder()
                .resourceArn(arn)
                .tags(tag)
                .build();
    }

    static UntagResourceRequest generateUntagDistributionConfigurationRequest(final String arn, final String tagKey) {
        return UntagResourceRequest.builder()
                .resourceArn(arn)
                .tagKeys(tagKey)
                .build();
    }

    static UntagResourceRequest generateUntagDistributionConfigurationRequest(final String arn, final List<String> tagKeys) {
        return UntagResourceRequest.builder()
                .resourceArn(arn)
                .tagKeys(tagKeys)
                .build();
    }
}
