package software.amazon.imagebuilder.distributionconfiguration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.imagebuilder.model.DistributionConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.AmiDistributionConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.LaunchPermissionConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.Distribution;
import software.amazon.awssdk.services.imagebuilder.model.DistributionConfigurationSummary;

import java.util.List;
import java.util.Map;

public class TestUtil {
    static final List<DistributionConfigurationSummary> DISTRIBUTION_CONFIGURATION_SUMMARIES = ImmutableList.of(
            generateDistributionConfigurationSummaryForTest()
    );

    static final List<Distribution> DISTRIBUTIONS = generateDistributionsForTest();

    final static String READ_DISTRIBUTION_CONFIGURATION_ARN = "arn::prefix/read-distribution-configuration/1.0.0/1";
    final String DELETED_DISTRIBUTION_CONFIGURATION_ARN = "arn::prefix/delete-distribution-configuration/1.0.0/1";

    private static final Map<String, String> TAG_MAPS = ImmutableMap.of("key1","value1","key2","value2");

    static DistributionConfiguration generateDistributionConfigurationForTest() {

        return DistributionConfiguration.builder()
                .arn(READ_DISTRIBUTION_CONFIGURATION_ARN)
                .name("distribution-configuration-name-test")
                .description("description-test")
                .distributions(DISTRIBUTIONS)
                .timeoutMinutes(300)
                .dateCreated("create-date-test")
                .dateUpdated("update-date-test")
                .tags(TAG_MAPS)
                .build();
    }

    static DistributionConfigurationSummary generateDistributionConfigurationSummaryForTest() {

        return DistributionConfigurationSummary.builder()
                .arn(READ_DISTRIBUTION_CONFIGURATION_ARN)
                .name("distribution-configuration-name-test")
                .dateCreated("create-date-test")
                .dateUpdated("update-date-test")
                .description("description-test")
                .tags(TAG_MAPS)
                .build();
    }

    static List<Distribution> generateDistributionsForTest () {
        return ImmutableList.of(
                Distribution.builder()
                        .region("test-region")
                        .licenseConfigurationArns(ImmutableList.of("liscense-arn-1", "lisence-arn-2"))
                        .amiDistributionConfiguration(AmiDistributionConfiguration.builder()
                                .description("Description-1")
                                .launchPermission(LaunchPermissionConfiguration.builder()
                                        .userIds(ImmutableList.of("user-id-1"))
                                        .userGroups(ImmutableList.of("user-group-1"))
                                        .build())
                                .amiTags(ImmutableMap.of("TagKey1", "TagValue1"))
                                .build())
                        .build(),

                Distribution.builder()
                        .region("test-region-2")
                        .licenseConfigurationArns(ImmutableList.of("liscense-arn-3", "lisence-arn-4"))
                        .amiDistributionConfiguration(AmiDistributionConfiguration.builder()
                                .description("Description-2")
                                .launchPermission(LaunchPermissionConfiguration.builder()
                                        .userIds(ImmutableList.of("user-id-2"))
                                        .userGroups(ImmutableList.of("user-group-2"))
                                        .build())
                                .amiTags(ImmutableMap.of("TagKey2", "TagValue2"))
                                .build())
                        .build()
                );
    }
}
