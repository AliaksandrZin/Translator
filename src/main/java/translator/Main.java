package translator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import translator.model.Text;
import translator.util.Languages;
import translator.view.ViewController;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private ObservableList<Text> wordsTranslated = FXCollections.observableArrayList();
    private ObservableList<String> langs = FXCollections.observableArrayList();
    private ObservableList<String> apis = FXCollections.observableArrayList();
    private MouseListener mouseListener = new MouseListener();
    private ViewController controller;

    public Main() {
        apis.addAll("Yandex"/*, "Google"*/);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("main");

        initRootLayout();
        initTranslatorLayout();
        initMouseTracker();
        initLanguages();
    }

    private void initRootLayout() {
        try {
            rootLayout = FXMLLoader.load(getClass().getResource("view/rootLayout.fxml"));
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
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
            this.controller = controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMouseTracker() {
        mouseListener.registerHook(this);
    }

    private void initLanguages() {
        Languages.setProperties(this);
        Languages.initialize("Yandex");
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ObservableList<Text> getWordsTranslated() {
        return wordsTranslated;
    }

    public ObservableList<String> getLangs() {
        return langs;
    }

    public ObservableList<String> getApis() {
        return apis;
    }

    public MouseListener getMouseListener() {
        return mouseListener;
    }

    public ViewController getController() {
        return controller;
    }
}
