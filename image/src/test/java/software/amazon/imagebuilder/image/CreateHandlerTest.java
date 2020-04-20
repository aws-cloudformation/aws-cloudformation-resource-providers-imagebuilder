package software.amazon.imagebuilder.image;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.imagebuilder.model.CreateImageRequest;
import software.amazon.awssdk.services.imagebuilder.model.CreateImageResponse;
import software.amazon.awssdk.services.imagebuilder.model.GetImageRequest;
import software.amazon.awssdk.services.imagebuilder.model.GetImageResponse;
import software.amazon.awssdk.services.imagebuilder.model.Image;
import software.amazon.awssdk.services.imagebuilder.model.ImageState;
import software.amazon.awssdk.services.imagebuilder.model.ImageStatus;
import software.amazon.awssdk.services.imagebuilder.model.InvalidParameterException;
import software.amazon.awssdk.services.imagebuilder.model.OutputResources;
import software.amazon.awssdk.services.imagebuilder.model.ResourceAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static software.amazon.imagebuilder.image.TestUtil.generateImageForTest;



@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    final CreateHandler handler = new CreateHandler();

    final ResourceModel model = ResourceModel.builder()
            .arn(generateImageForTest().arn())
            .imageTestsConfiguration(Translator.translateToCfnModelImageTestsConfiguration(generateImageForTest().imageTestsConfiguration()))
            .distributionConfigurationArn(generateImageForTest().distributionConfiguration().arn())
            .infrastructureConfigurationArn(generateImageForTest().infrastructureConfiguration().arn())
            .imageRecipeArn(generateImageForTest().imageRecipe().arn())
            .tags(generateImageForTest().tags())
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
    public void handleRequest_imageCreationNotYetStarted_inProgress_creation() {
        final CreateImageResponse createImageResponse = CreateImageResponse.builder()
                .imageBuildVersionArn(generateImageForTest().arn())
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final CallbackContext inputContext = CallbackContext.builder()
                .build();
        final CallbackContext outputContext = CallbackContext.builder()
                .imageCreationInvoked(true)
                .build();

        doReturn(createImageResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, inputContext, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackContext()).isEqualTo(outputContext);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(10);

        ResourceModel returnModel = response.getResourceModel();

        assertThat(returnModel.getArn()).isEqualTo(model.getArn());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_imageCreationNotYetStabilized_inProgress_building() {
        final GetImageResponse getImageResponse = GetImageResponse.builder()
                .image(Image.builder()
                    .arn(generateImageForTest().arn())
                    .state((ImageState.builder()
                            .status(ImageStatus.BUILDING.toString())
                            .build()))
                    .build())
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final CallbackContext inputContext = CallbackContext.builder()
                .imageCreationInvoked(true)
                .build();
        final CallbackContext outputContext = CallbackContext.builder()
                .imageCreationInvoked(true)
                .build();

        doReturn(getImageResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetImageRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, inputContext, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.IN_PROGRESS);
        assertThat(response.getCallbackContext()).isEqualTo(outputContext);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(10);

        ResourceModel returnModel = response.getResourceModel();

        assertThat(returnModel.getArn()).isEqualTo(model.getArn());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_imageCreationNotYetStabilized_success_available() {
        List<software.amazon.awssdk.services.imagebuilder.model.Ami> amis= new LinkedList<>();
        amis.add(software.amazon.awssdk.services.imagebuilder.model.Ami.builder().image("id-sfo").region("us-west-1").build());
        amis.add(software.amazon.awssdk.services.imagebuilder.model.Ami.builder().image("id-iad").region("us-east-1").build());
        amis.add(software.amazon.awssdk.services.imagebuilder.model.Ami.builder().image("id-pdx").region("us-west-2").build());
        amis.add(software.amazon.awssdk.services.imagebuilder.model.Ami.builder().image("id-cmh").region("us-east-2").build());
        final GetImageResponse getImageResponse = GetImageResponse.builder()
                .image(Image.builder()
                        .arn(generateImageForTest().arn())
                        .outputResources(OutputResources.builder().amis(amis).build())
                        .state((ImageState.builder()
                                .status(ImageStatus.AVAILABLE.toString())
                                .build()))
                        .build())
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .region("us-west-2")
                .desiredResourceState(model)
                .build();
        final CallbackContext inputContext = CallbackContext.builder()
                .imageCreationInvoked(true)
                .build();
        final CallbackContext outputContext = CallbackContext.builder()
                .imageCreationInvoked(true)
                .build();

        doReturn(getImageResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetImageRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, inputContext, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        ResourceModel returnModel = response.getResourceModel();

        assertThat(returnModel.getArn()).isEqualTo(generateImageForTest().arn());
        assertThat(returnModel.getImageId()).isEqualTo("id-pdx");
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_imageCreationNotYetStabilized_failed_imageCreationFailed() {
        final GetImageResponse getImageResponse = GetImageResponse.builder()
                .image(Image.builder()
                        .arn(generateImageForTest().arn())
                        .state((ImageState.builder()
                                .status(ImageStatus.FAILED.toString())
                                .build()))
                        .build())
                .build();
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final CallbackContext inputContext = CallbackContext.builder()
                .imageCreationInvoked(true)
                .build();
        final CallbackContext outputContext = CallbackContext.builder()
                .imageCreationInvoked(true)
                .build();

        doReturn(getImageResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(GetImageRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, inputContext, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isEqualTo("Error occurred during operation 'Image Creation Failed.'.");
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.GeneralServiceException);
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
