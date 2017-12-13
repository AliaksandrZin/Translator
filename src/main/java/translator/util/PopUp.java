package translator.util;

import javax.swing.*;
import java.awt.*;

public class PopUp {

    private PopUp() {}

    private static JFrame frame = new JFrame();
    private final static String html1 = "<html><body style='margin:2px 2px; width:";
    private final static String html2 = "px'>";

    public static void show(int x, int y, String text, Dimension[] dimensions) {
        frame = new JFrame();
        JLabel label = new JLabel(html1 + dimensions[0].width + html2 + text);
        frame.setUndecorated(true);
        frame.getContentPane().add(label);
        frame.setMinimumSize(dimensions[1]);
        frame.pack();
        frame.setLocation(x, y - label.getSize().height);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    public static void dispose() {
        frame.dispose();
    }

    public static JFrame getFrame() {
        return frame;
    }
}
