package dev.gamekit.core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

/**
 * GameKit's window class in which the game is rendered.
 * <p>
 * Window uses double buffering with two {@link BufferedImage} objects.
 * One to draw the world state and another for world text rendering.
 */
public final class Window {
  private static final int MIN_WIDTH = 640;
  private static final int MIN_HEIGHT = 480;
  private static Window instance;

  final JFrame frame;
  private final Graphics2D windowGraphics;
  private BufferedImage sceneLayer;
  private BufferedImage screenLayer;
  private Graphics2D sceneGraphics;
  private Graphics2D screenGraphics;
  private int width, height;
  private int centerX, centerY;

  Window(String title) {
    width = MIN_WIDTH;
    height = MIN_HEIGHT;
    centerX = width / 2;
    centerY = height / 2;
    createRenderLayers();

    frame = new JFrame(title);
    frame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
    frame.setPreferredSize(new Dimension(width, height));
    // frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setLocationRelativeTo(null);
    frame.setTitle(title);
    frame.pack();

    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        super.componentResized(e);

        width = frame.getWidth();
        height = frame.getHeight();
        centerX = width / 2;
        centerY = height / 2;
        createRenderLayers();
      }
    });

    windowGraphics = (Graphics2D) frame.getGraphics();

    Window.instance = this;
  }

  public static Window getInstance() { return instance; }

  public int getWidth() { return width; }

  public int getHeight() { return height; }

  public int getCenterX() { return centerX; }

  public int getCenterY() { return centerY; }

  Graphics2D getSceneGraphics() { return sceneGraphics; }

  Graphics2D getScreenGraphics() { return screenGraphics; }

  void redraw() {
    windowGraphics.drawImage(sceneLayer, null, 0, 0);
    windowGraphics.drawImage(screenLayer, null, 0, 0);
  }

  void createRenderLayers() {
    sceneLayer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    sceneGraphics = sceneLayer.createGraphics();
    sceneGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    screenLayer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    screenGraphics = screenLayer.createGraphics();
    screenGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  }
}
