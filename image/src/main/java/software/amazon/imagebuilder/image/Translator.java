package software.amazon.imagebuilder.image;

import software.amazon.awssdk.services.imagebuilder.model.GetImageResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListImagesResponse;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Translator {
    private Translator() {}

    static ResourceModel translateForRead(final GetImageResponse response, String currentRegion) {

        return ResourceModel.builder()
                .arn(response.image().arn())
                .imageRecipeArn(response.image().imageRecipe() == null ? null : response.image().imageRecipe().arn())
                .infrastructureConfigurationArn(response.image().infrastructureConfiguration() == null ?
                        null : response.image().infrastructureConfiguration().arn())
                .distributionConfigurationArn(response.image().distributionConfiguration() == null ?
                        null : response.image().distributionConfiguration().arn())
                .imageTestsConfiguration(translateToCfnModelImageTestsConfiguration(response.image().imageTestsConfiguration()))
                .outputResources(translateToCfnModelOutputResources(response.image().outputResources()))
                .imageId(translateToCfnModelImageId(response.image().outputResources(), currentRegion))
                .tags(response.image().tags())
                .build();
    }

    static List<ResourceModel> translateForList(final ListImagesResponse response) {

        return streamOfOrEmpty(response.imageVersionList())
                .map(imageVersion -> ResourceModel.builder()
                        .arn(imageVersion.arn())
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

    static OutputResources translateToCfnModelOutputResources(
            final software.amazon.awssdk.services.imagebuilder.model.OutputResources imageBuilderOutputResources) {

        return OutputResources.builder()
                .amis(imageBuilderOutputResources == null ? null : translateToCfnModelAmis(imageBuilderOutputResources.amis()))
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.OutputResources translateToImageBuilderOutputResources(
            final OutputResources cfnModelOutputResources) {

        return software.amazon.awssdk.services.imagebuilder.model.OutputResources.builder()
                .amis(cfnModelOutputResources == null ? null : translateToImageBuilderAmis(cfnModelOutputResources.getAmis()))
                .build();
    }

    static List<Ami> translateToCfnModelAmis(
            final List<software.amazon.awssdk.services.imagebuilder.model.Ami> imageBuilderAmis) {

        return streamOfOrEmpty(imageBuilderAmis)
                .map(imageBuilderAmi -> Ami.builder()
                        .image(imageBuilderAmi.image())
                        .name(imageBuilderAmi.name())
                        .description(imageBuilderAmi.description())
                        .region(imageBuilderAmi.region())
                        .state(translateToCfnModelImageState(imageBuilderAmi.state()))
                        .build())
                .collect(Collectors.toList());
    }

    static List<software.amazon.awssdk.services.imagebuilder.model.Ami> translateToImageBuilderAmis(
            final List<Ami> cfnModelAmis) {

        return streamOfOrEmpty(cfnModelAmis)
                .map(cfnModelAmi -> software.amazon.awssdk.services.imagebuilder.model.Ami.builder()
                        .image(cfnModelAmi.getImage())
                        .name(cfnModelAmi.getName())
                        .description(cfnModelAmi.getDescription())
                        .region(cfnModelAmi.getRegion())
                        .state(translateToImageBuilderImageState(cfnModelAmi.getState()))
                        .build())
                .collect(Collectors.toList());
    }

    static ImageState translateToCfnModelImageState(
            final software.amazon.awssdk.services.imagebuilder.model.ImageState imageBuilderImageState) {

        return ImageState.builder()
                .reason(imageBuilderImageState == null ? null : imageBuilderImageState.reason())
                .status(imageBuilderImageState == null ? null : imageBuilderImageState.status().name())
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.ImageState translateToImageBuilderImageState(
            final ImageState cfnModelImageState) {

        return software.amazon.awssdk.services.imagebuilder.model.ImageState.builder()
                .reason(cfnModelImageState.getReason())
                .status(cfnModelImageState.getStatus())
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

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}
