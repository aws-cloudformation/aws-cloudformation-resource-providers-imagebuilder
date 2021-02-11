package software.amazon.imagebuilder.imagepipeline;


import software.amazon.awssdk.services.imagebuilder.model.CreateImagePipelineRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteImagePipelineRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetImagePipelineRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListImagePipelinesRequest;
import software.amazon.awssdk.services.imagebuilder.model.TagResourceRequest;
import software.amazon.awssdk.services.imagebuilder.model.UntagResourceRequest;
import software.amazon.awssdk.services.imagebuilder.model.UpdateImagePipelineRequest;

import java.util.List;
import java.util.Map;

import static software.amazon.imagebuilder.imagepipeline.Translator.translateToImageBuilderImageTestsConfiguration;
import static software.amazon.imagebuilder.imagepipeline.Translator.translateToImageBuilderSchedule;

public class RequestUtil {
    static GetImagePipelineRequest generateGetImagePipelineRequest(final ResourceModel model) {

        return GetImagePipelineRequest.builder()
                .imagePipelineArn(model.getArn())
                .build();
    }

    static ListImagePipelinesRequest generateListImagePipelinesRequest(final String nextToken) {
        return ListImagePipelinesRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    static DeleteImagePipelineRequest generateDeleteImagePipelineRequest(final ResourceModel model) {
        return DeleteImagePipelineRequest.builder()
                .imagePipelineArn(model.getArn())
                .build();
    }

    static CreateImagePipelineRequest generateCreateImagePipelineRequest(final ResourceModel model) {
        return CreateImagePipelineRequest.builder()
                .name(model.getName())
                .description(model.getDescription())
                .imageRecipeArn(model.getImageRecipeArn())
                .containerRecipeArn(model.getContainerRecipeArn())
                .distributionConfigurationArn(model.getDistributionConfigurationArn())
                .infrastructureConfigurationArn(model.getInfrastructureConfigurationArn())
                .imageTestsConfiguration(model.getImageTestsConfiguration() == null ?
                        null : translateToImageBuilderImageTestsConfiguration(model.getImageTestsConfiguration()))
                .schedule(model.getSchedule() == null ?
                        null : translateToImageBuilderSchedule(model.getSchedule()))
                .status(model.getStatus())
                .tags(model.getTags())
                .enhancedImageMetadataEnabled(model.getEnhancedImageMetadataEnabled())
                .build();
    }

    static UpdateImagePipelineRequest generateUpdateImagePipelineRequest(String arn, final ResourceModel model) {
        return UpdateImagePipelineRequest.builder()
                .imagePipelineArn(arn)
                .imageRecipeArn(model.getImageRecipeArn())
                .containerRecipeArn(model.getContainerRecipeArn())
                .infrastructureConfigurationArn(model.getInfrastructureConfigurationArn())
                .distributionConfigurationArn(model.getDistributionConfigurationArn())
                .imageTestsConfiguration(translateToImageBuilderImageTestsConfiguration(model.getImageTestsConfiguration()))
                .description(model.getDescription())
                .schedule(translateToImageBuilderSchedule(model.getSchedule()))
                .enhancedImageMetadataEnabled(model.getEnhancedImageMetadataEnabled())
                .status(model.getStatus())
                .build();
    }

    static TagResourceRequest generateTagImagePipelineRequest(final String arn, final Map<String, String> tag) {
        return TagResourceRequest.builder()
                .resourceArn(arn)
                .tags(tag)
                .build();
    }

    static UntagResourceRequest generateUntagImagePipelineRequest(final String arn, final List<String> tagKeys) {
        return UntagResourceRequest.builder()
                .resourceArn(arn)
                .tagKeys(tagKeys)
                .build();
    }
}
