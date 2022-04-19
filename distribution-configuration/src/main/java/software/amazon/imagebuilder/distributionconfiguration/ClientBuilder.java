package software.amazon.imagebuilder.distributionconfiguration;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.internal.retry.SdkDefaultRetrySetting;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.backoff.BackoffStrategy;
import software.amazon.awssdk.core.retry.backoff.EqualJitterBackoffStrategy;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.services.imagebuilder.ImagebuilderClient;
import software.amazon.cloudformation.LambdaWrapper;

import java.time.Duration;

class ClientBuilder {
    private static final BackoffStrategy BACKOFF_THROTTLING_STRATEGY =
            EqualJitterBackoffStrategy.builder()
                    .baseDelay(Duration.ofMillis(2000)) //1st retry is ~2 sec
                    .maxBackoffTime(SdkDefaultRetrySetting.MAX_BACKOFF) //default is 20s
                    .build();

    private static final RetryPolicy RETRY_POLICY = RetryPolicy.builder()
            .numRetries(6)
            .retryCondition(RetryCondition.defaultRetryCondition())
            .throttlingBackoffStrategy(BACKOFF_THROTTLING_STRATEGY)
            .build();

    static ImagebuilderClient getImageBuilderClient() {
        return ImagebuilderClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT) // Singleton HTTP client and save memory.
                .overrideConfiguration(ClientOverrideConfiguration.builder().retryPolicy(RETRY_POLICY).build())
                .build();
    }
}