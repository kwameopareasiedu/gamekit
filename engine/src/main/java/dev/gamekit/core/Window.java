package dev.gamekit.core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

/** Singleton class which manages the {@link JFrame} the application is rendered in */
public final class Window {
  private static final int MIN_WIDTH = 640;
  private static final int MIN_HEIGHT = 480;
  private static Window instance;

  private final JFrame frame;
  private final JPanel panel;
  private BufferedImage image;
  private Graphics2D graphics;
  private int width, height;
  private int centerX, centerY;

  Window(String title) {
    width = MIN_WIDTH;
    height = MIN_HEIGHT;
    centerX = width / 2;
    centerY = height / 2;
    createRenderImage();

    panel = new JPanel();

    frame = new JFrame(title);
    frame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
    frame.setPreferredSize(new Dimension(width, height));
    frame.setLocationRelativeTo(null);
    frame.setTitle(title);
    frame.pack();

    frame.add(panel);
    frame.pack();

    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        super.componentResized(e);

        width = frame.getWidth();
        height = frame.getHeight();
        centerX = width / 2;
        centerY = height / 2;
        createRenderImage();
      }
    });

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

  JFrame getFrame() { return frame; }

  Graphics2D getGraphics() { return graphics; }

  void redraw() {
    var scenePanelGraphics = (Graphics2D) panel.getGraphics();
    scenePanelGraphics.drawImage(image, null, 0, 0);
  }

  void createRenderImage() {
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    graphics = image.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
  }
}
