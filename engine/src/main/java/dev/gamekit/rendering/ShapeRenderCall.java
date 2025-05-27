package dev.gamekit.rendering;

import java.awt.*;

/** {@link ShapeRenderCall} is an abstract render call to render a shape to the window */
public abstract class ShapeRenderCall extends RenderCall {
  protected Color color, prevColor;
  protected Stroke stroke, prevStroke;
  protected Paint paint, prevPaint;
  protected Shape clip, prevClip;

  public final ShapeRenderCall withStroke(Stroke stroke) {
    this.stroke = stroke;
    return this;
  }

  public final ShapeRenderCall withPaint(Paint paint) {
    this.paint = paint;
    return this;
  }

  public final ShapeRenderCall withColor(Color color) {
    this.color = color;
    return this;
  }

  public final ShapeRenderCall withClip(int x, int y, int width, int height) {
    this.clip = new Rectangle(x, y, width, height);
    return this;
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
