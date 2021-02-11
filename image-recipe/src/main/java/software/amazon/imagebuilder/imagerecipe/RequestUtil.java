package software.amazon.imagebuilder.imagerecipe;

import software.amazon.awssdk.services.imagebuilder.model.CreateImageRecipeRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteImageRecipeRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetImageRecipeRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListImageRecipesRequest;

import static software.amazon.imagebuilder.imagerecipe.Translator.translateToImageBuilderComponentConfiguration;
import static software.amazon.imagebuilder.imagerecipe.Translator.translateToImageBuilderInstanceBlockDeviceMapping;

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
                .parentImage(model.getParentImage())
                .tags(model.getTags())
                .workingDirectory(model.getWorkingDirectory())
                .build();
    }
}
