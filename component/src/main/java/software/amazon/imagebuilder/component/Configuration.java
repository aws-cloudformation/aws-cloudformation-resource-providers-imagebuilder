package software.amazon.imagebuilder.component;

import java.util.Map;
import org.json.JSONObject;
import org.json.JSONTokener;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-imagebuilder-component.json");
    }
}
