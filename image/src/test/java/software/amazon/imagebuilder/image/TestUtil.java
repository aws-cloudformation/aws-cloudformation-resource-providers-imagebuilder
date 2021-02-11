package software.amazon.imagebuilder.image;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.imagebuilder.model.Ami;
import software.amazon.awssdk.services.imagebuilder.model.DistributionConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.Image;
import software.amazon.awssdk.services.imagebuilder.model.ImageRecipe;
import software.amazon.awssdk.services.imagebuilder.model.ImageState;
import software.amazon.awssdk.services.imagebuilder.model.ImageStatus;
import software.amazon.awssdk.services.imagebuilder.model.ImageTestsConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.ImageVersion;
import software.amazon.awssdk.services.imagebuilder.model.InfrastructureConfiguration;
import software.amazon.awssdk.services.imagebuilder.model.OutputResources;
import software.amazon.awssdk.services.imagebuilder.model.Platform;

import java.util.List;
import java.util.Map;

public class TestUtil {
    static final List<ImageVersion> IMAGE_VERSIONS = ImmutableList.of(
            generateImageVersionForTest()
    );

    final static String READ_IMAGE_ARN = "arn::prefix/read-image/1.0.0/1";
    final String DELETED_IMAGE_ARN = "arn::prefix/delete-image/1.0.0/1";

    private static final Map<String, String> TAG_MAPS = ImmutableMap.of("key1","value1","key2","value2");

    static Image generateImageForTest() {

        return Image.builder()
                .arn(READ_IMAGE_ARN)
                .name("image-name-test")
                .imageRecipe(ImageRecipe.builder().arn(READ_IMAGE_ARN).build())
                .distributionConfiguration(DistributionConfiguration.builder().arn("distro-arn").build())
                .infrastructureConfiguration(InfrastructureConfiguration.builder().arn("infra-arn").build())
                .outputResources(generateOutputResourceForTest())
                .imageTestsConfiguration(ImageTestsConfiguration.builder()
                        .imageTestsEnabled(true)
                        .timeoutMinutes(60)
                        .build())
                .platform(Platform.LINUX.name())
                .dateCreated("create-date-test")
                .enhancedImageMetadataEnabled(true)
                .name("image-name")
                .tags(TAG_MAPS)
                .build();
    }

    static ImageVersion generateImageVersionForTest() {

        return ImageVersion.builder()
                .arn(READ_IMAGE_ARN)
                .name("image-name-test")
                .version("image-version-test")
                .platform(Platform.LINUX)
                .dateCreated("create-date-test")
                .owner("owner")
                .build();
    }

    static OutputResources generateOutputResourceForTest() {

        return OutputResources.builder()
                .amis(ImmutableList.of(
                        Ami.builder()
                                .region("us-west-1")
                                .name("ami-name-sfo")
                                .description("ami-description")
                                .image("ami-id-sfo")
                                .state(ImageState.builder()
                                        .reason("ami-reason")
                                        .status(ImageStatus.AVAILABLE.name())
                                        .build())
                                .build(),
                        Ami.builder()
                                .region("us-west-2")
                                .name("ami-name-pdx")
                                .description("ami-description")
                                .image("ami-id-pdx")
                                .state(ImageState.builder()
                                        .reason("ami-reason")
                                        .status(ImageStatus.AVAILABLE.name())
                                        .build())
                                .build()
                ))
                .build();
    }
}
