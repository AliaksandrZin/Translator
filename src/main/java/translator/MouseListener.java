package translator;

import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import translator.api.JsonRetrievalTask;
import translator.model.Text;
import translator.util.Assert;
import translator.util.Languages;
import translator.util.PopUp;

import javax.json.JsonObject;
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
    private long dragStart;
    private int X;
    private int Y;
    private Dimension[] dimensions = new Dimension[2];
    private boolean dragged;

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
        PopUp.dispose();
        if (nativeMouseEvent.getClickCount() == 2) {
            PopUp.dispose();
            X = nativeMouseEvent.getX();
            Y = nativeMouseEvent.getY();
            dimensions = calculatePopUpDimensions(true);
            processText();
        }
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
        PopUp.dispose();
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        PopUp.dispose();
        if (dragged & (nativeMouseEvent.getWhen() - dragStart) > 200) {
            dimensions = calculatePopUpDimensions(false);
            processText();
        }
        dragStart = 0;
        dragged = false;
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
        if (dragStart == 0) dragStart = nativeMouseEvent.getWhen();
        dragged = true;
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
            log.log(Level.SEVERE, "Hook is not registered. Problem occured: {0}", e.getMessage());
            System.exit(1);
        }
        GlobalScreen.addNativeMouseListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);
        this.mainApp = mainApp;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            log.log(Level.SEVERE,"Robot was not created. Problem occured: {0}", e.getMessage());
            System.exit(1);
        }
    }

    private void processText() {
        getSelected();
        try {
            Thread.sleep(150);
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
            PopUp.show(X, Y, e.getMessage(), calculatePopUpDimensions(false));
        }
        String lang = Languages.languages.get(from) + "-" + Languages.languages.get(to);
        JsonRetrievalTask translatedTextTask = new JsonRetrievalTask(lang, text);
        translatedTextTask.setOnSucceeded(event -> {
            String s = ((JsonObject) event.getSource().getValue()).getJsonArray("text").get(0).toString();
            s = s.substring(1, s.length()-1).replaceAll("\\\\n", " ");
            mainApp.getWordsTranslated().add(new Text(text, s));
            PopUp.show(X, Y, s, dimensions);
            event.getSource().cancel();
        });
        Platform.runLater(translatedTextTask);
    }

    private Dimension[] calculatePopUpDimensions(boolean leftClick) {
        if (leftClick) {
            return new Dimension[]{new Dimension(100, 30), new Dimension(100, 30)};
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width/6;
        Dimension[] dimension = {new Dimension(width, screenSize.height), new Dimension(width, 400)};
        X = screenSize.width - width - 100;
        Y = screenSize.height;
        return dimension;
    }
}
