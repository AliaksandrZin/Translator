package translator;

import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import translator.api.JsonRetrievalTask;
import translator.model.Word;
import translator.util.Assert;
import translator.util.Languages;
import translator.util.PopUp;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MouseListener implements NativeMouseInputListener {

    private static final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    private static final Logger log = Logger.getLogger(MouseListener.class.getName());
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private Main mainApp;
    private Robot robot;
    private String from;
    private String to;
    private int X;
    private int Y;
    private int width;
    private int height;

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
        PopUp.dispose();
        if (nativeMouseEvent.getClickCount() == 2) {
            X = nativeMouseEvent.getX();
            Y = nativeMouseEvent.getY();
            width = 100;
            height = 30;
            processText();
        }
        if (nativeMouseEvent.getButton() == NativeMouseEvent.BUTTON2) {
            calculatePopUpDimensions();
            processText();
        }
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
    }
    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
    }
    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
    }
    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    void registerHook(Main mainApp) {
        logger.setLevel(Level.WARNING); // disable excessive logging in jnativehook library
        log.log(Level.INFO, "registering new mouse tracker");
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            log.log(Level.SEVERE, "Hook is not registered. Problem occured: " + e.getMessage());
            System.exit(1);
        }
        GlobalScreen.addNativeMouseListener(this);
        this.mainApp = mainApp;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            log.log(Level.SEVERE,"Robot was not created. Problem occured: " + e.getMessage());
            System.exit(1);
        }
    }

    private void processText() {
        getSelected();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            String text;
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                text = (String) clipboard.getData(DataFlavor.stringFlavor);
                translate(text);
            }
            clipboard.setContents(new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[0];
                }
                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return false;
                }
                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    throw new UnsupportedFlavorException(flavor);
                }
            }, null);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
    }

    private void getSelected() {
        log.log(Level.INFO, "copying selected words to System clipboard");
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_C);
    }

    private void translate(String text) {
        log.log(Level.INFO, "translating selected words");
        try {
            Assert.notNull(text, "Text format is not valid");
        } catch (IllegalArgumentException e) {
            PopUp.show(X, Y, e.getMessage(), 200, 50);
        }
        String lang = Languages.languages.get(from) + "-" + Languages.languages.get(to);
        JsonRetrievalTask translatedTextTask = new JsonRetrievalTask(lang, text);
        translatedTextTask.setOnSucceeded(event -> {
            String s = (String) event.getSource().getValue();
            mainApp.getWordsTranslated().add(new Word(text, s));
            PopUp.show(X, Y, s, width, height);
        });
        Platform.runLater(translatedTextTask);
    }

    private void calculatePopUpDimensions() {
        // TODO

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = 500;
        height = 800;
        X = screenSize.width - width;
        Y = screenSize.height - height;
    }
}
