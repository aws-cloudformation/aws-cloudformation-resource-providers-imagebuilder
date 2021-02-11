package software.amazon.imagebuilder.containerrecipe;

import software.amazon.awssdk.services.imagebuilder.model.CreateContainerRecipeRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteContainerRecipeRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetContainerRecipeRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListContainerRecipesRequest;

import static software.amazon.imagebuilder.containerrecipe.Translator.translateToImageBuilderComponentConfiguration;
import static software.amazon.imagebuilder.containerrecipe.Translator.translateToImageBuilderTargetRepository;

public class RequestUtil {
    static GetContainerRecipeRequest generateReadContainerRecipeRequest(final ResourceModel model) {
        return GetContainerRecipeRequest.builder()
                .containerRecipeArn(model.getArn())
                .build();
    }

    static ListContainerRecipesRequest generateListContainerRecipeRequest(final String nextToken) {
        return ListContainerRecipesRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    static DeleteContainerRecipeRequest generateDeleteContainerRecipeRequest(final ResourceModel model) {
        return DeleteContainerRecipeRequest.builder()
                .containerRecipeArn(model.getArn())
                .build();
    }

    static CreateContainerRecipeRequest generateCreateContainerRecipeRequest(final ResourceModel model) {
        return CreateContainerRecipeRequest.builder()
                .name(model.getName())
                .semanticVersion(model.getVersion())
                .components(translateToImageBuilderComponentConfiguration(model.getComponents()))
                .containerType(model.getContainerType())
                .description(model.getDescription())
                .kmsKeyId(model.getKmsKeyId())
                .dockerfileTemplateData(model.getDockerfileTemplateData())
                .dockerfileTemplateUri(model.getDockerfileTemplateUri())
                .parentImage(model.getParentImage())
                .platformOverride(model.getPlatformOverride())
                .targetRepository(translateToImageBuilderTargetRepository(model.getTargetRepository()))
                .workingDirectory(model.getWorkingDirectory())
                .tags(model.getTags())
                .build();
    }
}
