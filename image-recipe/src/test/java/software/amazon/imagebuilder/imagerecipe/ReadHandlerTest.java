package software.amazon.imagebuilder.imagerecipe;

import software.amazon.awssdk.services.imagebuilder.model.GetImageRecipeResponse;
import software.amazon.awssdk.services.imagebuilder.model.InvalidParameterException;
import software.amazon.awssdk.services.imagebuilder.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static software.amazon.imagebuilder.imagerecipe.TestUtil.generateImageRecipeForTest;
import static software.amazon.imagebuilder.imagerecipe.Translator.translateToCfnModelComponentConfiguration;
import static software.amazon.imagebuilder.imagerecipe.Translator.translateToCfnModelInstanceBlockDeviceMapping;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    ReadHandler handler;
    final ResourceModel model = ResourceModel.builder()
            .arn(generateImageRecipeForTest().arn())
            .name(generateImageRecipeForTest().name())
            .version(generateImageRecipeForTest().version())
            .parentImage(generateImageRecipeForTest().parentImage())
            .components(translateToCfnModelComponentConfiguration(generateImageRecipeForTest().components()))
            .blockDeviceMappings(translateToCfnModelInstanceBlockDeviceMapping(generateImageRecipeForTest().blockDeviceMappings()))
            .description(generateImageRecipeForTest().description())
            .tags(generateImageRecipeForTest().tags())
            .build();
    final ResourceHandlerRequest<ResourceModel> request =
            ResourceHandlerRequest.<ResourceModel>builder()
                    .desiredResourceState(model)
                    .build();
    final CallbackContext context = CallbackContext.builder()
            .build();

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        handler = new ReadHandler();
    }

    @Test
    public void handleRequest_Success() {
        final GetImageRecipeResponse getImageRecipeResponse =
                GetImageRecipeResponse.builder()
                        .imageRecipe(TestUtil.generateImageRecipeForTest())
                        .build();
        doReturn(getImageRecipeResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, context, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ResourceNotFound_Failure() {
        doThrow(ResourceNotFoundException.class)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(), any());

        assertThrows(CfnNotFoundException.class,
                () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_InvalidParameter_Failure() {
        doThrow(InvalidParameterException.class)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(), any());

        assertThrows(CfnInvalidRequestException.class,
                () -> handler.handleRequest(proxy, request, null, logger));
    }
}
