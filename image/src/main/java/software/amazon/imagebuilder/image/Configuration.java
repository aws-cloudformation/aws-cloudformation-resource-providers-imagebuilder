package software.amazon.imagebuilder.image;

import java.util.Map;
import org.json.JSONObject;
import org.json.JSONTokener;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-imagebuilder-image.json");
    }
}
