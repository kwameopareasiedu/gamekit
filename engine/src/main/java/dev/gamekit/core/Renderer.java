package dev.gamekit.core;

import dev.gamekit.settings.ImageInterpolation;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.VoidCallback;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

  /** {@link DrawCall} is an instruction to the draw thread to render something */
  @SuppressWarnings("unchecked")
  public abstract static class DrawCall<T extends DrawCall<T>> {
    private int rotationPointX;
    private int rotationPointY;
    private double rotationAngle;
    private AlphaComposite opacityComposite;

    private DrawCall() { }

    /** Called internally by the engine to modify the provided {@link Graphics2D} object with the call's draw logic */
    public final void apply(Graphics2D g) {
      if (rotationAngle != 0) {
        g.translate(rotationPointX, -rotationPointY);
        g.rotate(rotationAngle);
        g.translate(-rotationPointX, rotationPointY);
      }

      Composite originalComposite = g.getComposite();

      if (opacityComposite != null)
        g.setComposite(opacityComposite);

      setup(g);
      draw(g);
      cleanup(g);

      if (opacityComposite != null)
        g.setComposite(originalComposite);

      if (rotationAngle != 0) {
        g.translate(rotationPointX, -rotationPointY);
        g.rotate(-rotationAngle);
        g.translate(-rotationPointX, rotationPointY);
      }
    }

    /**
     * A modifier which applies a rotation of {@code rad} radian about the point {@code (x, y)}.
     * <p>
     * This method returns the object on which it was called for further chaining
     */
    public T withRotation(int x, int y, double rad) {
      rotationPointX = x;
      rotationPointY = y;
      rotationAngle = rad;
      return (T) this;
    }

    /**
     * A modifier which applies transparency to the draw call.
     * <p>
     * This method returns the object on which it was called for further chaining
     */
    public T withOpacity(double opacity) {
      opacityComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity);
      return (T) this;
    }

    /** Overridable method called before {@link #draw} to perform pre-draw setup */
    protected void setup(Graphics2D g) { /* No-op */ }

    /** Abstract method which should perform the draw call's rendering logic */
    protected abstract void draw(Graphics2D g);

    /** Overridable method called after {@link #draw} to perform post-draw cleanup */
    protected void cleanup(Graphics2D g) { /* No-op */ }
  }

  /** {@link ClearCall} clears the visible bounds of the window */
  public static class ClearCall extends DrawCall<ClearCall> {
    private final Color color;

    private ClearCall(Color color) {
      this.color = color;
    }

    @Override
    protected void draw(Graphics2D g) {
      Bounds rb = Camera.getCurrent().getRenderBounds();
      Color originalColor = g.getBackground();

      g.setBackground(color);
      g.clearRect((int) rb.x, (int) rb.y, (int) rb.width, (int) rb.height);
      g.setBackground(originalColor);
    }
  }

  /** {@link DrawImage} renders a <b>center-origin</b> image to the window */
  public static class DrawImage extends DrawCall<DrawImage> {
    private static final Color TRANSPARENT = new Color(0x00000000, true);
    private static final AlphaComposite ALPHA_MASK_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1);
    private static final Map<String, BufferedImage> IMAGE_CACHE = new HashMap<>();

    private final BufferedImage image;
    private final int x, y;
    private final int width;
    private final int height;

    private ImageInterpolation interpolation;
    private ImageInterpolation prevInterpolation;
    private BufferedImage mask;
    private BufferedImage target;

    private DrawImage(BufferedImage image, int x, int y, int width, int height) {
      this.image = image;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }

    /**
     * Returns a cached {@link BufferedImage} to be used for temporary rendering.
     * <p>
     * This is way more performant than rapidly creating new {@link BufferedImage} objects every render frame
     */
    private static BufferedImage getCachedImage(int width, int height) {
      String key = width + "-" + height;
      BufferedImage image = IMAGE_CACHE.get(key);

      if (image != null)
        return image;

      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      IMAGE_CACHE.put(key, image);
      return image;
    }

    /**
     * A modifier which sets an {@link ImageInterpolation} strategy to the {@link Graphics2D} object.
     * <p>
     * This method returns the object on which it was called for further chaining
     */
    public final DrawImage withInterpolation(ImageInterpolation interpolation) {
      this.interpolation = interpolation;
      return this;
    }

    /**
     * A modifier which sets an alpha layer mask to use on this image.
     * <p>
     * The alpha channel of the provided mask image is used to control the visibility of the drawn pixels of the
     * underlying image.
     * <p>
     * If the alpha = 1.0, the pixels in the corresponding area of the underlying image are cleared and if
     * the alpha is 0.0, the pixels in the overlapping area are unchanged.
     * <p>
     * This method returns the object on which it was called for further chaining
     */
    public final DrawImage withMask(BufferedImage maskImage) {
      this.mask = maskImage;
      return this;
    }

    /**
     * By default, draw image calls are applied to the {@link Window}.
     * <p>
     * With this modifier, an additional {@link BufferedImage} target can be set to be drawn to.
     * <p>
     * The drawn image is scaled to match the dimensions of the give target.
     * <p>
     * This method returns the object on which it was called for further chaining
     */
    public final DrawImage withTarget(BufferedImage target) {
      this.target = target;
      return this;
    }

    @Override
    protected void setup(Graphics2D g) {
      if (interpolation != null) {
        prevInterpolation = ImageInterpolation.from(g);
        interpolation.apply(g);
      }
    }

    @Override
    protected void draw(Graphics2D g) {
      BufferedImage image = this.image;

      if (mask != null) {
        image = getCachedImage(width, height);

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        Graphics2D bg = image.createGraphics();

        bg.setBackground(TRANSPARENT);
        bg.clearRect(0, 0, imageWidth, imageHeight);

        bg.drawImage(
          this.image, 0, 0, imageWidth, imageHeight,
          0, 0, this.image.getWidth(), this.image.getHeight(), null
        );

        int maskWidth = mask.getWidth();
        int maskHeight = mask.getHeight();

        int x0 = (imageWidth - maskWidth) / 2;
        int y0 = (imageHeight - maskHeight) / 2;

        bg.setComposite(ALPHA_MASK_COMPOSITE);
        bg.drawImage(mask, x0, y0, maskWidth, maskHeight, null);
        bg.dispose();
      }

      int x0 = x - width / 2, y0 = y + height / 2;
      int x1 = x0 + width, y1 = y0 - height;
      g.drawImage(image, x0, -y0, x1, -y1, 0, 0, image.getWidth(), image.getHeight(), null);

      if (target != null) {
        Graphics2D gt = target.createGraphics();
        int targetWidth = target.getWidth();
        int targetHeight = target.getHeight();

        if (interpolation != null)
          interpolation.apply(gt);

        gt.setBackground(TRANSPARENT);
        gt.clearRect(0, 0, targetWidth, targetHeight);
        gt.drawImage(image, 0, 0, targetWidth, targetHeight, 0, 0, image.getWidth(), image.getHeight(), null);
        gt.dispose();
      }
    }

    @Override
    protected void cleanup(Graphics2D g) {
      if (prevInterpolation != null) {
        prevInterpolation.apply(g);
      }
    }
  }

  /** {@link DrawShape} is an abstract draw call to render a shape */
  @SuppressWarnings("unchecked")
  public abstract static class DrawShape<T extends DrawShape<T>> extends DrawCall<DrawShape<T>> {
    protected Color color;
    protected Color prevColor;
    protected Stroke stroke;
    protected Stroke prevStroke;
    protected Paint paint;
    protected Paint prevPaint;
    protected Shape clip;
    protected Shape prevClip;

    private DrawShape() { }

    /**
     * A modifier which applies an outline {@link Stroke} to the {@link Graphics2D} object.
     * <p>
     * This method returns the object on which it was called for further chaining
     */
    public final T withStroke(Stroke stroke) {
      this.stroke = stroke;
      return (T) this;
    }

    /**
     * A modifier which applies a pattern {@link Paint} to the {@link Graphics2D} object.
     * <p>
     * This method returns the object on which it was called for further chaining
     */
    public final T withPaint(Paint paint) {
      this.paint = paint;
      return (T) this;
    }

    /**
     * A modifier which applies a foreground color to the {@link Graphics2D} object.
     * <p>
     * This method returns the object on which it was called for further chaining
     */
    public final T withColor(Color color) {
      this.color = color;
      return (T) this;
    }

    /**
     * A modifier which applies a {@link Shape clip region} to the {@link Graphics2D} object.
     * <p>
     * This method returns the object on which it was called for further chaining
     */
    public final T withClip(int x, int y, int width, int height) {
      this.clip = new Rectangle(x, y, width, height);
      return (T) this;
    }

    @Override
    protected final void setup(Graphics2D g) {
      prevStroke = g.getStroke();
      prevPaint = g.getPaint();
      prevColor = g.getColor();
      prevClip = g.getClip();

      if (stroke == null) stroke = prevStroke;

      g.setStroke(stroke);
      g.setPaint(paint);
      g.setColor(color);
      g.setClip(clip);
    }

    @Override
    protected final void cleanup(Graphics2D g) {
      g.setStroke(prevStroke);
      g.setPaint(prevPaint);
      g.setColor(prevColor);
      g.setClip(prevClip);
    }
  }

  /** {@link DrawLine} is a draw call which renders a line */
  public static class DrawLine extends DrawShape<DrawLine> {
    private final int x1, y1, x2, y2;

    private DrawLine(int x1, int y1, int x2, int y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }

    @Override
    protected void draw(Graphics2D g) {
      g.drawLine(x1, -y1, x2, -y2);
    }
  }

  /** {@link DrawOval} is a draw call which renders a <b>center-origin</b> oval */
  public static class DrawOval extends DrawShape<DrawOval> {
    private final int x, y;
    private final int width;
    private final int height;
    private final boolean filled;

    private DrawOval(int x, int y, int width, int height, boolean filled) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.filled = filled;
    }

    @Override
    protected void draw(Graphics2D g) {
      int x0 = x - width / 2, y0 = y + height / 2;
      if (filled) g.fillOval(x0, -y0, width, height);
      else g.drawOval(x0, -y0, width, height);
    }
  }

  /** {@link DrawCircle} is a draw call which renders a <b>center-origin</b> circle */
  public static class DrawCircle extends DrawOval {
    public DrawCircle(int x, int y, int diameter, boolean filled) {
      super(x, y, diameter, diameter, filled);
    }
  }

  /** {@link DrawRoundRect} is a draw call which renders a <b>center-origin</b> rounded rect */
  public static class DrawRoundRect extends DrawShape<DrawRoundRect> {
    private final int x, y;
    private final int width;
    private final int height;
    private final int arcWidth;
    private final int arcHeight;
    private final boolean filled;

    private DrawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean filled) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.arcWidth = arcWidth;
      this.arcHeight = arcHeight;
      this.filled = filled;
    }

    @Override
    protected void draw(Graphics2D g) {
      int x0 = x - width / 2, y0 = y + height / 2;
      if (filled) g.fillRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
      else g.drawRoundRect(x0, -y0, width, height, arcWidth, arcHeight);
    }
  }

  /** {@link DrawRect} is a draw call which renders a <b>center-origin</b> rect */
  public static class DrawRect extends DrawRoundRect {
    private DrawRect(int x, int y, int width, int height, boolean filled) {
      super(x, y, width, height, 0, 0, filled);
    }
  }

  /**
   * {@link DrawPolygon} renders a polygon from a list of point pairs in the format
   * {@code [px1, py1, px2, py2, ..., pxn, pyn]}
   */
  public static class DrawPolygon extends DrawShape<DrawPolygon> {
    private final Polygon polygon;
    private final boolean filled;

    private DrawPolygon(int[] points, boolean filled) {
      int[] xPoints = new int[points.length / 2];
      int[] yPoints = new int[points.length / 2];

      for (int i = 0; i < points.length; i++) {
        if (i % 2 == 0) {
          xPoints[i / 2] = points[i];
        } else {
          yPoints[(i - 1) / 2] = -points[i];
        }
      }

      this.polygon = new Polygon(xPoints, yPoints, xPoints.length);
      this.filled = filled;
    }

    @Override
    protected void draw(Graphics2D g) {
      if (filled) g.fillPolygon(polygon);
      else g.drawPolygon(polygon);
    }
  }
}
