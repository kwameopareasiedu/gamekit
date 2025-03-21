package dev.gamekit.core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** Singleton class which manages the {@link JFrame} the application is rendered in */
public final class Window {
  private static Resolution resolution = Resolution._800_600;
  private static Window instance;

  private final int width;
  private final int height;
  private final int centerX;
  private final int centerY;
  private final double scaleRatio;
  private final JFrame frame;
  private final JPanel renderPanel;
  private BufferedImage sceneTarget;
  private BufferedImage nodeTarget;
  private Graphics2D sceneGraphics;
  private Graphics2D nodeGraphics;

  Window(String title) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    width = resolution == Resolution.NATIVE ? screenSize.width : resolution.minWidth;
    height = resolution == Resolution.NATIVE ? screenSize.height : resolution.minHeight;
    scaleRatio = Math.min(screenSize.getWidth() / width, screenSize.getHeight() / height);
    centerX = width / 2;
    centerY = height / 2;

    createRenderTargets();

    renderPanel = new JPanel();
    renderPanel.setBackground(Color.BLACK);

    frame = new JFrame(title);
    frame.setLocationRelativeTo(null);
    frame.setUndecorated(true);
    frame.setTitle(title);
    frame.add(renderPanel);
    frame.pack();

    // Enter true full screen mode
    GraphicsEnvironment
      .getLocalGraphicsEnvironment()
      .getDefaultScreenDevice()
      .setFullScreenWindow(frame);

    Window.instance = this;
  }

  /**
   * Returns the current instance of the window
   * @return The current window instance
   */
  public static Window getInstance() { return instance; }

  /**
   * Sets the resolution of the {@link Window} before starting the application
   * <p>
   * After the {@code Window} instance is created, this method does nothing
   * @param resolution The resolution of the window
   */
  public static void setResolution(Resolution resolution) {
    if (Window.instance == null) {
      Window.resolution = resolution;
    }
  }

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

  JFrame getFrame() { return frame; }

  Graphics2D getSceneGraphics() { return sceneGraphics; }

  Graphics2D getNodeGraphics() { return nodeGraphics; }

  void redraw() {
    int scaledWidth = (int) (width * scaleRatio);
    int scaledHeight = (int) (height * scaleRatio);
    int dx1 = (int) (0.5 * (frame.getWidth() - scaledWidth));
    int dy1 = (int) (0.5 * (frame.getHeight() - scaledHeight));
    int dx2 = dx1 + scaledWidth;
    int dy2 = dy1 + scaledHeight;
    Graphics2D renderGraphics = (Graphics2D) renderPanel.getGraphics();

    renderGraphics.drawImage(sceneTarget, dx1, dy1, dx2, dy2, 0, 0, width, height, null);
    renderGraphics.drawImage(nodeTarget, dx1, dy1, dx2, dx2, 0, 0, width, height, null);
  }

  void createRenderTargets() {
    sceneTarget = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    sceneGraphics = sceneTarget.createGraphics();
    sceneGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    sceneGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    nodeTarget = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    nodeGraphics = nodeTarget.createGraphics();
    nodeGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    nodeGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
  }

  public static final class Resolution {
    public static final Resolution _640_480 = new Resolution(640, 480);
    public static final Resolution _800_600 = new Resolution(800, 600);
    public static final Resolution _1024_768 = new Resolution(1024, 768);
    public static final Resolution _1280_720 = new Resolution(1280, 720);
    public static final Resolution _1366_768 = new Resolution(1366, 768);
    public static final Resolution _1920_1080 = new Resolution(1920, 1080);
    public static final Resolution NATIVE = new Resolution(-1, -1);

    public final int minWidth;
    public final int minHeight;
    public final double ratio;

    public Resolution(int minWidth, int minHeight) {
      this.minWidth = minWidth;
      this.minHeight = minHeight;
      ratio = (double) minWidth / minHeight;
    }
  }
}
