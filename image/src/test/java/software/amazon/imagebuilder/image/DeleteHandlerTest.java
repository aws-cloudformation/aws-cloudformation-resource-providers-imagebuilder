package software.amazon.imagebuilder.image;

import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.services.imagebuilder.model.Ami;
import software.amazon.awssdk.services.imagebuilder.model.CancelImageCreationRequest;
import software.amazon.awssdk.services.imagebuilder.model.CancelImageCreationResponse;
import software.amazon.awssdk.services.imagebuilder.model.DeleteImageRequest;
import software.amazon.awssdk.services.imagebuilder.model.DeleteImageResponse;
import software.amazon.awssdk.services.imagebuilder.model.GetImageRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetImageResponse;
import software.amazon.awssdk.services.imagebuilder.model.Image;
import software.amazon.awssdk.services.imagebuilder.model.ImageState;
import software.amazon.awssdk.services.imagebuilder.model.ImageStatus;
import software.amazon.awssdk.services.imagebuilder.model.ImageType;
import software.amazon.awssdk.services.imagebuilder.model.OutputResources;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static software.amazon.imagebuilder.image.TestUtil.generateImageForTest;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest {
    @Mock private AmazonWebServicesClientProxy proxy;
    @Mock private Logger logger;

    private static final List<Ami> AMIS = ImmutableList.of(Ami.builder().image("id-pdx").region("us-west-2").build());
    private static final DeleteHandler DELETE_HANDLER = new DeleteHandler();
    private static final ResourceModel RESOURCE_MODEL = ResourceModel.builder()
            .arn(generateImageForTest().arn())
            .build();

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
    }

    @Test
    public void handleRequest_noWorkflowCancellationRequired_inProgress_AvailableState() {
        final DeleteImageResponse deleteImageResponse = getDeleteImageResponse();
        final GetImageResponse getImageResponse = getImageResponseWithImageStatus(ImageStatus.AVAILABLE);
        final ResourceHandlerRequest<ResourceModel> request = setupRequestFromResourceModel();

        final CallbackContext inputContext = CallbackContext.builder()
                .build();
        final CallbackContext outputContext = CallbackContext.builder()
                .imageWorkflowStatus(CallbackContext.WorkflowStatus.DELETED)
                .build();

        doReturn(deleteImageResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeleteImageRequest.class), any());

        doReturn(getImageResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetImageRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = DELETE_HANDLER.handleRequest(proxy, request, inputContext, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackContext()).isEqualTo(outputContext);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(1); // 1s if not require workflow cancellation.

        ResourceModel returnModel = response.getResourceModel();

        assertThat(returnModel.getArn()).isEqualTo(RESOURCE_MODEL.getArn());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_noWorkflowCancellationRequired_inProgress_FailedState() {
        final DeleteImageResponse deleteImageResponse = getDeleteImageResponse();
        final GetImageResponse getImageResponse = getImageResponseWithImageStatus(ImageStatus.FAILED);
        final ResourceHandlerRequest<ResourceModel> request = setupRequestFromResourceModel();
        final CallbackContext inputContext = CallbackContext.builder()
                .build();
        final CallbackContext outputContext = CallbackContext.builder()
                .imageWorkflowStatus(CallbackContext.WorkflowStatus.DELETED)
                .build();

        doReturn(deleteImageResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(DeleteImageRequest.class), any());

        doReturn(getImageResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetImageRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = DELETE_HANDLER.handleRequest(proxy, request, inputContext, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackContext()).isEqualTo(outputContext);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(1); // 1s if not require workflow cancellation.

        ResourceModel returnModel = response.getResourceModel();

        assertThat(returnModel.getArn()).isEqualTo(RESOURCE_MODEL.getArn());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_imageCancellationNotYetStabilized_inProgress_NonTerminalState() {
        final GetImageResponse getImageResponse = getImageResponseWithImageStatus(ImageStatus.BUILDING);
        final CancelImageCreationResponse cancelImageCreationResponse = getCancelImageCreationResponse();
        final ResourceHandlerRequest<ResourceModel> request = setupRequestFromResourceModel();
        final CallbackContext inputContext = CallbackContext.builder()
                .build();
        final CallbackContext outputContext = CallbackContext.builder()
                .imageWorkflowStatus(CallbackContext.WorkflowStatus.RUNNING)
                .build();

        doReturn(getImageResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetImageRequest.class), any());

        doReturn(cancelImageCreationResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(CancelImageCreationRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = DELETE_HANDLER.handleRequest(proxy, request, inputContext, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackContext()).isEqualTo(outputContext);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(10); // 10s if cancelled but not stabilized yet.

        ResourceModel returnModel = response.getResourceModel();

        assertThat(returnModel.getArn()).isEqualTo(RESOURCE_MODEL.getArn());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_CancellationStabilized_inProgress_cancelledState() {
        final GetImageResponse getImageResponse = getImageResponseWithImageStatus(ImageStatus.CANCELLED);

        final ResourceHandlerRequest<ResourceModel> request = setupRequestFromResourceModel();
        final CallbackContext inputContext = CallbackContext.builder()
                .imageWorkflowStatus(CallbackContext.WorkflowStatus.RUNNING)
                .build();
        final CallbackContext outputContext = CallbackContext.builder()
                .imageWorkflowStatus(CallbackContext.WorkflowStatus.IN_TERMINAL_STATE)
                .build();

        doReturn(getImageResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetImageRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = DELETE_HANDLER.handleRequest(proxy, request, inputContext, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackContext()).isEqualTo(outputContext);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(1); // 1s if stabilized in CANCELLED.

        ResourceModel returnModel = response.getResourceModel();

        assertThat(returnModel.getArn()).isEqualTo(RESOURCE_MODEL.getArn());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_success_cancelledState() {
        final ResourceHandlerRequest<ResourceModel> request = setupRequestFromResourceModel();
        final CallbackContext inputContext = CallbackContext.builder()
                .imageWorkflowStatus(CallbackContext.WorkflowStatus.IN_TERMINAL_STATE)
                .build();
        final CallbackContext outputContext = CallbackContext.builder()
                .imageWorkflowStatus(CallbackContext.WorkflowStatus.DELETED)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = DELETE_HANDLER.handleRequest(proxy, request, inputContext, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackContext()).isEqualTo(outputContext);

        ResourceModel returnModel = response.getResourceModel();

        assertThat(returnModel.getArn()).isEqualTo(RESOURCE_MODEL.getArn());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_success_deletedState() {
        final ResourceHandlerRequest<ResourceModel> request = setupRequestFromResourceModel();
        final CallbackContext inputContext = CallbackContext.builder()
                .imageWorkflowStatus(CallbackContext.WorkflowStatus.DELETED)
                .build();
        final CallbackContext outputContext = CallbackContext.builder()
                .imageWorkflowStatus(CallbackContext.WorkflowStatus.DELETED)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = DELETE_HANDLER.handleRequest(proxy, request, inputContext, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isEqualTo(outputContext);

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }


    /** Helpers **/
    private GetImageResponse getImageResponseWithImageStatus(ImageStatus status) {
        return GetImageResponse.builder()
                .image(Image.builder()
                        .version("1.0.0/1")
                        .arn(generateImageForTest().arn())
                        .type(ImageType.AMI)
                        .outputResources(OutputResources.builder().amis(AMIS).build())
                        .state((ImageState.builder()
                                .status(status)
                                .build()))
                        .build())
                .build();
    }

    private DeleteImageResponse getDeleteImageResponse() {
        return DeleteImageResponse.builder()
                .imageBuildVersionArn(generateImageForTest().arn())
                .build();
    }

    private CancelImageCreationResponse getCancelImageCreationResponse() {
        return CancelImageCreationResponse.builder()
                .imageBuildVersionArn(RESOURCE_MODEL.getArn())
                .build();
    }

    private ResourceHandlerRequest<ResourceModel> setupRequestFromResourceModel() {
        return ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(RESOURCE_MODEL)
                .build();
    }
}
