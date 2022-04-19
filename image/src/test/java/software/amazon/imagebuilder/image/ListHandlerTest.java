package software.amazon.imagebuilder.image;

import software.amazon.awssdk.services.imagebuilder.model.ListImageBuildVersionsRequest;
import software.amazon.awssdk.services.imagebuilder.model.ListImageBuildVersionsResponse;
import software.amazon.awssdk.services.imagebuilder.model.ListImagesResponse;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    ListHandler handler;

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        handler = new ListHandler();
    }

    @Test
    public void handleRequest_Success() {
        final ListImageBuildVersionsResponse listImageBuildVersionsResponse =
                ListImageBuildVersionsResponse.builder()
                        .imageSummaryList(TestUtil.IMAGE_VERSIONS)
                        .build();
        doReturn(listImageBuildVersionsResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(any(), any());

        final ListHandler handler = new ListHandler();

        final ResourceModel model = ResourceModel.builder()
                .arn("arn:aws:imagebuilder:us-west-2:123456789012:image/imageunittest/1.0.0/1")
                .build();


        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNotNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void testBuildVersionArnToVersionArnForListRequest() {
        String imageBuildVersionArn =
                "arn:aws:imagebuilder:us-west-2:123456789012:image/testimage/1.0.0/9999";
        String expectedImageVersionArn =
                "arn:aws:imagebuilder:us-west-2:123456789012:image/testimage/1.0.0";
        ResourceModel model = new ResourceModel();
        model.setArn(imageBuildVersionArn);
        ListImageBuildVersionsRequest request = RequestUtil.generateListImageBuilderVersions(model, null);

        assertThat(request.imageVersionArn()).isEqualTo(expectedImageVersionArn);
    }
}