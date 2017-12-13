package translator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import translator.Main;
import translator.api.JsonRetrievalTask;

import javax.json.JsonObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Languages {

    private static final Logger log = Logger.getLogger(Languages.class.getName());

    private static final Properties properties = new Properties();
    public static Map<String, String> languages = new HashMap<>();
    public static String private_key;
    public static String base_url;
    private static Main mainApp;

    private Languages() {}

    public static void initialize(String api) {
        if (api.equals("Yandex")) {
            private_key = properties.getProperty("private_key_yandex");
            base_url = properties.getProperty("base_url_yandex");
        } else {
            private_key = properties.getProperty("private_key_google");
            base_url = properties.getProperty("base_url_google");
        }

        JsonRetrievalTask languageTask = new JsonRetrievalTask("en", null);
        languageTask.setOnSucceeded((WorkerStateEvent state) -> {
            try {
                // get original String from Json and parse it to Map
                String s = ((JsonObject) state.getSource().getValue()).getJsonObject("langs").toString();
                languages = new ObjectMapper().readValue(s, HashMap.class);
                // reversing Map's key-value (e.g. "en"-"english" to "english"-"en")
                languages = languages
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            } catch (IOException e) {
                log.log(Level.SEVERE, "Cannot read json values, {0}. Setting default languages (en, ru)", e.getMessage());
                languages.putIfAbsent("english", "en");
                languages.putIfAbsent("russian", "ru");
            }
            mainApp.getLangs().addAll(new ArrayList<>(languages.keySet()).stream().sorted().collect(Collectors.toCollection(ArrayList::new)));
            mainApp.getController().setDefaultLangs();
        });
        Platform.runLater(languageTask);
    }

    public static void setProperties(Main mainApp) {
        Languages.mainApp = mainApp;
        InputStream inputStream;
        try {
            inputStream = new FileInputStream("e:/translate/src/main/resources/translator/util/translator.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
            System.exit(1);
        }
    }
}
