package software.amazon.imagebuilder.imagepipeline;

import software.amazon.awssdk.services.imagebuilder.model.GetImagePipelineResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListImagePipelinesResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Translator {
    private Translator() {}

    static ResourceModel translateForRead(final GetImagePipelineResponse response) {

        return ResourceModel.builder()
                .arn(response.imagePipeline().arn())
                .name(response.imagePipeline().name())
                .description(response.imagePipeline().description())
                .imageRecipeArn(response.imagePipeline().imageRecipeArn())
                .containerRecipeArn(response.imagePipeline().containerRecipeArn())
                .infrastructureConfigurationArn(response.imagePipeline().infrastructureConfigurationArn())
                .distributionConfigurationArn(response.imagePipeline().distributionConfigurationArn())
                .imageTestsConfiguration(translateToCfnModelImageTestsConfiguration(response.imagePipeline().imageTestsConfiguration()))
                .schedule(response.imagePipeline().schedule() == null ? null : translateToCfnModelSchedule(response.imagePipeline().schedule()))
                .status(response.imagePipeline().status() == null ? null : response.imagePipeline().status().name())
                .tags(response.imagePipeline().tags())
                .enhancedImageMetadataEnabled(response.imagePipeline().enhancedImageMetadataEnabled())
                .build();
    }

    static List<ResourceModel> translateForList(final ListImagePipelinesResponse response) {

        return streamOfOrEmpty(response.imagePipelineList())
                .map(imagePipeline -> ResourceModel.builder()
                        .arn(imagePipeline.arn())
                        .name(imagePipeline.name())
                        .description(imagePipeline.description())
                        .imageRecipeArn(imagePipeline.imageRecipeArn())
                        .containerRecipeArn(imagePipeline.containerRecipeArn())
                        .infrastructureConfigurationArn(imagePipeline.infrastructureConfigurationArn())
                        .distributionConfigurationArn(imagePipeline.distributionConfigurationArn())
                        .imageTestsConfiguration(translateToCfnModelImageTestsConfiguration(imagePipeline.imageTestsConfiguration()))
                        .schedule(imagePipeline.schedule() == null ? null : translateToCfnModelSchedule(imagePipeline.schedule()))
                        .status(imagePipeline.status() == null ? null : imagePipeline.status().name())
                        .tags(imagePipeline.tags())
                        .enhancedImageMetadataEnabled(imagePipeline.enhancedImageMetadataEnabled())
                        .build())
                .collect(Collectors.toList());
    }

    static ImageTestsConfiguration translateToCfnModelImageTestsConfiguration(
            final software.amazon.awssdk.services.imagebuilder.model.ImageTestsConfiguration imageBuilderImageTestsConfiguration) {

        return ImageTestsConfiguration.builder()
                .imageTestsEnabled(imageBuilderImageTestsConfiguration.imageTestsEnabled())
                .timeoutMinutes(imageBuilderImageTestsConfiguration.timeoutMinutes())
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.ImageTestsConfiguration translateToImageBuilderImageTestsConfiguration(
            final ImageTestsConfiguration cfnModelImageTestsConfiguration) {

        return software.amazon.awssdk.services.imagebuilder.model.ImageTestsConfiguration.builder()
                .imageTestsEnabled(cfnModelImageTestsConfiguration == null ? null : cfnModelImageTestsConfiguration.getImageTestsEnabled())
                .timeoutMinutes(cfnModelImageTestsConfiguration == null ? null : cfnModelImageTestsConfiguration.getTimeoutMinutes())
                .build();
    }

    static Schedule translateToCfnModelSchedule(
            final software.amazon.awssdk.services.imagebuilder.model.Schedule imageBuilderSchedule) {

        return Schedule.builder()
                .pipelineExecutionStartCondition(imageBuilderSchedule.pipelineExecutionStartCondition() == null ?
                        null : imageBuilderSchedule.pipelineExecutionStartCondition().name())
                .scheduleExpression(imageBuilderSchedule.scheduleExpression())
                .build();
    }

    static software.amazon.awssdk.services.imagebuilder.model.Schedule translateToImageBuilderSchedule(
            final Schedule cfnModelSchedule) {

        return cfnModelSchedule == null ? null : software.amazon.awssdk.services.imagebuilder.model.Schedule.builder()
                .pipelineExecutionStartCondition(cfnModelSchedule.getPipelineExecutionStartCondition())
                .scheduleExpression(cfnModelSchedule.getScheduleExpression())
                .build();
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}