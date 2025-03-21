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
  private static Resolution resolution = Resolution.FULL;
  private static Window instance;

  private final Dimension renderSize;
  private final Point center;
  private final double scaleRatio;
  private final JFrame frame;
  private final JPanel renderPanel;
  private BufferedImage sceneTarget;
  private BufferedImage nodeTarget;
  private Graphics2D sceneGraphics;
  private Graphics2D nodeGraphics;

  Window(String title) {
    renderSize = new Dimension(resolution.width, resolution.height);
    center = new Point(resolution.width / 2, resolution.height / 2);

    renderPanel = new JPanel();
    renderPanel.setBackground(Color.BLACK);

    frame = new JFrame(title);

    if (Window.isFullScreen) {
      frame.setUndecorated(true);

      GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .setFullScreenWindow(frame);

      scaleRatio = Math.min(
        (double) Resolution.FULL.width / resolution.width,
        (double) Resolution.FULL.height / resolution.height
      );
    } else {
      Dimension d = new Dimension(
        resolution.width,
        resolution.height
      );

      frame.setMinimumSize(d);
      frame.setPreferredSize(d);
      frame.setResizable(false);
      scaleRatio = 1;
    }

    frame.setLocationRelativeTo(null);
    frame.add(renderPanel);
    frame.pack();

    createRenderTargets();

    Window.instance = this;

    LOGGER.debug("Resolution: {}x{}", resolution.width, resolution.height);
    LOGGER.debug("Fullscreen: {}", isFullScreen);
    LOGGER.debug("Scale Ratio: {}", scaleRatio);
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
   * Returns the width of the {@link JFrame frame}
   * @return The width of the {@link JFrame frame}
   */
  public int getFrameWidth() { return frame.getWidth(); }

  /**
   * Returns the height of the {@link JFrame frame}
   * @return The height of the {@link JFrame frame}
   */
  public int getFrameHeight() { return frame.getHeight(); }

  /**
   * Returns the width of the render target
   * @return The width of the render target
   */
  public int getRenderWidth() { return renderSize.width; }

  /**
   * Returns the height of the render target
   * @return The height of the render target
   */
  public int getRenderHeight() { return renderSize.height; }

  /**
   * Returns the x component of the center point of the render target
   * @return The x component of the center point of the render target
   */
  public int getCenterX() { return center.x; }

  /**
   * Returns the y component of the center point of the render target
   * @return The y component of the center point of the render target
   */
  public int getCenterY() { return center.y; }

  /**
   * Returns the resolution scale ratio
   * @return The resolution scale ratio
   */
  public double getScaleRatio() { return scaleRatio; }

  JFrame getFrame() { return frame; }

  Graphics2D getSceneGraphics() { return sceneGraphics; }

  Graphics2D getNodeGraphics() { return nodeGraphics; }

  void redraw() {
    Graphics2D renderGraphics = (Graphics2D) renderPanel.getGraphics();

    if (Window.isFullScreen) {
      int scaledWidth = (int) (renderSize.width * scaleRatio);
      int scaledHeight = (int) (renderSize.height * scaleRatio);
      int dx1 = (int) (0.5 * (frame.getWidth() - scaledWidth));
      int dy1 = (int) (0.5 * (frame.getHeight() - scaledHeight));
      int dx2 = dx1 + scaledWidth;
      int dy2 = dy1 + scaledHeight;

      renderGraphics.drawImage(sceneTarget, dx1, dy1, dx2, dy2, 0, 0, renderSize.width, renderSize.height, null);
      renderGraphics.drawImage(nodeTarget, dx1, dy1, dx2, dx2, 0, 0, renderSize.width, renderSize.height, null);
    } else {
      renderGraphics.drawImage(sceneTarget, null, 0, 0);
      renderGraphics.drawImage(nodeTarget, null, 0, 0);
    }
  }

  void createRenderTargets() {
    sceneTarget = new BufferedImage(renderSize.width, renderSize.height, BufferedImage.TYPE_INT_ARGB);
    sceneGraphics = sceneTarget.createGraphics();
    sceneGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    sceneGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    nodeTarget = new BufferedImage(renderSize.width, renderSize.height, BufferedImage.TYPE_INT_ARGB);
    nodeGraphics = nodeTarget.createGraphics();
    nodeGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    nodeGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
  }

  public record Resolution(int width, int height) {
    public static final Resolution _640_480 = new Resolution(640, 480);
    public static final Resolution _800_600 = new Resolution(800, 600);
    public static final Resolution _1024_768 = new Resolution(1024, 768);
    public static final Resolution _1280_720 = new Resolution(1280, 720);
    public static final Resolution _1366_768 = new Resolution(1366, 768);
    public static final Resolution _1920_1080 = new Resolution(1920, 1080);
    public static final Resolution FULL = new Resolution(
      Toolkit.getDefaultToolkit().getScreenSize().width,
      Toolkit.getDefaultToolkit().getScreenSize().height
    );
  }
}
