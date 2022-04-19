package software.amazon.imagebuilder.imagerecipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.imagebuilder.model.AdditionalInstanceConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.ComponentParameter;
import software.amazon.awssdk.services.imagebuilder.model.InstanceBlockDeviceMapping;
import software.amazon.awssdk.services.imagebuilder.model.EbsInstanceBlockDeviceSpecification;
import software.amazon.awssdk.services.imagebuilder.model.ComponentConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.EbsVolumeType;
import software.amazon.awssdk.services.imagebuilder.model.ImageRecipe;
import software.amazon.awssdk.services.imagebuilder.model.ImageRecipeSummary;
import software.amazon.awssdk.services.imagebuilder.model.SystemsManagerAgent;

import java.util.List;
import java.util.Map;

public class TestUtil {
    static final List<ImageRecipeSummary> IMAGE_RECIPE_SUMMARIES = ImmutableList.of(
            generateImageRecipeSummaryForTest()
    );

    static final List<ComponentConfiguration> COMPONENT_CONFIGURATIONS = ImmutableList.of(
            ComponentConfiguration.builder()
                    .parameters(ImmutableList.of(ComponentParameter.builder()
                            .name("ParameterName")
                            .value(ImmutableList.of("ParameterValue1", "ParameterValue2"))
                            .build()))
                    .componentArn("component-arn-1")
                    .build(),
            ComponentConfiguration.builder()
                    .componentArn("component-arn-2")
                    .build()
    );

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

    /**
     * Base64 decoded userDataOverride from below recipe:
     *
     * #!/bin/bash
     * mkdir -p /var/bb/
     * touch /var/bb/user-data-override-test.txt**/
    static final AdditionalInstanceConfiguration ADDITIONAL_INSTANCE_CONFIGURATION = AdditionalInstanceConfiguration.builder()
            .systemsManagerAgent(SystemsManagerAgent.builder().uninstallAfterBuild(true).build())
            .userDataOverride("IyEvYmluL2Jhc2gKbWtkaXIgLXAgL3Zhci9iYi8KdG91Y2ggL3Zhci9iYi91c2VyLWRhdGEtb3ZlcnJpZGUtdGVzdC50eHQ=")
            .build();

    static final String READ_IMAGE_RECIPE_ARN = "arn::prefix/read-image-recipe/1.0.0/1";
    final String DELETED_IMAGE_RECIPE_ARN = "arn::prefix/delete-image-recipe/1.0.0/1";

    private static final Map<String, String> TAG_MAPS = ImmutableMap.of("key1","value1","key2","value2");

    static ImageRecipe generateImageRecipeForTest() {

        return ImageRecipe.builder()
                .arn(READ_IMAGE_RECIPE_ARN)
                .name("image-recipe-name-test")
                .version("1.0.0")
                .description("description-test")
                .components(COMPONENT_CONFIGURATIONS)
                .blockDeviceMappings(INSTANCE_BLOCK_DEVICE_MAPPINGS)
                .additionalInstanceConfiguration(ADDITIONAL_INSTANCE_CONFIGURATION)
                .parentImage("parent-image")
                .owner("Self")
                .platform("Linux")
                .dateCreated("create-date-test")
                .tags(TAG_MAPS)
                .build();
    }

    static ImageRecipeSummary generateImageRecipeSummaryForTest () {

        return ImageRecipeSummary.builder()
                .arn("image-recipe-arn-test")
                .name("image-recipe-name-test")
                .tags(TAG_MAPS)
                .parentImage("parent-image")
                .dateCreated("date-created-test")
                .owner("owner-test")
                .platform("Linux")
                .build();
    }
}
