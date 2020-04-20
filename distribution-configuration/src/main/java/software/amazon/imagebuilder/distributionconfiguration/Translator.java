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
                        .licenseConfigurationArns(cfnModelDistribution.licenseConfigurationArns())
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
                    .launchPermissionConfiguration(imageBuilderAmiDistributionConfiguration.launchPermission() == null ?
                            null : translateToCfnModelLaunchPermission(imageBuilderAmiDistributionConfiguration.launchPermission()))
                    .build();
    }

    static LaunchPermissionConfiguration translateToCfnModelLaunchPermission(
        final software.amazon.awssdk.services.imagebuilder.model.LaunchPermissionConfiguration imageBuilderLaunchPermission) {

        return LaunchPermissionConfiguration.builder()
                        .userGroups(imageBuilderLaunchPermission.userGroups())
                        .userIds(imageBuilderLaunchPermission.userIds())
                        .build();
    }

    static List<software.amazon.awssdk.services.imagebuilder.model.Distribution> translateToImageBuilderDistributions(
        final List<Distribution> cfnModelDistributions) {

        return streamOfOrEmpty(cfnModelDistributions)
                .map(cfnModelDistribution -> software.amazon.awssdk.services.imagebuilder.model.Distribution.builder()
                        .amiDistributionConfiguration(cfnModelDistribution.getAmiDistributionConfiguration() == null ?
                                null : translateToImageBuilderAmiDistributionConfiguration(cfnModelDistribution.getAmiDistributionConfiguration()))
                        .licenseConfigurationArns(cfnModelDistribution.getLicenseConfigurationArns())
                        .region(cfnModelDistribution.getRegion())
                        .build())
                .collect(Collectors.toList());
    }

    static software.amazon.awssdk.services.imagebuilder.model.AmiDistributionConfiguration translateToImageBuilderAmiDistributionConfiguration(
        final AmiDistributionConfiguration cfnModelAmiDistributionConfiguration) {

        return software.amazon.awssdk.services.imagebuilder.model.AmiDistributionConfiguration.builder()
                .name(cfnModelAmiDistributionConfiguration.getName())
                .description(cfnModelAmiDistributionConfiguration.getDescription())
                .amiTags(cfnModelAmiDistributionConfiguration.getAmiTags())
                .launchPermission(cfnModelAmiDistributionConfiguration.getLaunchPermissionConfiguration() == null ?
                        null : translateToImageBuilderLaunchPermission(cfnModelAmiDistributionConfiguration.getLaunchPermissionConfiguration()))
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.LaunchPermissionConfiguration translateToImageBuilderLaunchPermission(
        final LaunchPermissionConfiguration cfnModelLaunchPermission) {

        return software.amazon.awssdk.services.imagebuilder.model.LaunchPermissionConfiguration.builder()
                .userGroups(cfnModelLaunchPermission.getUserGroups())
                .userIds(cfnModelLaunchPermission.getUserIds())
                .build();
    }


    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}
