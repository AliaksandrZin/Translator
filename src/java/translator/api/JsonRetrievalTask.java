package translator.api;

import javafx.concurrent.Task;

import javax.json.Json;
import javax.json.JsonReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import static translator.util.Languages.base_url;
import static translator.util.Languages.private_key;

public class JsonRetrievalTask extends Task<String> {

    private static final Logger log = Logger.getLogger(JsonRetrievalTask.class.getName());

    private final URL completeUrl;

    public JsonRetrievalTask(String lang, String text) {
        URL hlp = null;
        try {
            hlp = new URL(String.format("%s?key=%s&lang=%s&text=%s", base_url, private_key, lang, URLEncoder.encode(text, "UTF-8")));
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        completeUrl = hlp;
    }

    @Override
    protected String call() throws Exception {
        log.log(Level.INFO, "retrieving Json data from {0}", new String[]{base_url});

        try (JsonReader jsonReader = Json.createReader(completeUrl.openStream())) {
            return jsonReader.readObject().getJsonArray("text").get(0).toString().replaceAll("\"", "");
        }
    }
}