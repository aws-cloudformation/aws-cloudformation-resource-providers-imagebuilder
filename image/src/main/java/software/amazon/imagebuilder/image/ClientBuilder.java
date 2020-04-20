package software.amazon.imagebuilder.image;


import software.amazon.awssdk.services.imagebuilder.ImagebuilderClient;

class ClientBuilder {
    static ImagebuilderClient getImageBuilderClient() {
        return ImagebuilderClient.create();
    }
}