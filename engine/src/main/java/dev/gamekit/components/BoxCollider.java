package dev.gamekit.components;

import dev.gamekit.core.Physics;
import org.dyn4j.geometry.Rectangle;

/** {@link BoxCollider} defines a rectangular {@link Collider} for an entity */
public class BoxCollider extends Collider {
  public BoxCollider(double width, double height) {
    super(new RefFixture(new Rectangle(width / Physics.PIXELS_PER_METER, height / Physics.PIXELS_PER_METER)));
  }
}
