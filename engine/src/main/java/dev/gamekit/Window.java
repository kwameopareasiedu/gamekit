package dev.gamekit;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import static dev.gamekit.Utils.toInt;

/** GameKit's window class in which the game is rendered */
public class Window extends JFrame {
  final Graphics2D windowGraphics;
  final BufferedImage screenImage;
  final Graphics2D screenGraphics;
  final AffineTransform screenTransform;
  private final int width, height;

  Window(String title, int width, int height) {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setPreferredSize(new Dimension(width, height));
    setSize(width, height);
    setResizable(false);
    setTitle(title);
    pack();

    windowGraphics = (Graphics2D) super.getGraphics();
    screenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    screenTransform = new AffineTransform(1, 0, 0, -1, 0.5 * width, 0.5 * height);
    screenGraphics = screenImage.createGraphics();

    this.width = width;
    this.height = height;
  }

  void clearScreen(Color color) {
    screenGraphics.setBackground(color);
    screenGraphics.setTransform(screenTransform);
    screenGraphics.clearRect(toInt(-0.5 * width), toInt(-0.5 * height), width, height);
  }

  void refresh() {
    windowGraphics.drawImage(screenImage, null, 0, 0);
  }
}
