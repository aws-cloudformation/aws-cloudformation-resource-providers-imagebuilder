package software.amazon.imagebuilder.image;

import software.amazon.awssdk.services.imagebuilder.model.GetImageResponse;
import software.amazon.awssdk.services.imagebuilder.model.ImageType;
import software.amazon.awssdk.services.imagebuilder.model.ListImageBuildVersionsResponse;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Translator {
    private Translator() {}

    static ResourceModel translateForRead(final GetImageResponse response, String currentRegion) {
        final ResourceModel.ResourceModelBuilder modelBuilder = ResourceModel.builder()
                .arn(response.image().arn())
                .imageRecipeArn(response.image().imageRecipe() == null ? null : response.image().imageRecipe().arn())
                .containerRecipeArn(response.image().containerRecipe() == null ? null : response.image().containerRecipe().arn())
                .infrastructureConfigurationArn(response.image().infrastructureConfiguration() == null ?
                        null : response.image().infrastructureConfiguration().arn())
                .distributionConfigurationArn(response.image().distributionConfiguration() == null ?
                        null : response.image().distributionConfiguration().arn())
                .imageTestsConfiguration(translateToCfnModelImageTestsConfiguration(response.image().imageTestsConfiguration()))
                .name(response.image().name())
                .tags(response.image().tags())
                .enhancedImageMetadataEnabled(response.image().enhancedImageMetadataEnabled());

        if (ImageType.DOCKER.equals(response.image().type())) {
            return modelBuilder
                    .imageUri(translateToCfnModelImageUri(response.image().outputResources(), response.image().version(), currentRegion))
                    .build();
        }

        return modelBuilder
                .imageId(translateToCfnModelImageId(response.image().outputResources(), currentRegion))
                .build();
    }

    static List<ResourceModel> translateForList(final ListImageBuildVersionsResponse response) {

        return streamOfOrEmpty(response.imageSummaryList())
                .map(imageVersion -> ResourceModel.builder()
                        .arn(imageVersion.arn())
                        .name(imageVersion.name())
                        .build())
                .collect(Collectors.toList());
    }

    static ImageTestsConfiguration translateToCfnModelImageTestsConfiguration(
            final software.amazon.awssdk.services.imagebuilder.model.ImageTestsConfiguration imageBuilderImageTestsConfiguration) {

        return ImageTestsConfiguration.builder()
                .imageTestsEnabled(imageBuilderImageTestsConfiguration == null ? null :imageBuilderImageTestsConfiguration.imageTestsEnabled())
                .timeoutMinutes((imageBuilderImageTestsConfiguration == null ? null :imageBuilderImageTestsConfiguration.timeoutMinutes()))
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.ImageTestsConfiguration translateToImageBuilderImageTestsConfiguration(
            final ImageTestsConfiguration cfnModelImageTestsConfiguration) {

        return software.amazon.awssdk.services.imagebuilder.model.ImageTestsConfiguration.builder()
                .imageTestsEnabled(cfnModelImageTestsConfiguration == null ? null : cfnModelImageTestsConfiguration.getImageTestsEnabled())
                .timeoutMinutes(cfnModelImageTestsConfiguration == null ? null : cfnModelImageTestsConfiguration.getTimeoutMinutes())
                .build();
    }

    static String translateToCfnModelImageId (final software.amazon.awssdk.services.imagebuilder.model.OutputResources outputResources,
                                              String currentRegion) {
        List<software.amazon.awssdk.services.imagebuilder.model.Ami> amis = outputResources.amis();

        for (software.amazon.awssdk.services.imagebuilder.model.Ami ami : amis) {
            if (ami.region().equals(currentRegion)) {
                // Return the Image Id in Current Region
                return ami.image();
            }
        }

        throw new CfnNotFoundException(ResourceModel.TYPE_NAME, "Not able to find the image id in current region.");
    }

    static String translateToCfnModelImageUri(final software.amazon.awssdk.services.imagebuilder.model.OutputResources outputResources,
                                              final String imageVersion,
                                              final String currentRegion) {
        final List<software.amazon.awssdk.services.imagebuilder.model.Container> containers = outputResources.containers();
        final String ecrBuildVersionImageTag = imageVersion.replace("/", "-");

        for (final software.amazon.awssdk.services.imagebuilder.model.Container container : containers) {
            if (container.region().equals(currentRegion)) {
                for (final String imageUri : container.imageUris()) {
                    // Return the default Image URI in Current Region
                    if (imageUri.endsWith(ecrBuildVersionImageTag)) {
                        return imageUri;
                    }
                }
            }
        }

        throw new CfnNotFoundException(ResourceModel.TYPE_NAME, "Unable to find the default Container Image URI in the current region.");
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}
