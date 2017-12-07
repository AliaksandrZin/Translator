package translator.view;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import translator.Main;
import translator.model.Word;
import translator.util.Languages;

public class ViewController {

    @FXML
    private TableView<Word> wordTable;
    @FXML
    private TableColumn<Word, String> original;
    @FXML
    private TableColumn<Word, String> translated;
    @FXML
    private ComboBox<String> originalLang;
    @FXML
    private ComboBox<String> translatedLang;
    @FXML
    private ComboBox<String> api;

    private Main mainApp;

    public ViewController() {
    }

    @FXML
    private void initialize() {
        original.setCellValueFactory(cellData -> cellData.getValue().originalProperty());
        translated.setCellValueFactory(cellData -> cellData.getValue().translatedProperty());
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        wordTable.setItems(mainApp.getWordsTranslated());
        api.setItems(mainApp.getEngines());
        api.setValue("Yandex");
        Languages.initialize(api.getValue(), mainApp);
        originalLang.setItems(mainApp.getLangs());
        translatedLang.setItems(mainApp.getLangs());
    }

    @FXML
    private void setOriginalLang() {
        String lang = originalLang.getValue();
        originalLang.setValue(lang);
        mainApp.getMouseListener().setFrom(lang);
    }
    @FXML
    private void setTranslatedLang() {
        String lang = translatedLang.getValue();
        translatedLang.setValue(lang);
        mainApp.getMouseListener().setTo(lang);
    }
    @FXML
    private void setTranslationEngine() {
        String engineValue = api.getValue();
        api.setValue(engineValue);
        Languages.initialize(engineValue);
    }
}