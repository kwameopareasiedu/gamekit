package dev.gamekit.graphics;

import java.awt.*;

/** {@link DrawShape} is an abstract draw call to render a shape to the window */
public abstract class DrawShape extends DrawCall {
  protected Color color, prevColor;
  protected Stroke stroke, prevStroke;
  protected Paint paint, prevPaint;
  protected Shape clip, prevClip;

  public final DrawShape withStroke(Stroke stroke) {
    this.stroke = stroke;
    return this;
  }

  public final DrawShape withPaint(Paint paint) {
    this.paint = paint;
    return this;
  }

  public final DrawShape withColor(Color color) {
    this.color = color;
    return this;
  }

  public final DrawShape withClip(int x, int y, int width, int height) {
    this.clip = new Rectangle(x, y, width, height);
    return this;
  }

  @Override
  protected final void setup(Graphics2D g) {
    super.setup(g);

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
    super.cleanup(g);

    g.setStroke(prevStroke);
    g.setPaint(prevPaint);
    g.setColor(prevColor);
    g.setClip(prevClip);
  }
}
