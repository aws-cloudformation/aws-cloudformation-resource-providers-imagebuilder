package software.amazon.imagebuilder.containerrecipe;


import software.amazon.awssdk.services.imagebuilder.model.GetContainerRecipeResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListContainerRecipesResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// coral model - https://code.amazon.com/packages/Ec2ImageBuilderCommonModelTypes/blobs/mainline/--/model/api/types/container-recipe.xml
public class Translator {
    private Translator() {}

    static ResourceModel translateForRead(final GetContainerRecipeResponse response) {
        return ResourceModel.builder()
                .arn(response.containerRecipe().arn())
                .name(response.containerRecipe().name())
                .version(response.containerRecipe().version())
                .parentImage(response.containerRecipe().parentImage())
                .components(translateToCfnModelComponentConfiguration(response.containerRecipe().components()))
                .containerType(response.containerRecipe().containerTypeAsString())
                .targetRepository(translateToCfnModelTargetRepository(response.containerRecipe().targetRepository()))
                .kmsKeyId(response.containerRecipe().kmsKeyId())
                .platformOverride(response.containerRecipe().platformAsString())
                .description(response.containerRecipe().description())
                .tags(response.containerRecipe().tags())
                .workingDirectory(response.containerRecipe().workingDirectory())
                .dockerfileTemplateData(response.containerRecipe().dockerfileTemplateData())
                .build();
    }

    static List<ResourceModel> translateForList(final ListContainerRecipesResponse response) {
        return streamOfOrEmpty(response.containerRecipeSummaryList())
                .map(containerRecipeSummary -> ResourceModel.builder()
                        .arn(containerRecipeSummary.arn())
                        .name(containerRecipeSummary.name())
                        .parentImage(containerRecipeSummary.parentImage())
                        .tags(containerRecipeSummary.tags())
                        .build())
                .collect(Collectors.toList());
    }

        private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
            return Optional.ofNullable(collection)
                    .map(Collection::stream)
                    .orElseGet(Stream::empty);
        }

    static List<software.amazon.awssdk.services.imagebuilder.model.ComponentConfiguration> translateToImageBuilderComponentConfiguration(
            final List<ComponentConfiguration> cfnModelComponentConfigurations) {

        return streamOfOrEmpty(cfnModelComponentConfigurations)
                .map(componentConfiguration -> software.amazon.awssdk.services.imagebuilder.model.ComponentConfiguration.builder()
                        .componentArn(componentConfiguration.getComponentArn())
                        .build())
                .collect(Collectors.toList());
    }

    static TargetContainerRepository translateToCfnModelTargetRepository(
            final software.amazon.awssdk.services.imagebuilder.model.TargetContainerRepository targetContainerRepository) {

        return TargetContainerRepository.builder()
                .repositoryName(targetContainerRepository.repositoryName())
                .service(targetContainerRepository.service().toString())
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.TargetContainerRepository translateToImageBuilderTargetRepository(
            final TargetContainerRepository targetRepository) {

        return software.amazon.awssdk.services.imagebuilder.model.TargetContainerRepository.builder()
                .repositoryName(targetRepository == null ? null : targetRepository.getRepositoryName())
                .service(targetRepository == null ? null : targetRepository.getService())
                .build();
    }

    static List<ComponentConfiguration> translateToCfnModelComponentConfiguration(
            final List<software.amazon.awssdk.services.imagebuilder.model.ComponentConfiguration> imageBuilderComponentConfigurations) {

        return streamOfOrEmpty(imageBuilderComponentConfigurations)
                .map(cfnModelComponent -> ComponentConfiguration.builder()
                        .componentArn(cfnModelComponent.componentArn())
                        .build())
                .collect(Collectors.toList());
    }
}
