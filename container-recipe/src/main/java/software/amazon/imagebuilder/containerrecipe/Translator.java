package software.amazon.imagebuilder.containerrecipe;


import software.amazon.awssdk.services.imagebuilder.model.GetContainerRecipeResponse;
import software.amazon.awssdk.services.imagebuilder.model.InstanceBlockDeviceMapping;
import software.amazon.awssdk.services.imagebuilder.model.InstanceConfiguration;
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
                .instanceConfiguration(translateToCfnModelInstanceConfiguration(response.containerRecipe().instanceConfiguration()))
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

    static software.amazon.awssdk.services.imagebuilder.model.InstanceConfiguration translateToImageBuilderInstanceConfiguration(
            final software.amazon.imagebuilder.containerrecipe.InstanceConfiguration cfnModelInstanceConfiguration) {

            return InstanceConfiguration.builder()
                    .image(cfnModelInstanceConfiguration == null ? null : cfnModelInstanceConfiguration.getImage())
                    .blockDeviceMappings(cfnModelInstanceConfiguration == null ? null :
                            translateToImageBuilderInstanceBlockDeviceMappings(cfnModelInstanceConfiguration.getBlockDeviceMappings()))
                    .build();
    }

    static software.amazon.imagebuilder.containerrecipe.InstanceConfiguration translateToCfnModelInstanceConfiguration(
            final software.amazon.awssdk.services.imagebuilder.model.InstanceConfiguration imageBuilderInstanceConfiguration) {

        return software.amazon.imagebuilder.containerrecipe.InstanceConfiguration.builder()
                .image(imageBuilderInstanceConfiguration.image())
                .blockDeviceMappings(translateToCfnModelInstanceBlockDeviceMappings(imageBuilderInstanceConfiguration.blockDeviceMappings()))
                .build();
    }

    static List<software.amazon.awssdk.services.imagebuilder.model.InstanceBlockDeviceMapping> translateToImageBuilderInstanceBlockDeviceMappings(
            final List<software.amazon.imagebuilder.containerrecipe.InstanceBlockDeviceMapping> cfnModelInstanceBlockDeviceMappings) {

        return streamOfOrEmpty(cfnModelInstanceBlockDeviceMappings)
                .map(imageBuilderInstanceBlockDeviceMapping -> software.amazon.awssdk.services.imagebuilder.model.InstanceBlockDeviceMapping.builder()
                        .deviceName(imageBuilderInstanceBlockDeviceMapping.getDeviceName())
                        .ebs(translateToImageBuilderEbs(imageBuilderInstanceBlockDeviceMapping.getEbs()))
                        .noDevice(imageBuilderInstanceBlockDeviceMapping.getNoDevice())
                        .virtualName(imageBuilderInstanceBlockDeviceMapping.getVirtualName())
                        .build())
                .collect(Collectors.toList());
    }

    static List<software.amazon.imagebuilder.containerrecipe.InstanceBlockDeviceMapping> translateToCfnModelInstanceBlockDeviceMappings(
            final List<software.amazon.awssdk.services.imagebuilder.model.InstanceBlockDeviceMapping> imageBuilderInstanceBlockDeviceMappings) {

        return streamOfOrEmpty(imageBuilderInstanceBlockDeviceMappings)
                .map(cfnModelInstanceBlockDeviceMapping -> software.amazon.imagebuilder.containerrecipe.InstanceBlockDeviceMapping.builder()
                        .deviceName(cfnModelInstanceBlockDeviceMapping.deviceName())
                        .ebs(translateToCfnModelEbs(cfnModelInstanceBlockDeviceMapping.ebs()))
                        .noDevice(cfnModelInstanceBlockDeviceMapping.noDevice())
                        .virtualName(cfnModelInstanceBlockDeviceMapping.virtualName())
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
                .throughput(imageBuilderEbs.throughput())
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.EbsInstanceBlockDeviceSpecification translateToImageBuilderEbs(
            final EbsInstanceBlockDeviceSpecification cfnEbs) {

        return software.amazon.awssdk.services.imagebuilder.model.EbsInstanceBlockDeviceSpecification.builder()
                .snapshotId(cfnEbs.getSnapshotId())
                .kmsKeyId(cfnEbs.getKmsKeyId())
                .encrypted(cfnEbs.getEncrypted())
                .iops(cfnEbs.getIops())
                .deleteOnTermination(cfnEbs.getDeleteOnTermination())
                .volumeType(cfnEbs.getVolumeType())
                .volumeSize(cfnEbs.getVolumeSize())
                .throughput(cfnEbs.getThroughput())
                .build();
    }
}
