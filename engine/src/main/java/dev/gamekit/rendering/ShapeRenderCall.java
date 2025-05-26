package dev.gamekit.rendering;

import java.awt.*;

/** {@link ShapeRenderCall} is an abstract render call to render a shape to the window */
public abstract class ShapeRenderCall extends RenderCall {
  protected Color color, prevColor;
  protected Color bgColor, prevBgColor;
  protected Stroke stroke, prevStroke;
  protected Paint paint, prevPaint;
  protected Shape clip, prevClip;

  public ShapeRenderCall withBgColor(Color bgColor) {
    this.bgColor = bgColor;
    return this;
  }

  public ShapeRenderCall withStroke(Stroke stroke) {
    this.stroke = stroke;
    return this;
  }

  public ShapeRenderCall withPaint(Paint paint) {
    this.paint = paint;
    return this;
  }

  public ShapeRenderCall withColor(Color color) {
    this.color = color;
    return this;
  }

  public ShapeRenderCall withShape(int x, int y, int width, int height) {
    this.clip = new Rectangle(x, y, width, height);
    return this;
  }

  @Override
  protected void preRender(Graphics2D g) {
    prevBgColor = g.getBackground();
    prevStroke = g.getStroke();
    prevPaint = g.getPaint();
    prevColor = g.getColor();
    prevClip = g.getClip();

    if (stroke == null) stroke = prevStroke;

    g.setBackground(bgColor);
    g.setStroke(stroke);
    g.setPaint(paint);
    g.setColor(color);
    g.setClip(clip);
  }

  @Override
  protected void postRender(Graphics2D g) {
    g.setBackground(prevBgColor);
    g.setStroke(prevStroke);
    g.setPaint(prevPaint);
    g.setColor(prevColor);
    g.setClip(prevClip);
  }
}
