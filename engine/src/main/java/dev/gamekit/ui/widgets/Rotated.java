package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Vector;

import java.awt.*;

/** A {@link SingleChildParent} which rotates its child about an angle <strong>in radian</strong> */
@WidgetBuilder
public class Rotated extends SingleChildParent {
  @WidgetBuilderField(fallback = "0.0", themable = false)
  public Double rotation;

  public Rotated(RotatedConfig config, Widget child) {
    super(config, child);
  }

  public static Rotated create(RotatedConfig.Updater updater, Widget child) {
    return new Rotated(Widgets.configureRotated(updater), child);
  }

  public static Rotated create(Double rotation, Widget child) {
    return Rotated.create(props -> props.rotation = rotation, child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    Vector[] childBoundsPoints = new Vector[]{
      new Vector(-0.5 * child.computedBounds.width, -0.5 * child.computedBounds.height),
      new Vector(0.5 * child.computedBounds.width, -0.5 * child.computedBounds.height),
      new Vector(0.5 * child.computedBounds.width, 0.5 * child.computedBounds.height),
      new Vector(-0.5 * child.computedBounds.width, 0.5 * child.computedBounds.height),
    };

    Vector origin = new Vector();

    for (Vector pt : childBoundsPoints) {
      origin.rotatePoint(pt, rotation);
    }

    Vector rotatedTopLeftPoint = new Vector(childBoundsPoints[0]);
    Vector rotatedBottomRightPoint = new Vector(childBoundsPoints[0]);

    for (Vector pt : childBoundsPoints) {
      rotatedTopLeftPoint.set(Math.min(rotatedTopLeftPoint.x, pt.x), Math.max(rotatedTopLeftPoint.y, pt.y));
      rotatedBottomRightPoint.set(Math.max(rotatedBottomRightPoint.x, pt.x), Math.min(rotatedBottomRightPoint.y, pt.y));
    }

    int aabbWidth = (int) Math.abs(rotatedBottomRightPoint.x - rotatedTopLeftPoint.x);
    int aabbHeight = (int) Math.abs(rotatedBottomRightPoint.y - rotatedTopLeftPoint.y);

    intrinsicSize.set(aabbWidth, aabbHeight);

    computedBounds.setSize(
      constraints.constrainWidth(intrinsicSize.width),
      constraints.constrainHeight(intrinsicSize.height)
    );

    child.computedBounds.setPosition(
      0.5 * (computedBounds.width - child.computedBounds.width),
      0.5 * (computedBounds.height - child.computedBounds.height)
    );
  }

  @Override
  protected void preRender(Graphics2D g) {
    if (rotation != 0) {
      double px = absoluteBounds.x + 0.5 * absoluteBounds.width;
      double py = absoluteBounds.y + 0.5 * absoluteBounds.height;

      g.translate(px, py);
      g.rotate(rotation);
      g.translate(-px, -py);
    }
  }

  @Override
  protected void postRender(Graphics2D g) {
    if (rotation != 0) {
      double px = absoluteBounds.x + 0.5 * absoluteBounds.width;
      double py = absoluteBounds.y + 0.5 * absoluteBounds.height;

      g.translate(px, py);
      g.rotate(-rotation);
      g.translate(-px, -py);
    }
  }
}
