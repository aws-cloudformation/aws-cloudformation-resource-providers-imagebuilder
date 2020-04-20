package software.amazon.imagebuilder.infrastructureconfiguration;

import software.amazon.awssdk.services.imagebuilder.model.GetInfrastructureConfigurationResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListInfrastructureConfigurationsResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Translator {
    private Translator() {}

    static ResourceModel translateForRead(final GetInfrastructureConfigurationResponse response) {
        return ResourceModel.builder()
                .arn(response.infrastructureConfiguration().arn())
                .name(response.infrastructureConfiguration().name())
                .description(response.infrastructureConfiguration().description())
                .instanceTypes(response.infrastructureConfiguration().instanceTypes())
                .securityGroupIds(response.infrastructureConfiguration().securityGroupIds())
                .logging(Translator.translateToCfnModelLogging(response.infrastructureConfiguration().logging()))
                .subnetId(response.infrastructureConfiguration().subnetId())
                .keyPair(response.infrastructureConfiguration().keyPair())
                .terminateInstanceOnFailure(response.infrastructureConfiguration().terminateInstanceOnFailure())
                .instanceProfileName(response.infrastructureConfiguration().instanceProfileName())
                .snsTopicArn(response.infrastructureConfiguration().snsTopicArn())
                .tags(response.infrastructureConfiguration().tags())
                .build();
    }

    static List<ResourceModel> translateForList(final ListInfrastructureConfigurationsResponse response) {
        return streamOfOrEmpty(response.infrastructureConfigurationSummaryList())
                .map(infrastructureConfigurationSummary -> ResourceModel.builder()
                        .arn(infrastructureConfigurationSummary.arn())
                        .name(infrastructureConfigurationSummary.name())
                        .description(infrastructureConfigurationSummary.description())
                        .tags(infrastructureConfigurationSummary.tags())
                        .build())
                .collect(Collectors.toList());
    }

    static software.amazon.awssdk.services.imagebuilder.model.Logging translateToImageBuilderLogging(final Logging cfnModelLogging) {
        software.amazon.awssdk.services.imagebuilder.model.S3Logs imageBuilderS3Logs = software.amazon.awssdk.services.imagebuilder.model.S3Logs.builder()
                .s3BucketName(cfnModelLogging == null ? null : cfnModelLogging.getS3Logs().getS3BucketName())
                .s3KeyPrefix(cfnModelLogging == null ? null : cfnModelLogging.getS3Logs().getS3KeyPrefix())
                .build();

        return software.amazon.awssdk.services.imagebuilder.model.Logging.builder().s3Logs(imageBuilderS3Logs).build();
    }

    static Logging translateToCfnModelLogging(final software.amazon.awssdk.services.imagebuilder.model.Logging imageBuilderLogging) {
       S3Logs cfnModelS3Logs = S3Logs.builder()
                .s3BucketName(imageBuilderLogging == null ? null : imageBuilderLogging.s3Logs().s3BucketName())
                .s3KeyPrefix(imageBuilderLogging == null ? null : imageBuilderLogging.s3Logs().s3KeyPrefix())
                .build();

        return Logging.builder().s3Logs(cfnModelS3Logs).build();
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}
