package translator.util;

import javax.swing.*;

public class PopUp {

    private PopUp() {}

    private static JFrame frame = new JFrame();

    public static void show(int x, int y, String text, int width, int height) {
        frame = new JFrame();
        JLabel label = new JLabel(text);
        frame.setSize(width, height);
        frame.setUndecorated(true);
        frame.add(label);
        frame.setLocation(x, y-40);
        frame.setVisible(true);
    }

    public static void dispose() {
        frame.dispose();
    }
}
