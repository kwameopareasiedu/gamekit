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
  private static Info info;

  private final int displayWidth;
  private final int displayHeight;
  private final double displayScaleRatio;
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

    displayWidth = settings.resolution.width();
    displayHeight = settings.resolution.height();
    frame = new JFrame(settings.title);

    if (settings.fullScreen) {
      frame.setUndecorated(true);

      GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice()
        .setFullScreenWindow(frame);

      Dimension d = new Dimension(
        Resolution.NATIVE.width(),
        Resolution.NATIVE.height()
      );

      frame.setMinimumSize(d);
      frame.setPreferredSize(d);
      frame.setResizable(false);

      displayScaleRatio = Math.min(
        (double) Resolution.NATIVE.width() / displayWidth,
        (double) Resolution.NATIVE.height() / displayHeight
      );
    } else {
      Dimension d = new Dimension(displayWidth, displayHeight);
      frame.setMinimumSize(d);
      frame.setPreferredSize(d);
      frame.setResizable(false);
      displayScaleRatio = 1;
    }

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

    int renderWidth = settings.fullScreen ? Resolution.NATIVE.width() : displayWidth;
    int renderHeight = settings.fullScreen ? Resolution.NATIVE.height() : displayHeight;
    renderBuffer = new BufferedImage(renderWidth, renderHeight, BufferedImage.TYPE_INT_ARGB);
    renderGraphics = renderBuffer.createGraphics();

    frame.getContentPane().setBackground(Color.BLACK);
    frame.setLocationRelativeTo(null);
    frame.pack();

    info = new Info(
      frame.getWidth(), frame.getHeight(),
      displayWidth, displayHeight,
      displayWidth / 2, displayHeight / 2,
      displayScaleRatio, 1.0 / displayScaleRatio
    );

    LOGGER.debug(info);
  }

  static Window getInstance() { return instance; }

  public static Info getInfo() { return info; }

  JFrame getFrame() { return frame; }

  Graphics2D getDisplayGraphics() { return displayGraphics; }

  Graphics2D getUiGraphics() { return uiGraphics; }

  void show() { frame.setVisible(true); }

  void refresh() {
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

  /** {@link Window.Info} holds the read-only parameters of the current {@link Window instance} */
  public record Info(
    int frameWidth,
    int frameHeight,
    int displayWidth,
    int displayHeight,
    int displayCenterX,
    int displayCenterY,
    double displayScaleRatio,
    double inverseDisplayScaleRatio
  ) {
    @Override
    public String toString() {
      return String.format(
        "%s[frameWidth=%d,frameHeight=%d,displayWidth=%d,displayHeight=%d" +
          "centerX=%d,centerY=%d,scaleRatio=%.2f,inverseScaleRatio=%.2f]",
        getClass().getName(),
        frameWidth, frameHeight,
        displayWidth, displayHeight,
        displayCenterX, displayCenterY,
        displayScaleRatio, inverseDisplayScaleRatio
      );
    }
  }
}
