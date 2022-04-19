package software.amazon.imagebuilder.imagerecipe;

import software.amazon.awssdk.services.imagebuilder.model.CreateImageRecipeRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteImageRecipeRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetImageRecipeRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListImageRecipesRequest;

import static software.amazon.imagebuilder.imagerecipe.Translator.translateToImageBuilderComponentConfiguration;
import static software.amazon.imagebuilder.imagerecipe.Translator.translateToImageBuilderInstanceBlockDeviceMapping;
import static software.amazon.imagebuilder.imagerecipe.Translator.translateToImageBuilderModelAdditionalInstanceConfiguration;

public class RequestUtil {
    static GetImageRecipeRequest generateReadImageRecipeRequest(final ResourceModel model) {
        return GetImageRecipeRequest.builder()
                .imageRecipeArn(model.getArn())
                .build();
    }

    static ListImageRecipesRequest generateListImageRecipeRequest(final String nextToken) {
        return ListImageRecipesRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    static DeleteImageRecipeRequest generateDeleteImageRecipeRequest(final ResourceModel model) {
        return DeleteImageRecipeRequest.builder()
                .imageRecipeArn(model.getArn())
                .build();
    }

    static CreateImageRecipeRequest generateCreateImageRecipeRequest(final ResourceModel model) {
        return CreateImageRecipeRequest.builder()
                .name(model.getName())
                .semanticVersion(model.getVersion())
                .description(model.getDescription())
                .components(translateToImageBuilderComponentConfiguration(model.getComponents()))
                .blockDeviceMappings(translateToImageBuilderInstanceBlockDeviceMapping(model.getBlockDeviceMappings()))
                .additionalInstanceConfiguration(model.getAdditionalInstanceConfiguration() == null ?
                        null : translateToImageBuilderModelAdditionalInstanceConfiguration(model.getAdditionalInstanceConfiguration()))
                .parentImage(model.getParentImage())
                .workingDirectory(model.getWorkingDirectory())
                .tags(model.getTags())
                .build();
    }
}
