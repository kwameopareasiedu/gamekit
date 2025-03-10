package dev.gamekit.core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** GameKit's window class in which the game is rendered */
public final class Window extends JFrame {
  static Window INSTANCE;

  final Graphics2D windowGraphics;
  final Graphics2D screenGraphics;
  final BufferedImage screenImage;

  Window(String title, int width, int height) {
    setPreferredSize(new Dimension(width, height));
    setSize(width, height);
    setResizable(false);
    setTitle(title);
    pack();

    windowGraphics = (Graphics2D) super.getGraphics();
    screenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    screenGraphics = screenImage.createGraphics();

    Window.INSTANCE = this;
  }

  public static Window getInstance() {
    return INSTANCE;
  }

  void refresh() {
    windowGraphics.drawImage(screenImage, null, 0, 0);
  }
}
