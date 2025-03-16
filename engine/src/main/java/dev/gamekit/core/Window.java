package dev.gamekit.core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

/**
 * Singleton class which manages the {@link JFrame} the application is rendered in.
 * <p>
 * Window uses a double buffering rendering approach to avoid screen tearing.
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

  /**
   * Returns the current instance of the window
   * @return The current window instance
   */
  public static Window getInstance() { return instance; }

  /**
   * Returns the width of the window
   * @return The width of the window
   */
  public int getWidth() { return width; }

  /**
   * Returns the height of the window
   * @return The height of the window
   */
  public int getHeight() { return height; }

  /**
   * Returns the x component of the center point of the window
   * @return The x component of the center point of the window
   */
  public int getCenterX() { return centerX; }

  /**
   * Returns the y component of the center point of the window
   * @return The y component of the center point of the window
   */
  public int getCenterY() { return centerY; }

  /**
   * Sets the size of the window. If the width or height is less than the minimum size,
   * the minimum width and/or height is used.
   * @param width  The new width of the window
   * @param height The new height of the window
   */
  public void setSize(int width, int height) {
    this.width = Math.max(width, MIN_WIDTH);
    this.height = Math.max(height, MIN_HEIGHT);
    Dimension d = new Dimension(this.width, this.height);
    frame.setPreferredSize(d);
    frame.setSize(d);
    frame.setLocationRelativeTo(null);
  }

  /** Maximizes the window */
  public void maximize() {
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

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
