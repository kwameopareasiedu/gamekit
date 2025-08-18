package dev.gamekit.components;

import dev.gamekit.core.Component;
import dev.gamekit.core.Entity;
import dev.gamekit.utils.Vector;

/**
 * {@link Transform} represents the position and rotation of an {@link Entity}.
 * <p>
 * An entity will always and can only have one instance of a {@link Transform} component.
 */
public class Transform extends Component {
  private final Vector position;
  private final Vector localPosition;
  private double rotation;

  public Transform() {
    position = new Vector();
    localPosition = new Vector();
    rotation = 0;
  }

  public double getX() {
    return position.x;
  }

  public double getY() {
    return position.y;
  }

  /**
   * Sets the position and computes the local position based on the host {@link Entity entity's}
   * parent (if any)
   */
  public void setPosition(double x, double y) {
    position.set(x, y);
    Entity entityParent = entity.getParent();

    if (entityParent != null) {
      Transform parentTransform = entityParent.findComponent(Transform.class);

      if (parentTransform != null) {
        double parentTransformX = parentTransform.getX();
        double parentTransformY = parentTransform.getY();
        localPosition.set(x - parentTransformX, y - parentTransformY);
      } else {
        localPosition.set(x, y);
      }
    } else {
      localPosition.set(x, y);
    }
  }

  /** Returns the transform's rotation <b>in degrees</b> */
  public double getRotation() {
    return rotation;
  }

  /** Set the transform's rotation <b>in degrees</b> */
  public void setRotation(double deg) {
    this.rotation = deg;
  }
}
