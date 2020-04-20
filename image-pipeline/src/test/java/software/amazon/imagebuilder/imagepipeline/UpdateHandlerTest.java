package software.amazon.imagebuilder.imagepipeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.imagebuilder.model.ResourceNotFoundException;
import software.amazon.awssdk.services.imagebuilder.model.UpdateImagePipelineResponse;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static software.amazon.imagebuilder.imagepipeline.TestUtil.generateImagePipelineForTest;
import static software.amazon.imagebuilder.imagepipeline.Translator.translateToCfnModelImageTestsConfiguration;
import static software.amazon.imagebuilder.imagepipeline.Translator.translateToCfnModelSchedule;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    final UpdateHandler handler = new UpdateHandler();

    final ResourceModel currentModel = ResourceModel.builder()
            .name(generateImagePipelineForTest().name())
            .description(generateImagePipelineForTest().description())
            .imageRecipeArn(generateImagePipelineForTest().imageRecipeArn())
            .infrastructureConfigurationArn(generateImagePipelineForTest().infrastructureConfigurationArn())
            .distributionConfigurationArn(generateImagePipelineForTest().distributionConfigurationArn())
            .imageTestsConfiguration(translateToCfnModelImageTestsConfiguration(generateImagePipelineForTest().imageTestsConfiguration()))
            .schedule(translateToCfnModelSchedule(generateImagePipelineForTest().schedule()))
            .status(generateImagePipelineForTest().status() == null ? null : generateImagePipelineForTest().status().name())
            .tags(generateImagePipelineForTest().tags())
            .build();

    final ResourceModel noTagsCurrentModel = ResourceModel.builder()
            .name(generateImagePipelineForTest().name())
            .description(generateImagePipelineForTest().description())
            .imageRecipeArn(generateImagePipelineForTest().imageRecipeArn())
            .infrastructureConfigurationArn(generateImagePipelineForTest().infrastructureConfigurationArn())
            .distributionConfigurationArn(generateImagePipelineForTest().distributionConfigurationArn())
            .imageTestsConfiguration(translateToCfnModelImageTestsConfiguration(generateImagePipelineForTest().imageTestsConfiguration()))
            .schedule(translateToCfnModelSchedule(generateImagePipelineForTest().schedule()))
            .status(generateImagePipelineForTest().status() == null ? null : generateImagePipelineForTest().status().name())
            .build();

    final ResourceModel previousModel = ResourceModel.builder()
            .arn(generateImagePipelineForTest().arn())
            .name(generateImagePipelineForTest().name())
            .description(generateImagePipelineForTest().description())
            .imageRecipeArn(generateImagePipelineForTest().imageRecipeArn())
            .infrastructureConfigurationArn(generateImagePipelineForTest().infrastructureConfigurationArn())
            .distributionConfigurationArn(generateImagePipelineForTest().distributionConfigurationArn())
            .imageTestsConfiguration(translateToCfnModelImageTestsConfiguration(generateImagePipelineForTest().imageTestsConfiguration()))
            .schedule(translateToCfnModelSchedule(generateImagePipelineForTest().schedule()))
            .status(generateImagePipelineForTest().status() == null ? null : generateImagePipelineForTest().status().name())
            .tags(generateImagePipelineForTest().tags())
            .build();

    final ResourceModel noTagsPreviousModel = ResourceModel.builder()
            .arn(generateImagePipelineForTest().arn())
            .name(generateImagePipelineForTest().name())
            .description(generateImagePipelineForTest().description())
            .imageRecipeArn(generateImagePipelineForTest().imageRecipeArn())
            .infrastructureConfigurationArn(generateImagePipelineForTest().infrastructureConfigurationArn())
            .distributionConfigurationArn(generateImagePipelineForTest().distributionConfigurationArn())
            .imageTestsConfiguration(translateToCfnModelImageTestsConfiguration(generateImagePipelineForTest().imageTestsConfiguration()))
            .schedule(translateToCfnModelSchedule(generateImagePipelineForTest().schedule()))
            .status(generateImagePipelineForTest().status() == null ? null : generateImagePipelineForTest().status().name())
            .build();

    final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(currentModel)
            .previousResourceState(previousModel)
            .build();

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
    }

    @Test
    public void handleRequest_Success() {
        final UpdateImagePipelineResponse updateImagePipelineResponse = UpdateImagePipelineResponse.builder()
                .imagePipelineArn("image-pipeline-arn-test")
                .build();

        doReturn(updateImagePipelineResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

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
    public void handleRequest_NoTagsInBothPreviousAndCurrentModel() {
        final ResourceHandlerRequest<ResourceModel> tagTestRequest = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(noTagsCurrentModel)
                .previousResourceState(noTagsPreviousModel)
                .build();
        final UpdateImagePipelineResponse updateImagePipelineResponse = UpdateImagePipelineResponse.builder()
                .imagePipelineArn("image-pipeline-arn-test")
                .build();

        doReturn(updateImagePipelineResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, tagTestRequest, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(tagTestRequest.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_NoTagsInPreviousModel() {
        final ResourceHandlerRequest<ResourceModel> tagTestRequest = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(currentModel)
                .previousResourceState(noTagsPreviousModel)
                .build();

        final UpdateImagePipelineResponse updateImagePipelineResponse = UpdateImagePipelineResponse.builder()
                .imagePipelineArn("image-pipeline-arn-test")
                .build();

        doReturn(updateImagePipelineResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, tagTestRequest, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(tagTestRequest.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_NoTagsInCurrentModel() {
        final ResourceHandlerRequest<ResourceModel> tagTestRequest = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(noTagsCurrentModel)
                .previousResourceState(previousModel)
                .build();

        final UpdateImagePipelineResponse updateImagePipelineResponse = UpdateImagePipelineResponse.builder()
                .imagePipelineArn("image-pipeline-arn-test")
                .build();

        doReturn(updateImagePipelineResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, tagTestRequest, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(tagTestRequest.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}