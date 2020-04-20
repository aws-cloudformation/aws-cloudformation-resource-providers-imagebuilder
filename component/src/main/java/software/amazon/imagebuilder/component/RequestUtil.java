package software.amazon.imagebuilder.component;

import software.amazon.awssdk.services.imagebuilder.model.CreateComponentRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteComponentRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetComponentRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListComponentsRequest;

public class RequestUtil {
    static GetComponentRequest generateReadComponentRequest(final ResourceModel model) {
        return GetComponentRequest.builder()
                .componentBuildVersionArn(model.getArn())
                .build();
    }

    static ListComponentsRequest generateListComponentRequest(final String nextToken) {
        return ListComponentsRequest.builder()
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
                .build();
    }
}
