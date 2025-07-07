package dev.gamekit.core;

import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/** Window which manages the {@link JFrame} and image buffers the application is rendered in */
public final class Window {
  private static final Logger LOGGER = LogManager.getLogger(Window.class);

  private static Window instance;

  private final int displayWidth;
  private final int displayHeight;
  private final int displayCenterX;
  private final int displayCenterY;
  private final double displayScaleRatio;
  private final double inverseDisplayScaleRatio;
  private final JFrame frame;
  private final BufferedImage renderBuffer;
  private final Graphics2D renderGraphics;
  private final BufferedImage displayBuffer;
  private final Graphics2D displayGraphics;
  private final BufferedImage uiBuffer;
  private final Graphics2D uiGraphics;

  Window() {
    LOGGER.debug("Created window");

    Window.instance = this;

    Settings settings = Application.getInstance().getSettings();

    displayWidth = settings.resolution.width;
    displayHeight = settings.resolution.height;
    displayCenterX = settings.resolution.width / 2;
    displayCenterY = settings.resolution.height / 2;

    frame = new JFrame(settings.title);

    if (settings.fullScreen) {
      frame.setUndecorated(true);

      GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .setFullScreenWindow(frame);

      displayScaleRatio = Math.min(
        (double) Resolution.NATIVE.width / settings.resolution.width,
        (double) Resolution.NATIVE.height / settings.resolution.height
      );
    } else {
      Dimension d = new Dimension(
        settings.resolution.width,
        settings.resolution.height
      );

      frame.setMinimumSize(d);
      frame.setPreferredSize(d);
      frame.setResizable(false);
      displayScaleRatio = 1;
    }

    inverseDisplayScaleRatio = 1.0 / displayScaleRatio;

    displayBuffer = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_ARGB);
    displayGraphics = displayBuffer.createGraphics();
    settings.antialiasing.apply(displayGraphics);
    settings.alphaInterpolation.apply(displayGraphics);
    settings.imageInterpolation.apply(displayGraphics);
    settings.renderingStrategy.apply(displayGraphics);
    settings.dithering.apply(displayGraphics);

    uiBuffer = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_ARGB);
    uiGraphics = uiBuffer.createGraphics();
    settings.antialiasing.apply(uiGraphics);
    settings.alphaInterpolation.apply(uiGraphics);
    settings.imageInterpolation.apply(uiGraphics);
    settings.renderingStrategy.apply(uiGraphics);
    settings.dithering.apply(uiGraphics);

    int renderWidth = settings.fullScreen ? Resolution.NATIVE.width : displayWidth;
    int renderHeight = settings.fullScreen ? Resolution.NATIVE.height : displayHeight;
    renderBuffer = new BufferedImage(renderWidth, renderHeight, BufferedImage.TYPE_INT_ARGB);
    renderGraphics = renderBuffer.createGraphics();

    frame.getContentPane().setBackground(Color.BLACK);
    frame.setLocationRelativeTo(null);
    frame.pack();
  }

  public static Window getInstance() { return instance; }

  public int getFrameWidth() { return frame.getWidth(); }

  public int getFrameHeight() { return frame.getHeight(); }

  public int getDisplayWidth() { return displayWidth; }

  public int getDisplayHeight() { return displayHeight; }

  public int getDisplayCenterX() { return displayCenterX; }

  public int getDisplayCenterY() { return displayCenterY; }

  public double getDisplayScaleRatio() { return displayScaleRatio; }

  public double getInverseDisplayScaleRatio() { return inverseDisplayScaleRatio; }

  JFrame getFrame() { return frame; }

  Graphics2D getDisplayGraphics() { return displayGraphics; }

  Graphics2D getUiGraphics() { return uiGraphics; }

  void draw() {
    Settings settings = Application.getInstance().getSettings();

    if (settings.fullScreen) {
      int scaledWidth = (int) (displayWidth * displayScaleRatio);
      int scaledHeight = (int) (displayHeight * displayScaleRatio);
      int dx1 = (int) (0.5 * (frame.getWidth() - scaledWidth));
      int dy1 = (int) (0.5 * (frame.getHeight() - scaledHeight));
      int dx2 = dx1 + scaledWidth;
      int dy2 = dy1 + scaledHeight;

      renderGraphics.drawImage(
        displayBuffer, dx1, dy1, dx2, dy2,
        0, 0, displayWidth, displayHeight, null
      );

      renderGraphics.drawImage(
        uiBuffer, dx1, dy1, dx2, dy2,
        0, 0, displayWidth, displayHeight, null
      );
    } else {
      renderGraphics.drawImage(displayBuffer, null, 0, 0);
      renderGraphics.drawImage(uiBuffer, null, 0, 0);
    }

    Graphics2D frameGraphics = (Graphics2D) frame.getGraphics();
    frameGraphics.drawImage(renderBuffer, null, 0, 0);
  }
}
