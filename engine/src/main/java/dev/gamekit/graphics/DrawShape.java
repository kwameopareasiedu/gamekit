package dev.gamekit.graphics;

import java.awt.*;

/** {@link DrawShape} is an abstract draw call to render a shape */
@SuppressWarnings("unchecked")
public abstract class DrawShape<T extends DrawShape<T>> extends DrawCall<DrawShape<T>> {
  protected Color color;
  protected Color prevColor;
  protected Stroke stroke;
  protected Stroke prevStroke;
  protected Paint paint;
  protected Paint prevPaint;
  protected Shape clip;
  protected Shape prevClip;

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
