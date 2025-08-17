package dev.gamekit.graphics;

import java.awt.*;

/**
 * {@link DrawPolygon} renders a <b>center-origin</b> polygon from a list of points to the window.
 * <p>
 * NB: The number of points supplied must even in the format {@code [px1, py1, px2, py2, ...,
 * pxn, pyn]}
 */
public class DrawPolygon extends DrawShape {
  private final Polygon polygon;
  private final boolean filled;

  public DrawPolygon(int[] points, boolean filled) {
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
