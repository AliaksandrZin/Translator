package translator;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import translator.model.Word;
import translator.view.ViewController;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private ObservableList<Word> wordsTranslated = FXCollections.observableArrayList();
    private ObservableList<String> langs = FXCollections.observableArrayList();
    private ObservableList<String> engines = FXCollections.observableArrayList();
    private MouseListener mouseListener = new MouseListener();


    public Main() {
        engines.add("Yandex");
        engines.add("Google");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("main");

        initRootLayout();
        initTranslatorLayout();
        initMouseTracker();
    }

    private void initRootLayout() {
        try {
            rootLayout = FXMLLoader.load(getClass().getResource("view/rootLayout.fxml"));
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initTranslatorLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/translatorLayout.fxml"));
            AnchorPane translator = loader.load();
            rootLayout.setCenter(translator);
            ViewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMouseTracker() {
        mouseListener.registerHook(this);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ObservableList<Word> getWordsTranslated() {
        return wordsTranslated;
    }

    public ObservableList<String> getLangs() {
        return langs;
    }

    public void setLangs(ObservableList<String> langs) {
        this.langs = langs;
    }

    public ObservableList<String> getEngines() {
        return engines;
    }

    public MouseListener getMouseListener() {
        return mouseListener;
    }
}
