package translator.api;

import javafx.concurrent.Task;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import static translator.util.Languages.base_url;
import static translator.util.Languages.private_key;

public class JsonRetrievalTask extends Task<JsonObject> {

    private static final Logger log = Logger.getLogger(JsonRetrievalTask.class.getName());

    private final URL completeUrl;

    public JsonRetrievalTask(String param, String text) {
        URL url = null;
        try {
            if (text != null) {
                url = new URL(String.format("%s%s?key=%s&lang=%s&text=%s", base_url, "/translate", private_key, param, URLEncoder.encode(text, "UTF-8")));
            } else {
                url = new URL(String.format("%s%s?key=%s&ui=%s", base_url, "/getLangs", private_key, param));
            }
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        completeUrl = url;
    }

    @Override
    protected JsonObject call() throws Exception {
        log.log(Level.INFO, "retrieving data from {0}", new String[]{base_url});

        try (JsonReader jsonReader = Json.createReader(completeUrl.openStream())) {
            return jsonReader.readObject();
        }
    }
}