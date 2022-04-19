package software.amazon.imagebuilder.containerrecipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.imagebuilder.model.ComponentConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.ContainerRecipe;
import software.amazon.awssdk.services.imagebuilder.model.ContainerRecipeSummary;
import software.amazon.awssdk.services.imagebuilder.model.ContainerType;
import software.amazon.awssdk.services.imagebuilder.model.EbsInstanceBlockDeviceSpecification;
import software.amazon.awssdk.services.imagebuilder.model.EbsVolumeType;
import software.amazon.awssdk.services.imagebuilder.model.InstanceBlockDeviceMapping;
import software.amazon.awssdk.services.imagebuilder.model.InstanceConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.TargetContainerRepository;

import java.util.List;
import java.util.Map;

public class TestUtil {
    static final List<ContainerRecipeSummary> CONTAINER_RECIPE_SUMMARIES = ImmutableList.of(
            generateContainerRecipeSummaryForTest()
    );

    static final List<ComponentConfiguration> COMPONENT_CONFIGURATIONS = ImmutableList.of(
            ComponentConfiguration.builder().componentArn("component-arn-1").build(),
            ComponentConfiguration.builder().componentArn("component-arn-2").build()
    );

    static final TargetContainerRepository targetRepository = TargetContainerRepository.builder()
            .service("ECR")
            .repositoryName("test")
            .build();

    final static String READ_CONTAINER_RECIPE_ARN = "arn::prefix/read-container-recipe/1.0.0/1";
    final String DELETED_CONTAINER_RECIPE_ARN = "arn::prefix/delete-container-recipe/1.0.0/1";

    private static final Map<String, String> TAG_MAPS = ImmutableMap.of("key1","value1","key2","value2");

    static final List<InstanceBlockDeviceMapping> INSTANCE_BLOCK_DEVICE_MAPPINGS = ImmutableList.of(
            InstanceBlockDeviceMapping.builder()
                    .deviceName("deviceName")
                    .virtualName("virtualName")
                    .noDevice("noDevice")
                    .ebs(EbsInstanceBlockDeviceSpecification.builder()
                            .deleteOnTermination(false)
                            .encrypted(false)
                            .iops(200)
                            .kmsKeyId("kmsKeyId")
                            .snapshotId("snapshotId")
                            .volumeSize(10)
                            .volumeType(EbsVolumeType.GP2.name())
                            .throughput(125)
                            .build()
                    ).build()
    );

    static ContainerRecipe generateContainerRecipeForTest() {

        return ContainerRecipe.builder()
                .arn(READ_CONTAINER_RECIPE_ARN)
                .name("container-recipe-name-test")
                .version("1.0.0")
                .description("description-test")
                .components(COMPONENT_CONFIGURATIONS)
                .instanceConfiguration(InstanceConfiguration.builder()
                        .image("ami-123456788")
                        .blockDeviceMappings(INSTANCE_BLOCK_DEVICE_MAPPINGS)
                        .build())
                .parentImage("parent-image")
                .owner("Self")
                .workingDirectory("/tmp/test")
                .containerType(ContainerType.DOCKER.name())
                .platform("Linux")
                .dateCreated("create-date-test")
                .targetRepository(targetRepository)
                .tags(TAG_MAPS)
                .build();
    }

    static ContainerRecipeSummary generateContainerRecipeSummaryForTest() {

        return ContainerRecipeSummary.builder()
                .arn("container-recipe-arn-test")
                .name("container-recipe-name-test")
                .tags(TAG_MAPS)
                .parentImage("parent-image")
                .dateCreated("date-created-test")
                .owner("owner-test")
                .platform("Linux")
                .build();
    }
}
