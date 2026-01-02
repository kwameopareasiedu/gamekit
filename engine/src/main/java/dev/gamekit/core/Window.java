package dev.gamekit.core;

import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.utils.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/** Window which manages the {@link JFrame} and image buffers the application is rendered in */
public final class Window {
  private static final Logger LOGGER = LogManager.getLogger(Window.class);

  private static Window instance;

  private final int displayWidth;
  private final int displayHeight;
  private final JFrame frame;
  private final BufferStrategy bufferStrategy;
  private final BufferedImage displayBuffer;
  private final Graphics2D displayGraphics;
  private final BufferedImage uiBuffer;
  private final Graphics2D uiGraphics;
  private final Info info;
  private final Position[] renderPositions;

  Window() {
    double displayScaleRatio;
    Settings settings = Application.getInstance().getSettings();

    displayWidth = settings.resolution.width();
    displayHeight = settings.resolution.height();
    frame = new JFrame(settings.title);

    if (settings.fullscreen) {
      Dimension d = new Dimension(Resolution.NATIVE.width(), Resolution.NATIVE.height());

      frame.setMinimumSize(d);
      frame.setPreferredSize(d);
      frame.setResizable(false);
      frame.setUndecorated(true);

      GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);

      displayScaleRatio = Math.min(
        (double) Resolution.NATIVE.width() / displayWidth,
        (double) Resolution.NATIVE.height() / displayHeight
      );
    } else {
      Dimension d = new Dimension(displayWidth, displayHeight);
      frame.setMinimumSize(d);
      frame.setPreferredSize(d);
      frame.setResizable(false);
      frame.setUndecorated(settings.undecorated);
      displayScaleRatio = 1;
    }

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

    displayBuffer = gc.createCompatibleImage(displayWidth, displayHeight, Transparency.TRANSLUCENT);
    displayGraphics = displayBuffer.createGraphics();
    settings.antialiasing.apply(displayGraphics);
    settings.alphaInterpolation.apply(displayGraphics);
    settings.imageInterpolation.apply(displayGraphics);
    settings.renderingStrategy.apply(displayGraphics);
    settings.dithering.apply(displayGraphics);

    uiBuffer = gc.createCompatibleImage(displayWidth, displayHeight, Transparency.TRANSLUCENT);
    uiGraphics = uiBuffer.createGraphics();
    settings.antialiasing.apply(uiGraphics);
    settings.alphaInterpolation.apply(uiGraphics);
    settings.imageInterpolation.apply(uiGraphics);
    settings.renderingStrategy.apply(uiGraphics);
    settings.dithering.apply(uiGraphics);

    frame.setIgnoreRepaint(true);
    frame.getContentPane().setBackground(Color.BLACK);
    frame.setLocationRelativeTo(null);
    frame.pack();

    frame.createBufferStrategy(2);
    bufferStrategy = frame.getBufferStrategy();

    info = new Info(
      frame.getWidth(), frame.getHeight(),
      displayWidth, displayHeight,
      displayWidth / 2, displayHeight / 2,
      displayScaleRatio, 1.0 / displayScaleRatio
    );

    int scaledWidth = (int) (displayWidth * displayScaleRatio);
    int scaledHeight = (int) (displayHeight * displayScaleRatio);
    Position topLeft = new Position(
      (int) (0.5 * (frame.getWidth() - scaledWidth)),
      (int) (0.5 * (frame.getHeight() - scaledHeight))
    );
    Position bottomRight = new Position(topLeft.x + scaledWidth, topLeft.y + scaledHeight);

    renderPositions = new Position[]{ topLeft, bottomRight };

    LOGGER.debug("Created window");
    LOGGER.debug(info);

    Window.instance = this;
  }

  /** Returns the current instance of {@link Window} */
  public static Window getInstance() {
    return instance;
  }

  /** Returns the {@link Info} object associated with the current instance of {@link Window} */
  public Info getInfo() {
    return info;
  }

  /** Returns the visible {@link JFrame} of the {@link Window} */
  JFrame getFrame() {
    return frame;
  }

  /** Returns the {@link Graphics2D} of the display buffer to which the scene is rendered */
  Graphics2D getDisplayGraphics() {
    return displayGraphics;
  }

  /** Returns the {@link Graphics2D} of the UI buffer to which UI elements are drawn */
  Graphics2D getUiGraphics() {
    return uiGraphics;
  }

  /** Makes the associated {@link JFrame} visible */
  void show() {
    frame.setVisible(true);
  }

  /** Updates the {@link JFrame} buffer strategy with the display and UI buffers */
  void refresh() {
    Position tl = renderPositions[0];
    Position br = renderPositions[1];

    do {
      Graphics2D bufferGraphics = (Graphics2D) bufferStrategy.getDrawGraphics();
      bufferGraphics.drawImage(displayBuffer, tl.x, tl.y, br.x, br.y, 0, 0, displayWidth, displayHeight, null);
      bufferGraphics.drawImage(uiBuffer, tl.x, tl.y, br.x, br.y, 0, 0, displayWidth, displayHeight, null);
      bufferStrategy.show();
      bufferGraphics.dispose();
    } while (bufferStrategy.contentsLost());
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
        "%s[frame=(%dx%d), display=(%dx%d), center=[%d,%d], scaleRatio=%.2f, inverseScaleRatio=%.2f]",
        getClass().getName(),
        frameWidth, frameHeight,
        displayWidth, displayHeight,
        displayCenterX, displayCenterY,
        displayScaleRatio, inverseDisplayScaleRatio
      );
    }
  }
}
