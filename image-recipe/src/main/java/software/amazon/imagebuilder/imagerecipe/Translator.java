package software.amazon.imagebuilder.imagerecipe;


import software.amazon.awssdk.services.imagebuilder.model.GetImageRecipeResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListImageRecipesResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Translator {
    private Translator() {}

    static ResourceModel translateForRead(final GetImageRecipeResponse response) {
        return ResourceModel.builder()
                .arn(response.imageRecipe().arn())
                .name(response.imageRecipe().name())
                .version(response.imageRecipe().version())
                .parentImage(response.imageRecipe().parentImage())
                .blockDeviceMappings(translateToCfnModelInstanceBlockDeviceMapping(response.imageRecipe().blockDeviceMappings()))
                .components(translateToCfnModelComponentConfiguration(response.imageRecipe().components()))
                .description(response.imageRecipe().description())
                .tags(response.imageRecipe().tags())
                .workingDirectory(response.imageRecipe().workingDirectory())
                .build();
    }

    static List<ResourceModel> translateForList(final ListImageRecipesResponse response) {
        return streamOfOrEmpty(response.imageRecipeSummaryList())
                .map(imageRecipeSummary -> ResourceModel.builder()
                        .arn(imageRecipeSummary.arn())
                        .name(imageRecipeSummary.name())
                        .parentImage(imageRecipeSummary.parentImage())
                        .tags(imageRecipeSummary.tags())
                        .build())
                .collect(Collectors.toList());
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }

    static List<software.amazon.awssdk.services.imagebuilder.model.InstanceBlockDeviceMapping> translateToImageBuilderInstanceBlockDeviceMapping(
            final List<InstanceBlockDeviceMapping> cfnModelInstanceBlockDeviceMappings) {

        return streamOfOrEmpty(cfnModelInstanceBlockDeviceMappings)
                .map(imageBuilderInstanceBlockDeviceMapping -> software.amazon.awssdk.services.imagebuilder.model.InstanceBlockDeviceMapping.builder()
                        .deviceName(imageBuilderInstanceBlockDeviceMapping.getDeviceName())
                        .ebs(translateToImageBuilderEbs(imageBuilderInstanceBlockDeviceMapping.getEbs()))
                        .noDevice(imageBuilderInstanceBlockDeviceMapping.getNoDevice())
                        .virtualName(imageBuilderInstanceBlockDeviceMapping.getVirtualName())
                        .build())
                .collect(Collectors.toList());
    }

    static List<InstanceBlockDeviceMapping> translateToCfnModelInstanceBlockDeviceMapping(
            final List<software.amazon.awssdk.services.imagebuilder.model.InstanceBlockDeviceMapping> imageBuilderInstanceBlockDeviceMappings) {

        return streamOfOrEmpty(imageBuilderInstanceBlockDeviceMappings)
                .map(cfnModelInstanceBlockDeviceMapping -> InstanceBlockDeviceMapping.builder()
                        .deviceName(cfnModelInstanceBlockDeviceMapping.deviceName())
                        .ebs(translateToCfnModelEbs(cfnModelInstanceBlockDeviceMapping.ebs()))
                        .noDevice(cfnModelInstanceBlockDeviceMapping.noDevice())
                        .virtualName(cfnModelInstanceBlockDeviceMapping.virtualName())
                        .build())
                .collect(Collectors.toList());
    }

    static List<software.amazon.awssdk.services.imagebuilder.model.ComponentConfiguration> translateToImageBuilderComponentConfiguration(
            final List<ComponentConfiguration> cfnModelComponentConfigurations) {

        return streamOfOrEmpty(cfnModelComponentConfigurations)
                .map(imageBuilderInstanceBlockDeviceMapping -> software.amazon.awssdk.services.imagebuilder.model.ComponentConfiguration.builder()
                        .componentArn(imageBuilderInstanceBlockDeviceMapping.getComponentArn())
                        .build())
                .collect(Collectors.toList());
    }

    static List<ComponentConfiguration> translateToCfnModelComponentConfiguration(
            final List<software.amazon.awssdk.services.imagebuilder.model.ComponentConfiguration> imageBuilderComponentConfigurations) {

        return streamOfOrEmpty(imageBuilderComponentConfigurations)
                .map(cfnModelInstanceBlockDeviceMapping -> ComponentConfiguration.builder()
                        .componentArn(cfnModelInstanceBlockDeviceMapping.componentArn())
                        .build())
                .collect(Collectors.toList());
    }

    static EbsInstanceBlockDeviceSpecification translateToCfnModelEbs(
            final software.amazon.awssdk.services.imagebuilder.model.EbsInstanceBlockDeviceSpecification imageBuilderEbs) {

        return EbsInstanceBlockDeviceSpecification.builder()
                .snapshotId(imageBuilderEbs.snapshotId())
                .kmsKeyId(imageBuilderEbs.kmsKeyId())
                .encrypted(imageBuilderEbs.encrypted())
                .iops(imageBuilderEbs.iops())
                .deleteOnTermination(imageBuilderEbs.deleteOnTermination())
                .volumeType(imageBuilderEbs.volumeType() == null ? null : imageBuilderEbs.volumeType().name())
                .volumeSize(imageBuilderEbs.volumeSize())
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.EbsInstanceBlockDeviceSpecification translateToImageBuilderEbs(
            final EbsInstanceBlockDeviceSpecification imageBuilderEbs) {

        return software.amazon.awssdk.services.imagebuilder.model.EbsInstanceBlockDeviceSpecification.builder()
                .snapshotId(imageBuilderEbs.getSnapshotId())
                .kmsKeyId(imageBuilderEbs.getKmsKeyId())
                .encrypted(imageBuilderEbs.getEncrypted())
                .iops(imageBuilderEbs.getIops())
                .deleteOnTermination(imageBuilderEbs.getDeleteOnTermination())
                .volumeType(imageBuilderEbs.getVolumeType())
                .volumeSize(imageBuilderEbs.getVolumeSize())
                .build();
    }
}
