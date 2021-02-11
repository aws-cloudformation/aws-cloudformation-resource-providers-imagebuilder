package software.amazon.imagebuilder.image;

import software.amazon.awssdk.services.imagebuilder.model.CreateImageRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteImageRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetImageRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListImagesRequest;

import static software.amazon.imagebuilder.image.Translator.translateToImageBuilderImageTestsConfiguration;


public class RequestUtil {
    static GetImageRequest generateGetImageRequest(final ResourceModel model) {
        return GetImageRequest.builder()
                .imageBuildVersionArn(model.getArn())
                .build();
    }

    static ListImagesRequest generateListImagesRequest(final String nextToken) {
        return ListImagesRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    static DeleteImageRequest generateDeleteImageRequest(final ResourceModel model) {
        return DeleteImageRequest.builder()
                .imageBuildVersionArn(model.getArn())
                .build();
    }

    static CreateImageRequest generateCreateImageRequest(final ResourceModel model) {
        return CreateImageRequest.builder()
                .imageRecipeArn(model.getImageRecipeArn())
                .containerRecipeArn(model.getContainerRecipeArn())
                .distributionConfigurationArn(model.getDistributionConfigurationArn())
                .infrastructureConfigurationArn(model.getInfrastructureConfigurationArn())
                .imageTestsConfiguration(model.getImageTestsConfiguration() == null ?
                        null : translateToImageBuilderImageTestsConfiguration(model.getImageTestsConfiguration()))
                .tags(model.getTags())
                .enhancedImageMetadataEnabled(model.getEnhancedImageMetadataEnabled())
                .build();


    }
}
