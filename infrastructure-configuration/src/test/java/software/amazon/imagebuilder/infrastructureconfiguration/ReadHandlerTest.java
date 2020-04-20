package software.amazon.imagebuilder.infrastructureconfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.imagebuilder.model.GetInfrastructureConfigurationResponse;
import software.amazon.awssdk.services.imagebuilder.model.InvalidParameterException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.awssdk.services.imagebuilder.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static software.amazon.imagebuilder.infrastructureconfiguration.TestUtil.generateInfrastructureConfigurationForTest;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    ReadHandler handler;
    final ResourceModel model = ResourceModel.builder()
            .arn(generateInfrastructureConfigurationForTest().arn())
            .name(generateInfrastructureConfigurationForTest().name())
            .description(generateInfrastructureConfigurationForTest().description())
            .instanceProfileName(generateInfrastructureConfigurationForTest().instanceProfileName())
            .instanceTypes(generateInfrastructureConfigurationForTest().instanceTypes())
            .keyPair(generateInfrastructureConfigurationForTest().keyPair())
            .snsTopicArn(generateInfrastructureConfigurationForTest().snsTopicArn())
            .logging(Translator.translateToCfnModelLogging(generateInfrastructureConfigurationForTest().logging()))
            .subnetId(generateInfrastructureConfigurationForTest().subnetId())
            .terminateInstanceOnFailure(generateInfrastructureConfigurationForTest().terminateInstanceOnFailure())
            .securityGroupIds(generateInfrastructureConfigurationForTest().securityGroupIds())
            .tags(generateInfrastructureConfigurationForTest().tags())
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
        final GetInfrastructureConfigurationResponse getInfrastructureConfigurationResponse =
                GetInfrastructureConfigurationResponse.builder()
                        .infrastructureConfiguration(TestUtil.generateInfrastructureConfigurationForTest())
                        .build();
        doReturn(getInfrastructureConfigurationResponse)
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

