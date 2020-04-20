package software.amazon.imagebuilder.imagepipeline;


import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.services.imagebuilder.ImagebuilderClient;
import software.amazon.cloudformation.LambdaWrapper;


import java.net.URI;

class ClientBuilder {
    static ImagebuilderClient getImageBuilderClient() {
        return ImagebuilderClient.create();
    }
}