package software.amazon.imagebuilder.image;
import software.amazon.awssdk.services.imagebuilder.model.GetImageResponse;
import software.amazon.awssdk.services.imagebuilder.model.InvalidParameterException;
import software.amazon.awssdk.services.imagebuilder.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import java.util.Objects;

public class ReadHandler extends BaseHandler<CallbackContext> {
    private AmazonWebServicesClientProxy proxy;
    private ResourceHandlerRequest<ResourceModel> request;
    private Logger logger;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        this.proxy = proxy;
        this.request = request;
        this.logger = logger;
        final ResourceModel model = request.getDesiredResourceState();
        final CallbackContext context = callbackContext == null ?
                CallbackContext.builder()
                        .build() :
                callbackContext;

        return fetchImageAndAssertExists();
    }

    private ProgressEvent<ResourceModel, CallbackContext> fetchImageAndAssertExists() {
        final ResourceModel model = request.getDesiredResourceState();

        if (model == null || model.getArn() == null) {
            throwNotFoundException(model);
        }

        GetImageResponse response = null;
        try {
            response = proxy.injectCredentialsAndInvokeV2(RequestUtil.generateGetImageRequest(model),
                    ClientBuilder.getImageBuilderClient()::getImage);
        } catch (final ResourceNotFoundException e) {
            throwNotFoundException(model);
        } catch (final InvalidParameterException e) {
            throw new CfnInvalidRequestException(ResourceModel.TYPE_NAME);
        }

        final ResourceModel modelFromReadResult = Translator.translateForRead(response, request.getRegion());
        if (modelFromReadResult.getArn() == null) {
            throwNotFoundException(model);
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(modelFromReadResult)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private void throwNotFoundException(final ResourceModel model) {
        final ResourceModel nullSafeModel = model == null ? ResourceModel.builder().build() : model;
        throw new CfnNotFoundException(ResourceModel.TYPE_NAME,
                Objects.toString(nullSafeModel.getPrimaryIdentifier()));
    }
}
