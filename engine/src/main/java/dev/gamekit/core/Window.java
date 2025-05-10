package dev.gamekit.core;

import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.utils.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** Window which manages the {@link JFrame} and image buffers the application is rendered in */
public final class Window {
  private static final Logger LOGGER = LogManager.getLogger();

  private static Window instance;

  private final Dimension displaySize;
  private final Position center;
  private final double displayScaleRatio;
  private final double inverseDisplayScaleRatio;
  private final JFrame frame;
  private BufferedImage renderBuffer;
  private Graphics2D renderGraphics;
  private BufferedImage sceneBuffer;
  private Graphics2D sceneGraphics;
  private BufferedImage uiBuffer;
  private Graphics2D uiGraphics;

  Window() {
    LOGGER.debug("Creating window");

    Window.instance = this;

    Settings settings = Application.getInstance().getSettings();

    displaySize = new Dimension(
      settings.resolution().width,
      settings.resolution().height
    );

    center = new Position(
      settings.resolution().width / 2,
      settings.resolution().height / 2
    );

    frame = new JFrame(settings.title());
    frame.getContentPane().setBackground(Color.BLACK);

    if (settings.fullScreen()) {
      frame.setUndecorated(true);

      GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .setFullScreenWindow(frame);

      displayScaleRatio = Math.min(
        (double) Resolution.NATIVE.width / settings.resolution().width,
        (double) Resolution.NATIVE.height / settings.resolution().height
      );
    } else {
      Dimension d = new Dimension(
        settings.resolution().width,
        settings.resolution().height
      );

      frame.setMinimumSize(d);
      frame.setPreferredSize(d);
      frame.setResizable(false);
      displayScaleRatio = 1;
    }

    inverseDisplayScaleRatio = 1.0 / displayScaleRatio;

    frame.setLocationRelativeTo(null);
    frame.setBackground(Color.BLACK);
    frame.pack();

    createRenderBuffers();
  }

  public static Window getInstance() { return instance; }

  /** Returns the width of the {@link JFrame frame} */
  public int getFrameWidth() { return frame.getWidth(); }

  /** Returns the height of the {@link JFrame frame} */
  public int getFrameHeight() { return frame.getHeight(); }

  /** Returns the width of the <b>scene</b> buffer */
  public int getDisplayWidth() { return displaySize.width; }

  /** Returns the height of the <b>scene</b> buffer */
  public int getDisplayHeight() { return displaySize.height; }

  public Position getCenter() { return center; }

  public double getDisplayScaleRatio() { return displayScaleRatio; }

  public double getInverseDisplayScaleRatio() { return inverseDisplayScaleRatio; }

  JFrame getFrame() { return frame; }

  Graphics2D getSceneGraphics() { return sceneGraphics; }

  Graphics2D getUiGraphics() { return uiGraphics; }

  void render() {
    Settings settings = Application.getInstance().getSettings();

    if (settings.fullScreen()) {
      int scaledWidth = (int) (displaySize.width * displayScaleRatio);
      int scaledHeight = (int) (displaySize.height * displayScaleRatio);
      int dx1 = (int) (0.5 * (frame.getWidth() - scaledWidth));
      int dy1 = (int) (0.5 * (frame.getHeight() - scaledHeight));
      int dx2 = dx1 + scaledWidth;
      int dy2 = dy1 + scaledHeight;

      renderGraphics.drawImage(
        sceneBuffer, dx1, dy1, dx2, dy2,
        0, 0, displaySize.width, displaySize.height, null
      );

      renderGraphics.drawImage(
        uiBuffer, dx1, dy1, dx2, dy2,
        0, 0, displaySize.width, displaySize.height, null
      );
    } else {
      renderGraphics.drawImage(sceneBuffer, null, 0, 0);
      renderGraphics.drawImage(uiBuffer, null, 0, 0);
    }

    Graphics2D frameGraphics = (Graphics2D) frame.getGraphics();
    frameGraphics.drawImage(renderBuffer, null, 0, 0);
  }

  void createRenderBuffers() {
    Settings settings = Application.getInstance().getSettings();
    int displayWidth = displaySize.width;
    int displayHeight = displaySize.height;

    sceneBuffer = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_ARGB);
    sceneGraphics = sceneBuffer.createGraphics();
    settings.antialiasing().apply(sceneGraphics);
    settings.alphaInterpolation().apply(sceneGraphics);
    settings.imageInterpolation().apply(sceneGraphics);
    settings.renderingStrategy().apply(sceneGraphics);
    settings.dithering().apply(sceneGraphics);

    uiBuffer = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_ARGB);
    uiGraphics = uiBuffer.createGraphics();
    settings.antialiasing().apply(uiGraphics);
    settings.alphaInterpolation().apply(uiGraphics);
    settings.imageInterpolation().apply(uiGraphics);
    settings.renderingStrategy().apply(uiGraphics);
    settings.dithering().apply(uiGraphics);

    int renderWidth = settings.fullScreen() ? Resolution.NATIVE.width : displaySize.width;
    int renderHeight = settings.fullScreen() ? Resolution.NATIVE.height : displaySize.height;
    renderBuffer = new BufferedImage(renderWidth, renderHeight, BufferedImage.TYPE_INT_ARGB);
    renderGraphics = renderBuffer.createGraphics();
  }
}
