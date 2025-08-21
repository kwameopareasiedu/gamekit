package dev.gamekit.components;

import dev.gamekit.core.Component;
import dev.gamekit.core.Entity;
import dev.gamekit.utils.Vector;

import java.util.List;

/**
 * {@link Transform} represents the position and rotation (in radian) of an {@link Entity}.
 * <p>
 * An entity will always and can only have one instance of a {@link Transform} component
 */
public class Transform extends Component {
  private final Vector localPosition;
  private final Vector globalPosition;
  private double localRotation;
  private double globalRotation;

  public Transform() {
    localPosition = new Vector();
    globalPosition = new Vector();
    localRotation = 0;
    globalRotation = 0;
  }

  @Override
  public void validate(Entity ignored, List<Component> components) {
    for (Component component : components) {
      if (component instanceof Transform) {
        throw new IllegalArgumentException(
          "Entity cannot have more than one Transform component"
        );
      }
    }
  }

  @Override
  protected void start() {
    Entity entityParent = entity.getParent();

    if (entityParent != null) {
      Transform parentTransform = entityParent.findComponent(Transform.class);
      Vector parentGlobalPosition = parentTransform.globalPosition;

      globalPosition.set(
        parentGlobalPosition.x + localPosition.x,
        parentGlobalPosition.y + localPosition.y
      );

      parentGlobalPosition.rotatePoint(globalPosition, parentTransform.globalRotation);

      globalRotation = parentTransform.globalRotation;
    } else {
      globalPosition.set(localPosition);
      globalRotation = localRotation;
    }
  }

  @Override
  protected void update() {
    Entity parentEntity = entity.getParent();

    // Start from the local position and rotation
    globalPosition.set(localPosition);
    globalRotation = localRotation;

    while (parentEntity != null) {
      // Recursively add the local position and rotations of the parent
      Transform parentTransform = parentEntity.findComponent(Transform.class);

      globalPosition.set(
        globalPosition.x += parentTransform.localPosition.x,
        globalPosition.y += parentTransform.localPosition.y
      );

      parentTransform.globalPosition.rotatePoint(
        globalPosition,
        parentTransform.globalRotation
      );

      globalRotation += parentTransform.localRotation;

      // Don't forget to move up the hierarchy
      parentEntity = parentEntity.getParent();
    }
  }

  /** Returns the local position */
  public Vector getLocalPosition() {
    return localPosition;
  }

  /** Sets the local position */
  public void setLocalPosition(double x, double y) {
    localPosition.set(x, y);
  }

  /** Returns the global position */
  public Vector getGlobalPosition() {
    return globalPosition;
  }

  /** Sets the global position */
  public void setGlobalPosition(double x, double y) {
    double offsetX = x - globalPosition.x;
    double offsetY = y - globalPosition.y;

    localPosition.set(
      localPosition.x + offsetX,
      localPosition.y + offsetY
    );
  }

  /** Returns the local rotation (radian) */
  public double getLocalRotation() {
    return localRotation;
  }

  /** Set the local rotation (radian) */
  public void setLocalRotation(double rad) {
    localRotation = rad;
  }

  /** Returns the global rotation (radian) */
  public double getGlobalRotation() {
    return globalRotation;
  }

  /** Set the global rotation (radian) */
  public void setGlobalRotation(double rad) {
    double offset = rad - localRotation;
    localRotation += offset;
  }
}
