package dev.gamekit.core;

import dev.gamekit.graphics.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * {@link Renderer} dispatches {@link DrawCall draw calls} which are processed by the render thread to draw on the
 * current {@link Window}
 */
@SuppressWarnings("JavaExistingMethodCanBeUsed")
public final class Renderer {
  private static final ArrayList<DrawCall<?>> BUFFER = new ArrayList<>();
  private static boolean committed = false;
  private static boolean completed = false;

  private Renderer() { }

  /** Clears the view area contents with a specified color */
  public static void clear(Color color) {
    BUFFER.add(new ClearCall(color));
  }

  /** Draws a line from {@code (x1, y1)} to {@code (x2, y2)} */
  public static DrawLine drawLine(int x1, int y1, int x2, int y2) {
    DrawLine call = new DrawLine(x1, y1, x2, y2);
    BUFFER.add(call);
    return call;
  }

  /** Draws a vertical line from {@code (x, y1)} to {@code (x, y2)} */
  public static DrawLine drawVerticalLine(int x, int y1, int y2) {
    DrawLine call = new DrawLine(x, y1, x, y2);
    BUFFER.add(call);
    return call;
  }

  /** Draws a horizontal line from {@code (x1, y)} to {@code (x2, y)} */
  public static DrawLine drawHorizontalLine(int x1, int x2, int y) {
    DrawLine call = new DrawLine(x1, y, x2, y);
    BUFFER.add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> rect at (x, y) with size and height */
  public static DrawRect fillRect(int x, int y, int width, int height) {
    DrawRect call = new DrawRect(x, y, width, height, true);
    BUFFER.add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> rect at (x, y) with size and height */
  public static DrawRect drawRect(int x, int y, int width, int height) {
    DrawRect call = new DrawRect(x, y, width, height, false);
    BUFFER.add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> rounded rect at (x, y) with size, height, arc size and height */
  public static DrawRoundRect fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    DrawRoundRect call = new DrawRoundRect(x, y, width, height, arcWidth, arcHeight, true);
    BUFFER.add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> rounded rect at (x, y) with size, height, arc size and height */
  public static DrawRoundRect drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    DrawRoundRect call = new DrawRoundRect(x, y, width, height, arcWidth, arcHeight, false);
    BUFFER.add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> oval at (x, y) with size and height */
  public static DrawOval fillOval(int x, int y, int width, int height) {
    DrawOval call = new DrawOval(x, y, width, height, true);
    BUFFER.add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> oval at (x, y) with size and height */
  public static DrawOval drawOval(int x, int y, int width, int height) {
    DrawOval call = new DrawOval(x, y, width, height, false);
    BUFFER.add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> circle at (x, y) with radius */
  public static DrawCircle fillCircle(int x, int y, int radius) {
    DrawCircle call = new DrawCircle(x, y, 2 * radius, true);
    BUFFER.add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> circle at (x, y) with radius */
  public static DrawCircle drawCircle(int x, int y, int radius) {
    DrawCircle call = new DrawCircle(x, y, 2 * radius, false);
    BUFFER.add(call);
    return call;
  }

  /**
   * Draws a scaled <b>center-origin</b> {@link BufferedImage} at {@code (x, y)} with width and
   * height
   */
  public static DrawImage drawImage(BufferedImage img, int x, int y, int width, int height) {
    DrawImage call = new DrawImage(img, x, y, width, height);
    BUFFER.add(call);
    return call;
  }

  /** Draws a polygon from a list of points which must even in the format {@code [px1, py1, px2, py2, ..., pxn, pyn] */
  public static DrawPolygon drawPolygon(int[] points) {
    DrawPolygon call = new DrawPolygon(points, false);
    BUFFER.add(call);
    return call;
  }

  /** Fills a polygon from a list of points which must even in the format {@code [px1, py1, px2, py2, ..., pxn, pyn] */
  public static DrawPolygon fillPolygon(int[] points) {
    DrawPolygon call = new DrawPolygon(points, true);
    BUFFER.add(call);
    return call;
  }

  static boolean isCommitted() {
    return committed;
  }

  static boolean isCompleted() {
    return committed && completed;
  }

  static void commit() {
    committed = true;
  }

  /** Applies accumulated draw calls to the provided {@link Graphics2D} object */
  static void draw(Graphics2D g) {
    for (DrawCall<?> call : BUFFER) {
      call.apply(g);
    }

    completed = true;
  }

  static void reset() {
    BUFFER.clear();
    committed = completed = false;
  }
}
