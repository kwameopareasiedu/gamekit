package dev.gamekit.core;

import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/** Window manages the {@link JFrame} and image buffers the application is rendered in */
public final class Window {
  private static final Logger LOGGER = LogManager.getLogger(Window.class);

  private static Window instance;

  private final int displayWidth;
  private final int displayHeight;
  private final int centerX;
  private final int centerY;
  private final JFrame frame;
  private final Canvas canvas;
  private final BufferStrategy bufferStrategy;
  private final BufferedImage displayBuffer;
  private final Graphics2D displayGraphics;
  private final BufferedImage uiBuffer;
  private final Graphics2D uiGraphics;

  Window() {
    Settings settings = Application.getInstance().getSettings();
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

    displayWidth = settings.resolution.width();
    displayHeight = settings.resolution.height();
    centerX = displayWidth / 2;
    centerY = displayHeight / 2;
    frame = new JFrame(settings.title);
    canvas = new Canvas();

    if (settings.fullscreen) {
      Dimension d = new Dimension(Resolution.NATIVE.width(), Resolution.NATIVE.height());

      frame.setMinimumSize(d);
      frame.setPreferredSize(d);
      frame.setResizable(false);
      frame.setUndecorated(true);

      ge.getDefaultScreenDevice().setFullScreenWindow(frame);
    } else {
      Dimension d = new Dimension(displayWidth, displayHeight);

      frame.setMinimumSize(d);
      frame.setPreferredSize(d);
      frame.setResizable(false);
      frame.setUndecorated(settings.undecorated);
    }

    Dimension d = new Dimension(displayWidth, displayHeight);
    canvas.setSize(d);
    canvas.setMinimumSize(d);
    canvas.setMaximumSize(d);
    canvas.setPreferredSize(d);
    canvas.setFocusTraversalKeysEnabled(false);
    canvas.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        canvas.requestFocusInWindow();
      }
    });

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
    frame.setLayout(new GridBagLayout());
    frame.setBackground(Color.BLACK);
    frame.getContentPane().add(canvas);
    frame.setLocationRelativeTo(null);
    frame.pack();

    canvas.createBufferStrategy(2);
    bufferStrategy = canvas.getBufferStrategy();

    LOGGER.debug("Created window");
    LOGGER.debug(
      "displayWidth={}, displayHeight={}, centerX={}, centerY={}",
      displayWidth, displayHeight, centerX, centerY
    );

    Window.instance = this;
  }

  /** Returns the current instance of {@link Window} */
  public static Window getInstance() {
    return instance;
  }

  /** Returns the display width of the {@link Window} */
  public int getDisplayWidth() {
    return displayWidth;
  }

  /** Returns the display height of the {@link Window} */
  public int getDisplayHeight() {
    return displayHeight;
  }

  /** Returns the x-coordinate of the center point of the {@link Window} */
  public int getCenterX() {
    return centerX;
  }

  /** Returns the y-coordinate of the center point of the {@link Window} */
  public int getCenterY() {
    return centerY;
  }

  /** Returns the visible {@link JFrame} of the {@link Window} */
  JFrame getFrame() {
    return frame;
  }

  /** Returns the visible {@link Canvas} of the {@link Window} */
  Canvas getCanvas() {
    return canvas;
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
    canvas.requestFocus();
  }

  /** Updates the {@link JFrame} buffer strategy with the display and UI buffers */
  void update() {
    do {
      Graphics2D bufferGraphics = (Graphics2D) bufferStrategy.getDrawGraphics();
      bufferGraphics.drawImage(displayBuffer, 0, 0, displayWidth, displayHeight, null);
      bufferGraphics.drawImage(uiBuffer, 0, 0, displayWidth, displayHeight, null);
      bufferStrategy.show();
      bufferGraphics.dispose();
    } while (bufferStrategy.contentsLost());
  }
}
