package dev.gamekit.core;

import dev.gamekit.graphics.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * {@link Renderer} dispatches {@link DrawCall draw calls} which are processed by the render
 * thread to draw on the current {@link Window}
 */
public final class Renderer {
  private final ArrayList<DrawCall> buffer;

  boolean committed;
  boolean completed;

  Renderer() {
    buffer = new ArrayList<>();
    committed = completed = false;
  }

  /** Clears the view area contents with a specified color */
  public void clear(Color color) {
    buffer.add(new ClearCall(color));
  }

  /** Draws a line from {@code (x1, y1)} to {@code (x2, y2)} */
  public DrawLine drawLine(int x1, int y1, int x2, int y2) {
    DrawLine call = new DrawLine(x1, y1, x2, y2);
    buffer.add(call);
    return call;
  }

  /** Draws a vertical line from {@code (x, y1)} to {@code (x, y2)} */
  public DrawLine drawVerticalLine(int x, int y1, int y2) {
    DrawLine call = new DrawLine(x, y1, x, y2);
    buffer.add(call);
    return call;
  }

  /** Draws a horizontal line from {@code (x1, y)} to {@code (x2, y)} */
  public DrawLine drawHorizontalLine(int x1, int x2, int y) {
    DrawLine call = new DrawLine(x1, y, x2, y);
    buffer.add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> rect at (x, y) with width and height */
  public DrawRect fillRect(int x, int y, int width, int height) {
    DrawRect call = new DrawRect(x, y, width, height, true);
    buffer.add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> rect at (x, y) with width and height */
  public DrawRect drawRect(int x, int y, int width, int height) {
    DrawRect call = new DrawRect(x, y, width, height, false);
    buffer.add(call);
    return call;
  }

  /**
   * Fills a <b>center-origin</b> rounded rect at (x, y) with width, height, arc width and height
   */
  public DrawRoundRect fillRoundRect(
    int x, int y, int width, int height, int arcWidth, int arcHeight
  ) {
    DrawRoundRect call = new DrawRoundRect(x, y, width, height, arcWidth, arcHeight, true);
    buffer.add(call);
    return call;
  }

  /**
   * Draws a <b>center-origin</b> rounded rect at (x, y) with width, height, arc width and height
   */
  public DrawRoundRect drawRoundRect(
    int x, int y, int width, int height, int arcWidth, int arcHeight
  ) {
    DrawRoundRect call = new DrawRoundRect(x, y, width, height, arcWidth, arcHeight, false);
    buffer.add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> oval at (x, y) with width and height */
  public DrawOval fillOval(int x, int y, int width, int height) {
    DrawOval call = new DrawOval(x, y, width, height, true);
    buffer.add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> oval at (x, y) with width and height */
  public DrawOval drawOval(int x, int y, int width, int height) {
    DrawOval call = new DrawOval(x, y, width, height, false);
    buffer.add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> circle at (x, y) with radius */
  public DrawCircle fillCircle(int x, int y, int radius) {
    DrawCircle call = new DrawCircle(x, y, 2 * radius, true);
    buffer.add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> circle at (x, y) with radius */
  public DrawCircle drawCircle(int x, int y, int radius) {
    DrawCircle call = new DrawCircle(x, y, 2 * radius, false);
    buffer.add(call);
    return call;
  }

  /** Draws a scaled <b>center-origin</b> {@link BufferedImage} at (x, y) with width and height */
  public DrawImage drawImage(BufferedImage img, int x, int y, int width, int height) {
    DrawImage call = new DrawImage(img, x, y, width, height);
    buffer.add(call);
    return call;
  }

  /** Applies accumulated draw calls to the provided {@link Graphics2D} object */
  void draw(Graphics2D g) {
    for (DrawCall call : buffer)
      call.apply(g);

    buffer.clear();
    completed = true;
  }

  void reset() {
    committed = completed = false;
  }
}
