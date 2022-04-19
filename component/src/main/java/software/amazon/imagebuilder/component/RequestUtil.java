package software.amazon.imagebuilder.component;

import software.amazon.awssdk.services.imagebuilder.model.CreateComponentRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteComponentRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetComponentRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListComponentBuildVersionsRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListComponentsRequest;

public class RequestUtil {
    static GetComponentRequest generateReadComponentRequest(final ResourceModel model) {
        return GetComponentRequest.builder()
                .componentBuildVersionArn(model.getArn())
                .build();
    }

    static ListComponentBuildVersionsRequest generateListComponentBuildVersions(final ResourceModel model, final String nextToken) {
        String arn = model.getArn();
        String componentVersionArn = arn.substring(0, arn.length() - 2);
        return ListComponentBuildVersionsRequest.builder()
                .componentVersionArn(componentVersionArn)
                .nextToken(nextToken)
                .build();
    }

    static DeleteComponentRequest generateDeleteComponentRequest(final ResourceModel model) {
        return DeleteComponentRequest.builder()
                .componentBuildVersionArn(model.getArn())
                .build();
    }

    static CreateComponentRequest generateCreateComponentRequest(final ResourceModel model) {
        return CreateComponentRequest.builder()
                 //Required fields
                .name(model.getName())
                .semanticVersion(model.getVersion())
                .platform(model.getPlatform())
                .uri(model.getUri())
                .data(model.getData())
                //Optional fields
                .description(model.getDescription())
                .changeDescription(model.getChangeDescription())
                .kmsKeyId(model.getKmsKeyId())
                .tags(model.getTags())
                .supportedOsVersions(model.getSupportedOsVersions())
                .build();
    }
}
