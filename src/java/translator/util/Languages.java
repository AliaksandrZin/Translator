package translator.util;

import javafx.collections.FXCollections;
import translator.Main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Languages {

    private static final Logger log = Logger.getLogger(Languages.class.getName());

    private static final Properties properties = new Properties();

    private Languages() {}

    public static Map<String, String> languages = new HashMap<>();

    public static String private_key;
    public static String base_url;

    private static Main mainApp;

    public static void initialize(String engine, Main main) {
        if (main != null) {
            Languages.mainApp = main;
            InputStream inputStream;
            try {
                inputStream = new FileInputStream("/translate/src/resources/translator.properties");
                properties.load(inputStream);
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
                System.exit(1);
            }
        }
        if (engine.equals("Yandex")) {
            private_key = properties.getProperty("PRIVATE_KEY_YANDEX");
            base_url = properties.getProperty("BASE_URL_YANDEX");
        } else {
            private_key = properties.getProperty("PRIVATE_KEY_GOOGLE");
            base_url = properties.getProperty("BASE_URL_GOOGLE");
        }

        // TODO reading json list of languages from translate api, converting to Map<String, String>

        languages.putIfAbsent("English", "en");
        languages.putIfAbsent("Russian", "ru");
        languages.putIfAbsent("Turkish", "tr");

        mainApp.setLangs(FXCollections.observableArrayList(languages.keySet()));
    }

    public static void initialize(String engine) {
        Languages.initialize(engine, null);
    }
}
