package software.amazon.imagebuilder.distributionconfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.imagebuilder.model.ResourceNotFoundException;
import software.amazon.awssdk.services.imagebuilder.model.UpdateDistributionConfigurationResponse;
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
import static software.amazon.imagebuilder.distributionconfiguration.TestUtil.generateDistributionConfigurationForTest;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    final UpdateHandler handler = new UpdateHandler();

    final ResourceModel currentModel = ResourceModel.builder()
            .name(generateDistributionConfigurationForTest().name())
            .description(generateDistributionConfigurationForTest().description())
            .distributions(Translator.translateToCfnModelDistributions(generateDistributionConfigurationForTest().distributions()))
            .tags(generateDistributionConfigurationForTest().tags())
            .build();

    final ResourceModel noTagsCurrentModel = ResourceModel.builder()
            .name(generateDistributionConfigurationForTest().name())
            .description(generateDistributionConfigurationForTest().description())
            .distributions(Translator.translateToCfnModelDistributions(generateDistributionConfigurationForTest().distributions()))
            .build();

    final ResourceModel previousModel = ResourceModel.builder()
            .arn(generateDistributionConfigurationForTest().arn())
            .name(generateDistributionConfigurationForTest().name())
            .description(generateDistributionConfigurationForTest().description())
            .distributions(Translator.translateToCfnModelDistributions(generateDistributionConfigurationForTest().distributions()))
            .tags(generateDistributionConfigurationForTest().tags())
            .build();

    final ResourceModel noTagsPreviousModel = ResourceModel.builder()
            .arn(generateDistributionConfigurationForTest().arn())
            .name(generateDistributionConfigurationForTest().name())
            .description(generateDistributionConfigurationForTest().description())
            .distributions(Translator.translateToCfnModelDistributions(generateDistributionConfigurationForTest().distributions()))
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
        
        final UpdateDistributionConfigurationResponse updateDistributionConfigurationResponse = UpdateDistributionConfigurationResponse.builder()
                .distributionConfigurationArn("Distribution-configuration-arn-test")
                .build();

        doReturn(updateDistributionConfigurationResponse)
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

        final UpdateDistributionConfigurationResponse updateDistributionConfigurationResponse = UpdateDistributionConfigurationResponse.builder()
                .distributionConfigurationArn("Distribution-configuration-arn-test")
                .build();

        doReturn(updateDistributionConfigurationResponse)
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

        final UpdateDistributionConfigurationResponse updateDistributionConfigurationResponse = UpdateDistributionConfigurationResponse.builder()
                .distributionConfigurationArn("Distribution-configuration-arn-test")
                .build();

        doReturn(updateDistributionConfigurationResponse)
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

        final UpdateDistributionConfigurationResponse updateDistributionConfigurationResponse = UpdateDistributionConfigurationResponse.builder()
                .distributionConfigurationArn("Distribution-configuration-arn-test")
                .build();

        doReturn(updateDistributionConfigurationResponse)
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