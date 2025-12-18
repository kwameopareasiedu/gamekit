package dev.gamekit.components;

import dev.gamekit.core.Physics;
import org.dyn4j.geometry.Circle;

/** {@link CircleCollider} defines a circular {@link Collider} for an entity */
public class CircleCollider extends Collider {
  public CircleCollider(double radius) {
    super(new ColliderFixture(new Circle(radius / Physics.PIXELS_PER_METER)));
  }
}
