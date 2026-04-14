package dev.gamekit.core;

import dev.gamekit.graphics.*;
import dev.gamekit.utils.VoidCallback;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * {@link Renderer} dispatches {@link DrawCall draw calls} which are processed by the render thread to draw on the
 * current {@link Window}
 */
@SuppressWarnings({ "JavaExistingMethodCanBeUsed", "unchecked" })
public final class Renderer {
  public static final int LAYER_COUNT = 64;

  private static final ArrayList<DrawCall<?>>[] BUFFERS = new ArrayList[LAYER_COUNT];
  private static boolean committed = false;
  private static boolean completed = false;
  private static int layerIndex = 0;

  static {
    for (int i = 0; i < LAYER_COUNT; i++)
      BUFFERS[i] = new ArrayList<>();
  }

  private Renderer() { }

  /**
   * Executes the provided render action on the specified layer (0 - 63).
   * <p>
   * Layers with higher index are rendered in front of layers with lower index
   */
  public static void onLayer(int layerIndex, VoidCallback renderAction) {
    if (layerIndex < 0 || layerIndex >= LAYER_COUNT)
      throw new IndexOutOfBoundsException(
        String.format("Renderer layerIndex must be 0 - %d", LAYER_COUNT - 1)
      );

    Renderer.layerIndex = layerIndex;
    renderAction.invoke();
    Renderer.layerIndex = 0;
  }

  /** Clears the view area contents with a specified color */
  public static void clear(Color color) {
    BUFFERS[0].add(new ClearCall(color));
  }

  /** Draws a line from {@code (x1, y1)} to {@code (x2, y2)} */
  public static DrawLine drawLine(int x1, int y1, int x2, int y2) {
    DrawLine call = new DrawLine(x1, y1, x2, y2);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Draws a vertical line from {@code (x, y1)} to {@code (x, y2)} */
  public static DrawLine drawVerticalLine(int x, int y1, int y2) {
    DrawLine call = new DrawLine(x, y1, x, y2);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Draws a horizontal line from {@code (x1, y)} to {@code (x2, y)} */
  public static DrawLine drawHorizontalLine(int x1, int x2, int y) {
    DrawLine call = new DrawLine(x1, y, x2, y);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> rect at (x, y) with size and height */
  public static DrawRect fillRect(int x, int y, int width, int height) {
    DrawRect call = new DrawRect(x, y, width, height, true);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> rect at (x, y) with size and height */
  public static DrawRect drawRect(int x, int y, int width, int height) {
    DrawRect call = new DrawRect(x, y, width, height, false);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> rounded rect at (x, y) with size, height, arc size and height */
  public static DrawRoundRect fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    DrawRoundRect call = new DrawRoundRect(x, y, width, height, arcWidth, arcHeight, true);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> rounded rect at (x, y) with size, height, arc size and height */
  public static DrawRoundRect drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    DrawRoundRect call = new DrawRoundRect(x, y, width, height, arcWidth, arcHeight, false);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> oval at (x, y) with size and height */
  public static DrawOval fillOval(int x, int y, int width, int height) {
    DrawOval call = new DrawOval(x, y, width, height, true);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> oval at (x, y) with size and height */
  public static DrawOval drawOval(int x, int y, int width, int height) {
    DrawOval call = new DrawOval(x, y, width, height, false);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Fills a <b>center-origin</b> circle at (x, y) with radius */
  public static DrawCircle fillCircle(int x, int y, int radius) {
    DrawCircle call = new DrawCircle(x, y, 2 * radius, true);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Draws a <b>center-origin</b> circle at (x, y) with radius */
  public static DrawCircle drawCircle(int x, int y, int radius) {
    DrawCircle call = new DrawCircle(x, y, 2 * radius, false);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Draws a scaled <b>center-origin</b> {@link BufferedImage} at {@code (x, y)} with width and height */
  public static DrawImage drawImage(BufferedImage img, int x, int y, int width, int height) {
    DrawImage call = new DrawImage(img, x, y, width, height);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Draws a polygon from a list of points which must even in the format {@code [px1, py1, px2, py2, ..., pxn, pyn] */
  public static DrawPolygon drawPolygon(int[] points) {
    DrawPolygon call = new DrawPolygon(points, false);
    BUFFERS[layerIndex].add(call);
    return call;
  }

  /** Fills a polygon from a list of points which must even in the format {@code [px1, py1, px2, py2, ..., pxn, pyn] */
  public static DrawPolygon fillPolygon(int[] points) {
    DrawPolygon call = new DrawPolygon(points, true);
    BUFFERS[layerIndex].add(call);
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
    for (ArrayList<DrawCall<?>> buffer : BUFFERS)
      for (DrawCall<?> call : buffer)
        call.apply(g);

    completed = true;
  }

  static void reset() {
    for (ArrayList<DrawCall<?>> buffer : BUFFERS)
      buffer.clear();

    committed = completed = false;
  }
}
