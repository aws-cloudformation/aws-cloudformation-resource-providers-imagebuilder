package software.amazon.imagebuilder.imagerecipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.imagebuilder.model.InstanceBlockDeviceMapping;
import software.amazon.awssdk.services.imagebuilder.model.EbsInstanceBlockDeviceSpecification;
import software.amazon.awssdk.services.imagebuilder.model.ComponentConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.EbsVolumeType;
import software.amazon.awssdk.services.imagebuilder.model.ImageRecipe;
import software.amazon.awssdk.services.imagebuilder.model.ImageRecipeSummary;

import java.util.List;
import java.util.Map;

public class TestUtil {
    static final List<ImageRecipeSummary> IMAGE_RECIPE_SUMMARIES = ImmutableList.of(
            generateImageRecipeSummaryForTest()
    );

    static final List<ComponentConfiguration> COMPONENT_CONFIGURATIONS = ImmutableList.of(
            ComponentConfiguration.builder().componentArn("component-arn-1").build(),
            ComponentConfiguration.builder().componentArn("component-arn-2").build()
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
                            .build()
            ).build()
    );

    final static String READ_IMAGE_RECIPE_ARN = "arn::prefix/read-image-recipe/1.0.0/1";
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
