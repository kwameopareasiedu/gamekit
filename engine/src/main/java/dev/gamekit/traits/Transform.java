package dev.gamekit.traits;

import dev.gamekit.core.Entity;
import dev.gamekit.core.Trait;
import dev.gamekit.utils.Vector;

/** The {@link Transform} trait represents the position, rotation and scale of an entity */
public class Transform extends Trait {
  private final Vector position;
  private final Vector localPosition;

  public Transform() {
    position = new Vector();
    localPosition = new Vector();
  }

  public double getX() {
    return position.x;
  }

  public double getY() {
    return position.y;
  }

  public void setPosition(double x, double y) {
    position.set(x, y);
    Entity entityParent = entity.getParent();

    if (entityParent != null) {
      Transform entityParentTransform = entityParent.findTrait(Transform.class);

      if (entityParentTransform != null) {
        double entityParentTransformX = entityParentTransform.getX();
        double entityParentTransformY = entityParentTransform.getY();
        localPosition.set(x - entityParentTransformX, y - entityParentTransformY);
      }
    } else {
      localPosition.set(x, y);
    }
  }
}
