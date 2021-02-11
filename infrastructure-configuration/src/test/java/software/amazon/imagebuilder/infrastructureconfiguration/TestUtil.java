package software.amazon.imagebuilder.infrastructureconfiguration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.imagebuilder.model.Component;
import software.amazon.awssdk.services.imagebuilder.model.ComponentVersion;
import software.amazon.awssdk.services.imagebuilder.model.InfrastructureConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.InfrastructureConfigurationSummary;
import software.amazon.awssdk.services.imagebuilder.model.Logging;
import software.amazon.awssdk.services.imagebuilder.model.S3Logs;

import java.util.List;
import java.util.Map;

public class TestUtil {
    static final List<InfrastructureConfigurationSummary> INFRASTRUCTURE_CONFIGURATION_SUMMARIES = ImmutableList.of(
            generateInfrastructureConfigurationSummaryForTest()
    );

    final static String READ_INFRASTRUCTURE_CONFIGURATION_ARN = "arn::prefix/read-infrastructure-configuration/1.0.0/1";
    final String DELETED_INFRASTRUCTURE_CONFIGURATION_ARN = "arn::prefix/delete-infrastructure-configuration/1.0.0/1";

    private static final Map<String, String> TAG_MAPS = ImmutableMap.of("key1","value1","key2","value2");
    private static final Map<String, String> RESOURCE_TAG_MAPS = ImmutableMap.of("key3","value3","key4","value4");

    static InfrastructureConfiguration generateInfrastructureConfigurationForTest() {

        return InfrastructureConfiguration.builder()
                .arn(READ_INFRASTRUCTURE_CONFIGURATION_ARN)
                .name("infrastructure-configuration-name-test")
                .description("description-test")
                .instanceProfileName("instance-profile-test")
                .instanceTypes("instance-type-test")
                .keyPair("key-pair")
                .snsTopicArn("sns-topic-arn")
                .logging(Logging.builder().s3Logs(S3Logs.builder()
                        .s3BucketName("s3-bucket-name-test").s3KeyPrefix("s3-key-prefix-test")
                        .build()).build())
                .subnetId("subnet-id-test")
                .terminateInstanceOnFailure(false)
                .securityGroupIds("security-group-id")
                .dateCreated("create-date-test")
                .dateUpdated("update-date-test")
                .resourceTags(RESOURCE_TAG_MAPS)
                .tags(TAG_MAPS)
                .build();
    }

    static InfrastructureConfigurationSummary generateInfrastructureConfigurationSummaryForTest () {

        return InfrastructureConfigurationSummary.builder()
                .arn(READ_INFRASTRUCTURE_CONFIGURATION_ARN)
                .name("infrastructure-configuration-name-test")
                .dateCreated("create-date-test")
                .dateUpdated("update-date-test")
                .description("description-test")
                .tags(TAG_MAPS)
                .build();
    }
}
