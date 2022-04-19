package software.amazon.imagebuilder.containerrecipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.imagebuilder.model.ContainerRecipe;
import software.amazon.awssdk.services.imagebuilder.model.GetContainerRecipeResponse;
import software.amazon.awssdk.services.imagebuilder.model.InvalidParameterException;
import software.amazon.awssdk.services.imagebuilder.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static software.amazon.imagebuilder.containerrecipe.TestUtil.generateContainerRecipeForTest;
import static software.amazon.imagebuilder.containerrecipe.Translator.translateToCfnModelComponentConfiguration;
import static software.amazon.imagebuilder.containerrecipe.Translator.translateToCfnModelInstanceConfiguration;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    ReadHandler handler;
    final ContainerRecipe containerRecipe = generateContainerRecipeForTest();
    final ResourceModel model = ResourceModel.builder()
            .arn(containerRecipe.arn())
            .name(containerRecipe.name())
            .version(containerRecipe.version())
            .parentImage(containerRecipe.parentImage())
            .components(translateToCfnModelComponentConfiguration(containerRecipe.components()))
            .description(containerRecipe.description())
            .containerType(containerRecipe.containerType().name())
            .platformOverride(containerRecipe.platformAsString())
            .instanceConfiguration(translateToCfnModelInstanceConfiguration(containerRecipe.instanceConfiguration()))
            .targetRepository(Translator.translateToCfnModelTargetRepository(containerRecipe.targetRepository()))
            .workingDirectory(containerRecipe.workingDirectory())
            .tags(containerRecipe.tags())
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
        final GetContainerRecipeResponse getContainerRecipeResponse =
                GetContainerRecipeResponse.builder()
                        .containerRecipe(containerRecipe)
                        .build();
        doReturn(getContainerRecipeResponse)
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
