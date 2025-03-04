package dev.gamekit;

import javax.swing.*;
import java.awt.*;

/** The GUI frame in which the GameKit game will be displayed in */
public class Window extends JFrame {
  private static Window instance;

  public Screen screen;

  private final Graphics2D windowGraphics;

  Window(String title, int width, int height) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(width, height));
    setSize(width, height);

    setTitle(title);
    pack();

    windowGraphics = (Graphics2D) super.getGraphics();
    Window.instance = this;
  }

  public static Window getInstance() {
    return instance;
  }

  void refresh() {
    if (screen != null) {
      windowGraphics.drawImage(screen, null, 0, 0);
    }
  }
}
