package software.amazon.imagebuilder.image;

import software.amazon.awssdk.services.imagebuilder.model.CancelImageCreationRequest;
import software.amazon.awssdk.services.imagebuilder.model.CreateImageRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteImageRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetImageRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListComponentBuildVersionsRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListImageBuildVersionsRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListImageBuildVersionsResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListImagesRequest;

import static software.amazon.imagebuilder.image.Translator.translateToImageBuilderImageTestsConfiguration;


public class RequestUtil {
    static GetImageRequest generateGetImageRequest(final ResourceModel model) {
        return GetImageRequest.builder()
                .imageBuildVersionArn(model.getArn())
                .build();
    }

    static ListImageBuildVersionsRequest generateListImageBuilderVersions(final ResourceModel model, final String nextToken) {
        String arn = model.getArn();
        String[] arnPartList = arn.split("/");
        StringBuilder imageVersionArn = new StringBuilder();
        for (int i = 0; i < arnPartList.length - 1; i++) {
            //re-generate image version arn and get rid of build version.
            imageVersionArn.append(arnPartList[i]);
            if (i != arnPartList.length - 2) imageVersionArn.append("/");
        }

        return ListImageBuildVersionsRequest.builder()
                .imageVersionArn(imageVersionArn.toString()) // remove the last "/"
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

    static CancelImageCreationRequest generateCancelImageCreationRequest(final ResourceModel model) {
        return CancelImageCreationRequest.builder()
                .imageBuildVersionArn(model.getArn())
                .build();
    }
}
