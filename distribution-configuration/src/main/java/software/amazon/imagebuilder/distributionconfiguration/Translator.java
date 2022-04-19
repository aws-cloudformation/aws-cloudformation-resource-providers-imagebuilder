package software.amazon.imagebuilder.distributionconfiguration;

import software.amazon.awssdk.services.imagebuilder.model.GetDistributionConfigurationResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListDistributionConfigurationsResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Translator {
    private Translator() {}

    static ResourceModel translateForRead(final GetDistributionConfigurationResponse response) {

        return ResourceModel.builder()
                .arn(response.distributionConfiguration().arn())
                .name(response.distributionConfiguration().name())
                .description(response.distributionConfiguration().description())
                .distributions(translateToCfnModelDistributions(response.distributionConfiguration().distributions()))
                .tags(response.distributionConfiguration().tags())
                .build();
    }

    static List<ResourceModel> translateForList(final ListDistributionConfigurationsResponse response) {

        return streamOfOrEmpty(response.distributionConfigurationSummaryList())
                .map(distributionConfigurationSummary -> ResourceModel.builder()
                        .arn(distributionConfigurationSummary.arn())
                        .name(distributionConfigurationSummary.name())
                        .description(distributionConfigurationSummary.description())
                        .tags(distributionConfigurationSummary.tags())
                        .build())
                .collect(Collectors.toList());
    }

    static List<Distribution> translateToCfnModelDistributions(
        final List<software.amazon.awssdk.services.imagebuilder.model.Distribution> imageBuilderDistributions) {

        return streamOfOrEmpty(imageBuilderDistributions)
                .map(cfnModelDistribution -> Distribution.builder()
                        .amiDistributionConfiguration(cfnModelDistribution.amiDistributionConfiguration() == null ?
                                null : translateToCfnModelAmiDistributionConfiguration(cfnModelDistribution.amiDistributionConfiguration()))
                        .containerDistributionConfiguration(cfnModelDistribution.containerDistributionConfiguration() == null ?
                                null : translateToCfnModelContainerDistributionConfiguration(cfnModelDistribution.containerDistributionConfiguration()))
                        .licenseConfigurationArns(cfnModelDistribution.licenseConfigurationArns())
                        .launchTemplateConfigurations(cfnModelDistribution.launchTemplateConfigurations() == null ?
                                null : translateToCfnModelLaunchTemplateConfigurations(cfnModelDistribution.launchTemplateConfigurations()))
                        .region(cfnModelDistribution.region())
                        .build())
                .collect(Collectors.toList());
    }

    static AmiDistributionConfiguration translateToCfnModelAmiDistributionConfiguration(
        final software.amazon.awssdk.services.imagebuilder.model.AmiDistributionConfiguration imageBuilderAmiDistributionConfiguration) {

        return AmiDistributionConfiguration.builder()
                    .name(imageBuilderAmiDistributionConfiguration.name())
                    .description(imageBuilderAmiDistributionConfiguration.description())
                    .amiTags(imageBuilderAmiDistributionConfiguration.amiTags())
                    .kmsKeyId(imageBuilderAmiDistributionConfiguration.kmsKeyId())
                    .targetAccountIds(imageBuilderAmiDistributionConfiguration.targetAccountIds())
                    .launchPermissionConfiguration(imageBuilderAmiDistributionConfiguration.launchPermission() == null ?
                            null : translateToCfnModelLaunchPermission(imageBuilderAmiDistributionConfiguration.launchPermission()))
                    .build();
    }

    static ContainerDistributionConfiguration translateToCfnModelContainerDistributionConfiguration(
            final software.amazon.awssdk.services.imagebuilder.model.ContainerDistributionConfiguration imageBuilderContainerDistributionConfiguration) {

        return ContainerDistributionConfiguration.builder()
                        .description(imageBuilderContainerDistributionConfiguration.description())
                        .containerTags(imageBuilderContainerDistributionConfiguration.containerTags())
                        .targetRepository(translateToCfnModelTargetRepository(imageBuilderContainerDistributionConfiguration.targetRepository()))
                        .build();
    }

    static TargetContainerRepository translateToCfnModelTargetRepository(
            final software.amazon.awssdk.services.imagebuilder.model.TargetContainerRepository targetContainerRepository) {

        return TargetContainerRepository.builder()
                .repositoryName(targetContainerRepository.repositoryName())
                .service(targetContainerRepository.service().toString())
                .build();
    }

    static LaunchPermissionConfiguration translateToCfnModelLaunchPermission(
        final software.amazon.awssdk.services.imagebuilder.model.LaunchPermissionConfiguration imageBuilderLaunchPermission) {

        return LaunchPermissionConfiguration.builder()
                        .userGroups(imageBuilderLaunchPermission.userGroups())
                        .userIds(imageBuilderLaunchPermission.userIds())
                        .organizationArns(imageBuilderLaunchPermission.organizationArns())
                        .organizationalUnitArns(imageBuilderLaunchPermission.organizationalUnitArns())
                        .build();
    }

    static List<software.amazon.awssdk.services.imagebuilder.model.Distribution> translateToImageBuilderDistributions(
        final List<Distribution> cfnModelDistributions) {

        return streamOfOrEmpty(cfnModelDistributions)
                .map(cfnModelDistribution -> software.amazon.awssdk.services.imagebuilder.model.Distribution.builder()
                        .amiDistributionConfiguration(cfnModelDistribution.getAmiDistributionConfiguration() == null ?
                                null : translateToImageBuilderAmiDistributionConfiguration(cfnModelDistribution.getAmiDistributionConfiguration()))
                        .containerDistributionConfiguration(cfnModelDistribution.getContainerDistributionConfiguration() == null ?
                                null : translateToImageBuilderContainerDistributionConfiguration(cfnModelDistribution.getContainerDistributionConfiguration()))
                        .licenseConfigurationArns(cfnModelDistribution.getLicenseConfigurationArns())
                        .launchTemplateConfigurations(cfnModelDistribution.getLaunchTemplateConfigurations() == null ?
                                null : translateToImageBuilderLaunchTemplateConfigurations(cfnModelDistribution.getLaunchTemplateConfigurations()))
                        .region(cfnModelDistribution.getRegion())
                        .build())
                .collect(Collectors.toList());
    }

    static List<software.amazon.awssdk.services.imagebuilder.model.LaunchTemplateConfiguration> translateToImageBuilderLaunchTemplateConfigurations(
            final List<software.amazon.imagebuilder.distributionconfiguration.LaunchTemplateConfiguration> cfnModelLaunchTemplateConfigurations) {

        return streamOfOrEmpty(cfnModelLaunchTemplateConfigurations)
                .map(cfnModelLaunchTemplateConfiguration -> software.amazon.awssdk.services.imagebuilder.model.LaunchTemplateConfiguration.builder()
                        .setDefaultVersion(cfnModelLaunchTemplateConfiguration.getSetDefaultVersion())
                        .launchTemplateId(cfnModelLaunchTemplateConfiguration.getLaunchTemplateId())
                        .accountId(cfnModelLaunchTemplateConfiguration.getAccountId())
                        .build())
                .collect(Collectors.toList());
    }

    static List<software.amazon.imagebuilder.distributionconfiguration.LaunchTemplateConfiguration> translateToCfnModelLaunchTemplateConfigurations(
            final List<software.amazon.awssdk.services.imagebuilder.model.LaunchTemplateConfiguration> imageBuilderLaunchTemplateConfigurations) {

        return streamOfOrEmpty(imageBuilderLaunchTemplateConfigurations)
                .map(imageBuilderLaunchTemplateConfiguration -> software.amazon.imagebuilder.distributionconfiguration.LaunchTemplateConfiguration.builder()
                        .launchTemplateId(imageBuilderLaunchTemplateConfiguration.launchTemplateId())
                        .accountId(imageBuilderLaunchTemplateConfiguration.accountId())
                        .setDefaultVersion(imageBuilderLaunchTemplateConfiguration.setDefaultVersion())
                        .build())
                .collect(Collectors.toList());
    }

    static software.amazon.awssdk.services.imagebuilder.model.AmiDistributionConfiguration translateToImageBuilderAmiDistributionConfiguration(
        final AmiDistributionConfiguration cfnModelAmiDistributionConfiguration) {

        return software.amazon.awssdk.services.imagebuilder.model.AmiDistributionConfiguration.builder()
                .name(cfnModelAmiDistributionConfiguration.getName())
                .description(cfnModelAmiDistributionConfiguration.getDescription())
                .amiTags(cfnModelAmiDistributionConfiguration.getAmiTags())
                .kmsKeyId(cfnModelAmiDistributionConfiguration.getKmsKeyId())
                .targetAccountIds(cfnModelAmiDistributionConfiguration.getTargetAccountIds())
                .launchPermission(cfnModelAmiDistributionConfiguration.getLaunchPermissionConfiguration() == null ?
                        null : translateToImageBuilderLaunchPermission(cfnModelAmiDistributionConfiguration.getLaunchPermissionConfiguration()))
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.ContainerDistributionConfiguration translateToImageBuilderContainerDistributionConfiguration(
            final ContainerDistributionConfiguration cfnModelContainerDistributionConfiguration) {

        return software.amazon.awssdk.services.imagebuilder.model.ContainerDistributionConfiguration.builder()
                .description(cfnModelContainerDistributionConfiguration.getDescription())
                .containerTags(cfnModelContainerDistributionConfiguration.getContainerTags())
                .targetRepository(translateToImageBuilderTargetRepository(cfnModelContainerDistributionConfiguration.getTargetRepository()))
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.TargetContainerRepository translateToImageBuilderTargetRepository(
            final TargetContainerRepository targetRepository) {

        return software.amazon.awssdk.services.imagebuilder.model.TargetContainerRepository.builder()
                .repositoryName(targetRepository == null ? null : targetRepository.getRepositoryName())
                .service(targetRepository == null ? null : targetRepository.getService())
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.LaunchPermissionConfiguration translateToImageBuilderLaunchPermission(
        final LaunchPermissionConfiguration cfnModelLaunchPermission) {

        return software.amazon.awssdk.services.imagebuilder.model.LaunchPermissionConfiguration.builder()
                .userGroups(cfnModelLaunchPermission.getUserGroups())
                .userIds(cfnModelLaunchPermission.getUserIds())
                .organizationArns(cfnModelLaunchPermission.getOrganizationArns())
                .organizationalUnitArns(cfnModelLaunchPermission.getOrganizationalUnitArns())
                .build();
    }


    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}
