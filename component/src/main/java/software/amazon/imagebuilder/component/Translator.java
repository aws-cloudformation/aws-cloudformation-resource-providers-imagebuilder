package software.amazon.imagebuilder.component;


import software.amazon.awssdk.services.imagebuilder.model.GetComponentResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListComponentBuildVersionsResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListComponentsResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Translator {
    private Translator() {}

    static ResourceModel translateForRead(final GetComponentResponse response) {
        return ResourceModel.builder()
                .arn(response.component().arn())
                .name(response.component().name())
                .version(response.component().version())
                .platform(response.component().platform().name())
                .description(response.component().description())
                .data(response.component().data())
                .changeDescription(response.component().changeDescription())
                .encrypted(response.component().encrypted())
                .kmsKeyId(response.component().kmsKeyId())
                .type(response.component().type() == null ? null : response.component().type().name())
                .tags(response.component().tags())
                .supportedOsVersions(response.component().supportedOsVersions())
                .build();
    }

    static List<ResourceModel> translateForList(final ListComponentBuildVersionsResponse response) {
        return streamOfOrEmpty(response.componentSummaryList())
                .map(componentSummary -> ResourceModel.builder()
                        .arn(componentSummary.arn())
                        .name(componentSummary.name())
                        .version(componentSummary.version())
                        .platform(componentSummary.platform() == null ? null : componentSummary.platform().name())
                        .type(componentSummary.type() == null ? null : componentSummary.type().name())
                        .description(componentSummary.description())
                        .supportedOsVersions(componentSummary.supportedOsVersions())
                        .build())
                .collect(Collectors.toList());
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}
