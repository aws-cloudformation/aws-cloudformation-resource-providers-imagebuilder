package software.amazon.imagebuilder.infrastructureconfiguration;

import software.amazon.awssdk.services.imagebuilder.model.CreateInfrastructureConfigurationRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteInfrastructureConfigurationRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetInfrastructureConfigurationRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListInfrastructureConfigurationsRequest;
import software.amazon.awssdk.services.imagebuilder.model.TagResourceRequest;
import software.amazon.awssdk.services.imagebuilder.model.UntagResourceRequest;
import software.amazon.awssdk.services.imagebuilder.model.UpdateInfrastructureConfigurationRequest;

import java.util.List;
import java.util.Map;

import static software.amazon.imagebuilder.infrastructureconfiguration.Translator.translateToImageBuilderInstanceMetadataOptions;
import static software.amazon.imagebuilder.infrastructureconfiguration.Translator.translateToImageBuilderLogging;

public class RequestUtil {
    static GetInfrastructureConfigurationRequest generateGetInfrastructureConfigurationRequest(final ResourceModel model) {
        return GetInfrastructureConfigurationRequest.builder()
                .infrastructureConfigurationArn(model.getArn())
                .build();
    }

    static ListInfrastructureConfigurationsRequest generateListInfrastructureConfigurationRequest(final String nextToken) {
        return ListInfrastructureConfigurationsRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    static DeleteInfrastructureConfigurationRequest generateDeleteInfrastructureConfigurationRequest(final ResourceModel model) {
        return DeleteInfrastructureConfigurationRequest.builder()
                .infrastructureConfigurationArn(model.getArn())
                .build();
    }

    static CreateInfrastructureConfigurationRequest generateCreateInfrastructureConfigurationRequest(final ResourceModel model) {
        return CreateInfrastructureConfigurationRequest.builder()
                .name(model.getName())
                .description(model.getDescription())
                .instanceProfileName(model.getInstanceProfileName())
                .keyPair(model.getKeyPair())
                .logging(translateToImageBuilderLogging(model.getLogging()))
                .instanceTypes(model.getInstanceTypes())
                .snsTopicArn(model.getSnsTopicArn())
                .securityGroupIds(model.getSecurityGroupIds())
                .terminateInstanceOnFailure(model.getTerminateInstanceOnFailure())
                .subnetId(model.getSubnetId())
                .tags(model.getTags())
                .resourceTags(model.getResourceTags())
                .instanceMetadataOptions(translateToImageBuilderInstanceMetadataOptions(model.getInstanceMetadataOptions()))
                .build();
    }

    static UpdateInfrastructureConfigurationRequest generateUpdateInfrastructureConfigurationRequest(String arn, final ResourceModel model) {
        return UpdateInfrastructureConfigurationRequest.builder()
                .infrastructureConfigurationArn(arn)
                .instanceProfileName(model.getInstanceProfileName())
                .description(model.getDescription())
                .keyPair(model.getKeyPair())
                .logging(translateToImageBuilderLogging(model.getLogging()))
                .instanceTypes(model.getInstanceTypes())
                .snsTopicArn(model.getSnsTopicArn())
                .securityGroupIds(model.getSecurityGroupIds())
                .terminateInstanceOnFailure(model.getTerminateInstanceOnFailure())
                .resourceTags(model.getResourceTags())
                .subnetId(model.getSubnetId())
                .instanceMetadataOptions(translateToImageBuilderInstanceMetadataOptions(model.getInstanceMetadataOptions()))
                .build();
    }

    static TagResourceRequest generateTagInfrastructureConfigurationRequest(final String arn, final Map<String, String> tag) {
        return TagResourceRequest.builder()
                .resourceArn(arn)
                .tags(tag)
                .build();
    }

    static UntagResourceRequest generateUntagInfrastructureConfigurationRequest(final String arn, final List<String> tagKey) {
        return UntagResourceRequest.builder()
                .resourceArn(arn)
                .tagKeys(tagKey)
                .build();
    }
}
