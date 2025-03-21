package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** Singleton class which manages the {@link JFrame} the application is rendered in */
public final class Window {
  private static final Logger LOGGER = LogManager.getLogger();

  private static boolean isFullScreen = false;
  private static Resolution resolution = Resolution._800_600;
  private static Window instance;

  private final int width;
  private final int height;
  //  private final Dimension renderSize;
  //  private final Dimension frameSize;
  private final int centerX;
  private final int centerY;
  //  private final Point center;
  private final double scaleRatio;
  private final JFrame frame;
  private final JPanel renderPanel;
  private BufferedImage sceneTarget;
  private BufferedImage nodeTarget;
  private Graphics2D sceneGraphics;
  private Graphics2D nodeGraphics;

  Window(String title) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    width = resolution == Resolution.NATIVE ? screenSize.width : resolution.renderWidth;
    height = resolution == Resolution.NATIVE ? screenSize.height : resolution.renderHeight;
    scaleRatio = Math.min(screenSize.getWidth() / width, screenSize.getHeight() / height);
    centerX = width / 2;
    centerY = height / 2;

    LOGGER.debug("Resolution: {}x{}", width, height);
    LOGGER.debug("Fullscreen: {}", isFullScreen);
    createRenderTargets();

    renderPanel = new JPanel();
    renderPanel.setBackground(Color.BLACK);

    frame = new JFrame(title);
    frame.setMinimumSize(new Dimension(width, height));
    frame.setLocationRelativeTo(null);
    frame.setTitle(title);

    if (Window.isFullScreen) {
      frame.setUndecorated(true);
      GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .setFullScreenWindow(frame);
    } else {
      frame.setPreferredSize(new Dimension(width, height));
      frame.setResizable(false);
    }

    frame.add(renderPanel);
    frame.pack();

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
   * Sets whether the {@link Window} should launch in full screen or in windowed mode
   * <p>
   * After the {@code Window} instance is created, this method does nothing
   * @param fullscreen The fullscreen state
   */
  public static void setFullscreen(boolean fullscreen) {
    if (Window.instance == null) {
      Window.isFullScreen = fullscreen;
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
    Graphics2D renderGraphics = (Graphics2D) renderPanel.getGraphics();

    if (Window.isFullScreen) {
      int scaledWidth = (int) (width * scaleRatio);
      int scaledHeight = (int) (height * scaleRatio);
      int dx1 = (int) (0.5 * (frame.getWidth() - scaledWidth));
      int dy1 = (int) (0.5 * (frame.getHeight() - scaledHeight));
      int dx2 = dx1 + scaledWidth;
      int dy2 = dy1 + scaledHeight;

      renderGraphics.drawImage(sceneTarget, dx1, dy1, dx2, dy2, 0, 0, width, height, null);
      renderGraphics.drawImage(nodeTarget, dx1, dy1, dx2, dx2, 0, 0, width, height, null);
    } else {
      renderGraphics.drawImage(sceneTarget, null, 0, 0);
      renderGraphics.drawImage(nodeTarget, null, 0, 0);
    }
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

    public final int renderWidth;
    public final int renderHeight;
    public final double ratio;

    public Resolution(int renderWidth, int renderHeight) {
      this.renderWidth = renderWidth;
      this.renderHeight = renderHeight;
      ratio = (double) renderWidth / renderHeight;
    }
  }
}
