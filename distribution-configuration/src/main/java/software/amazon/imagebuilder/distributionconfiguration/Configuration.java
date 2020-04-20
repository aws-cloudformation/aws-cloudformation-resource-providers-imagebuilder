package software.amazon.imagebuilder.distributionconfiguration;

import java.util.Map;
import org.json.JSONObject;
import org.json.JSONTokener;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-imagebuilder-distributionconfiguration.json");
    }
}
