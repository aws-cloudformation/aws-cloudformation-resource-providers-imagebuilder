package software.amazon.imagebuilder.imagepipeline;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.imagebuilder.model.ImagePipeline;
import software.amazon.awssdk.services.imagebuilder.model.ImageTestsConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.PipelineExecutionStartCondition;
import software.amazon.awssdk.services.imagebuilder.model.PipelineStatus;
import software.amazon.awssdk.services.imagebuilder.model.Platform;
import software.amazon.awssdk.services.imagebuilder.model.Schedule;

import java.util.List;
import java.util.Map;

public class TestUtil {
    static final List<ImagePipeline> IMAGE_PIPELINES = ImmutableList.of(
            generateImagePipelineForTest()
    );

    final static String READ_IMAGE_PIPELINE_ARN = "arn::prefix/read-image-pipeline/1.0.0/1";
    final String DELETED_IMAGE_PIPELINE_ARN = "arn::prefix/delete-image-pipeline/1.0.0/1";

    private static final Map<String, String> TAG_MAPS = ImmutableMap.of("key1","value1","key2","value2");

    static ImagePipeline generateImagePipelineForTest() {

        return ImagePipeline.builder()
                .arn(READ_IMAGE_PIPELINE_ARN)
                .name("image-pipeline-name-test")
                .description("description-test")
                .distributionConfigurationArn("distro-arn")
                .imageRecipeArn("image-recipe-arn")
                .infrastructureConfigurationArn("infra-arn")
                .imageTestsConfiguration(ImageTestsConfiguration.builder()
                        .imageTestsEnabled(true)
                        .timeoutMinutes(60)
                        .build())
                .platform(Platform.LINUX.name())
                .schedule(Schedule.builder()
                        .pipelineExecutionStartCondition(PipelineExecutionStartCondition.EXPRESSION_MATCH_AND_DEPENDENCY_UPDATES_AVAILABLE)
                        .scheduleExpression("schedule-expression-test")
                        .build())
                .status(PipelineStatus.ENABLED)
                .dateCreated("create-date-test")
                .dateUpdated("update-date-test")
                .dateLastRun("last-run-test")
                .dateNextRun("next-run-test")
                .tags(TAG_MAPS)
                .build();
    }
}
