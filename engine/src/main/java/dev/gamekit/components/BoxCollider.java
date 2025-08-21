package dev.gamekit.components;

import dev.gamekit.core.Constants;
import org.dyn4j.geometry.Rectangle;

/** {@link BoxCollider} defines a rectangular {@link Collider} for an entity */
public class BoxCollider extends Collider {
  public BoxCollider(double width, double height) {
    super(
      new ColliderFixture(
        new Rectangle(
          width / Constants.PIXELS_PER_METER,
          height / Constants.PIXELS_PER_METER
        )
      )
    );
  }
}
