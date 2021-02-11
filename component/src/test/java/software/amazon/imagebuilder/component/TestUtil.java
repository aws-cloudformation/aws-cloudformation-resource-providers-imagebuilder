package software.amazon.imagebuilder.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.imagebuilder.model.Component;
import software.amazon.awssdk.services.imagebuilder.model.ComponentSummary;
import software.amazon.awssdk.services.imagebuilder.model.ComponentVersion;
import software.amazon.awssdk.services.imagebuilder.model.ImageRecipe;
import software.amazon.awssdk.services.imagebuilder.model.ImageRecipeSummary;

import java.util.List;
import java.util.Map;

public class TestUtil {
    static final List<software.amazon.awssdk.services.imagebuilder.model.ComponentVersion> COMPONENT_VERSIONS = ImmutableList.of(
            generateComponentVersionForTest()
    );

    final static String READ_COMPONENT_ARN = "arn::prefix/read-component/1.0.0/1";
    final String DELETED_COMPONENT_ARN = "arn::prefix/delete-component/1.0.0/1";

    private static final Map<String, String> TAG_MAPS = ImmutableMap.of("key1","value1","key2","value2");

    static Component generateComponentForTest() {

        return Component.builder()
                .arn(READ_COMPONENT_ARN)
                .name("component-name-test")
                .version("1.0.0")
                .changeDescription("change-description-test")
                .description("description-test")
                .encrypted(false)
                .owner("Self")
                .type("BUILD")
                .kmsKeyId("kms-key-arn-test")
                .data("component-data-test")
                .dateCreated("create-date-test")
                .platform("Linux")
                .supportedOsVersions("Amazon Linux 2", "CentOS 7")
                .tags(TAG_MAPS)
                .build();
    }

    static ComponentVersion generateComponentVersionForTest () {

        return ComponentVersion.builder()
                .arn("component-arn-test")
                .name("component-name-test")
                .version("1.2.3")
                .dateCreated("date-created-test")
                .owner("owner-test")
                .platform("Linux")
                .description("description-test")
                .type("BUILDING")
                .build();
    }
}
