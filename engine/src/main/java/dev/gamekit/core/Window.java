package dev.gamekit.core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** GameKit's window class in which the game is rendered */
final class Window extends JFrame {
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
  }

  void refresh() {
    windowGraphics.drawImage(screenImage, null, 0, 0);
  }
}
