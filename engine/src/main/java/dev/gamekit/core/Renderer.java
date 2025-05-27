package dev.gamekit.core;

import dev.gamekit.rendering.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/** {@link Renderer} provides draw methods to draw on the current {@link Window} */
@SuppressWarnings("SynchronizeOnNonFinalField")
public final class Renderer {
  private ArrayList<RenderCall> frontBuffer;
  private ArrayList<RenderCall> backBuffer;
  private ArrayList<RenderCall> freeBuffer;

  Renderer() {
    frontBuffer = new ArrayList<>();
    backBuffer = new ArrayList<>();
    freeBuffer = new ArrayList<>();
  }

  /** Clears the view area contents with a specified color */
  public void clear(Color color) {
    frontBuffer.add(new RenderClear(color));
  }

  /** Draws a line from {@code (x1, y1)} to {@code (x2, y2)} */
  public RenderLine drawLine(int x1, int y1, int x2, int y2) {
    RenderLine call = new RenderLine(x1, y1, x2, y2);
    frontBuffer.add(call);
    return call;
  }

  /** Draws a vertical line from {@code (x, y1)} to {@code (x, y2)} */
  public RenderLine drawVerticalLine(int x, int y1, int y2) {
    RenderLine call = new RenderLine(x, y1, x, y2);
    frontBuffer.add(call);
    return call;
  }

  /** Draws a horizontal line from {@code (x1, y)} to {@code (x2, y)} */
  public RenderLine drawHorizontalLine(int x1, int x2, int y) {
    RenderLine call = new RenderLine(x1, y, x2, y);
    frontBuffer.add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> rect at (x, y) with width and height */
  public RenderRect fillRect(int x, int y, int width, int height) {
    RenderRect call = new RenderRect(x, y, width, height, true);
    frontBuffer.add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> rect at (x, y) with width and height */
  public RenderRect drawRect(int x, int y, int width, int height) {
    RenderRect call = new RenderRect(x, y, width, height, false);
    frontBuffer.add(call);
    return call;
  }

  /**
   * Fills a <b>center-origin</b> rounded rect at (x, y) with width, height, arc width and height
   */
  public RenderRoundRect fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    RenderRoundRect call = new RenderRoundRect(x, y, width, height, arcWidth, arcHeight, true);
    frontBuffer.add(call);
    return call;
  }

  /**
   * Draws a <b>center-origin</b> rounded rect at (x, y) with width, height, arc width and height
   */
  public RenderRoundRect drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    RenderRoundRect call = new RenderRoundRect(x, y, width, height, arcWidth, arcHeight, false);
    frontBuffer.add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> oval at (x, y) with width and height */
  public RenderOval fillOval(int x, int y, int width, int height) {
    RenderOval call = new RenderOval(x, y, width, height, true);
    frontBuffer.add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> oval at (x, y) with width and height */
  public RenderOval drawOval(int x, int y, int width, int height) {
    RenderOval call = new RenderOval(x, y, width, height, false);
    frontBuffer.add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> circle at (x, y) with radius */
  public RenderCircle fillCircle(int x, int y, int radius) {
    RenderCircle call = new RenderCircle(x, y, 2 * radius, true);
    frontBuffer.add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> circle at (x, y) with radius */
  public RenderCircle drawCircle(int x, int y, int radius) {
    RenderCircle call = new RenderCircle(x, y, 2 * radius, false);
    frontBuffer.add(call);
    return call;
  }

  /** Draws a scaled <b>center-origin</b> {@link BufferedImage} at (x, y) with width and height */
  public RenderImage drawImage(BufferedImage img, int x, int y, int width, int height) {
    RenderImage call = new RenderImage(img, x, y, width, height);
    frontBuffer.add(call);
    return call;
  }

  void apply(Graphics2D g) {
    synchronized (backBuffer) {
      for (RenderCall call : backBuffer)
        call.apply(g);
    }
  }

  void swapFrontBuffer() {
    synchronized (freeBuffer) {
      ArrayList<RenderCall> tempBuffer = frontBuffer;
      frontBuffer = freeBuffer;
      freeBuffer = tempBuffer;
      frontBuffer.clear();
    }
  }

  void swapBackBuffer() {
    synchronized (freeBuffer) {
      ArrayList<RenderCall> tempBuffer = backBuffer;
      backBuffer = freeBuffer;
      freeBuffer = tempBuffer;
      freeBuffer.clear();
    }
  }
}
