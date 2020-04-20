package software.amazon.imagebuilder.distributionconfiguration;

import software.amazon.awssdk.services.imagebuilder.model.CreateDistributionConfigurationResponse;
import software.amazon.awssdk.services.imagebuilder.model.InvalidParameterException;
import software.amazon.awssdk.services.imagebuilder.model.ResourceAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
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
import static org.mockito.Mockito.*;
import static software.amazon.imagebuilder.distributionconfiguration.TestUtil.generateDistributionConfigurationForTest;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    final CreateHandler handler = new CreateHandler();

    final ResourceModel model = ResourceModel.builder()
            .arn(generateDistributionConfigurationForTest().arn())
            .name(generateDistributionConfigurationForTest().name())
            .description(generateDistributionConfigurationForTest().description())
            .distributions(Translator.translateToCfnModelDistributions(generateDistributionConfigurationForTest().distributions()))
            .tags(generateDistributionConfigurationForTest().tags())
            .build();

    final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
    }

    @Test
    public void handleRequest_Success() {
        final CreateDistributionConfigurationResponse createDistributionConfigurationResponse = CreateDistributionConfigurationResponse.builder()
                .distributionConfigurationArn("Distribution-configuration-arn-test")
                .build();

        doReturn(createDistributionConfigurationResponse)
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
    public void handleRequest_ResourceAlreadyExists_Failure() {
        doThrow(ResourceAlreadyExistsException.class)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(), any());

        assertThrows(CfnAlreadyExistsException.class,
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
