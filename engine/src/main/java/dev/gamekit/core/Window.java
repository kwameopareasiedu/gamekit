package dev.gamekit.core;

import dev.gamekit.utils.Config;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Resolution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** Singleton class which manages the {@link JFrame} the application is rendered in */
public final class Window {
  private static final Logger LOGGER = LogManager.getLogger();

  private static Window instance;

  private final Config config;
  private final Dimension renderSize;
  private final Position center;
  private final double scaleRatio;
  private final double inverseScaleRatio;
  private final JFrame frame;
  private BufferedImage renderBuffer;
  private Graphics2D renderGraphics;
  private BufferedImage sceneBuffer;
  private Graphics2D sceneGraphics;
  private BufferedImage uiBuffer;
  private Graphics2D uiGraphics;

  Window(Config config) {
    this.config = config;

    renderSize = new Dimension(config.resolution().width(), config.resolution().height());
    center = new Position(config.resolution().width() / 2, config.resolution().height() / 2);

    frame = new JFrame(config.title());
    frame.getContentPane().setBackground(Color.BLACK);

    if (config.isFullScreen()) {
      frame.setUndecorated(true);

      GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .setFullScreenWindow(frame);

      scaleRatio = Math.min(
        (double) Resolution.NATIVE.width() / config.resolution().width(),
        (double) Resolution.NATIVE.height() / config.resolution().height()
      );
    } else {
      Dimension d = new Dimension(
        config.resolution().width(),
        config.resolution().height()
      );

      frame.setMinimumSize(d);
      frame.setPreferredSize(d);
      frame.setResizable(false);
      scaleRatio = 1;
    }

    inverseScaleRatio = 1.0 / scaleRatio;

    frame.setLocationRelativeTo(null);
    frame.setBackground(Color.BLACK);
    frame.pack();

    createRenderBuffers();

    Window.instance = this;

    LOGGER.debug("Scale Ratio: {}", scaleRatio);
  }

  public static Window getInstance() { return instance; }

  /** Returns the width of the {@link JFrame frame} */
  public int getFrameWidth() { return frame.getWidth(); }

  /** Returns the height of the {@link JFrame frame} */
  public int getFrameHeight() { return frame.getHeight(); }

  /** Returns the width of the render buffer */
  public int getRenderWidth() { return renderSize.width; }

  /** Returns the height of the render buffer */
  public int getRenderHeight() { return renderSize.height; }

  /** Returns the x component of the center point of the render buffer */
  public int getCenterX() { return center.x; }

  /** Returns the y component of the center point of the render buffer */
  public int getCenterY() { return center.y; }

  /** Returns the resolution scale ratio */
  public double getScaleRatio() { return scaleRatio; }

  /** Returns the inverse of the resolution scale ratio */
  public double getInverseScaleRatio() { return inverseScaleRatio; }

  JFrame getFrame() { return frame; }

  Graphics2D getSceneGraphics() { return sceneGraphics; }

  Graphics2D getUiGraphics() { return uiGraphics; }

  void redraw() {
    if (config.isFullScreen()) {
      int scaledWidth = (int) (renderSize.width * scaleRatio);
      int scaledHeight = (int) (renderSize.height * scaleRatio);
      int dx1 = (int) (0.5 * (frame.getWidth() - scaledWidth));
      int dy1 = (int) (0.5 * (frame.getHeight() - scaledHeight));
      int dx2 = dx1 + scaledWidth;
      int dy2 = dy1 + scaledHeight;

      renderGraphics.drawImage(sceneBuffer, dx1, dy1, dx2, dy2, 0, 0, renderSize.width, renderSize.height, null);
      renderGraphics.drawImage(uiBuffer, dx1, dy1, dx2, dy2, 0, 0, renderSize.width, renderSize.height, null);
    } else {
      renderGraphics.drawImage(sceneBuffer, null, 0, 0);
      renderGraphics.drawImage(uiBuffer, null, 0, 0);
    }

    Graphics2D frameGraphics = (Graphics2D) frame.getGraphics();
    frameGraphics.drawImage(renderBuffer, null, 0, 0);
  }

  void createRenderBuffers() {
    sceneBuffer = new BufferedImage(renderSize.width, renderSize.height, BufferedImage.TYPE_INT_ARGB);
    sceneGraphics = sceneBuffer.createGraphics();
    sceneGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    sceneGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    uiBuffer = new BufferedImage(renderSize.width, renderSize.height, BufferedImage.TYPE_INT_ARGB);
    uiGraphics = uiBuffer.createGraphics();
    uiGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    uiGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    renderBuffer = new BufferedImage(Resolution.NATIVE.width(), Resolution.NATIVE.height(), BufferedImage.TYPE_INT_ARGB);
    renderGraphics = renderBuffer.createGraphics();
  }
}
