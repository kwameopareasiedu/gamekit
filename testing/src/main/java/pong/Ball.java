package pong;

import dev.gamekit.components.RigidBody;
import dev.gamekit.core.Component;
import dev.gamekit.core.Entity;
import dev.gamekit.utils.Vector;
import org.dyn4j.geometry.MassType;

import java.util.List;

public class Ball extends Entity {
  private final double radius = 0.1;

  public Ball() {
    super("Ball");
  }

  @Override
  protected List<Component> getComponents() {
    RigidBody rb = new RigidBody(
      MassType.NORMAL, new Vector(), 1, 1
    );

    rb.setUserData("Ball");
    rb.setGravityScale(0);
    rb.addCircleFixture(radius, (fx, circle) -> {
      fx.setDensity(15);
      fx.setRestitution(1);
      fx.setFriction(0);
    });
    rb.applyImpulse(-2, -0.5);
    rb.applyTorque(3);
    rb.addCollisionListener((selfBody, selfFixture, otherBody, otherFixture) -> {
      logger.debug("Ball collided with {}", otherBody.getUserData());
    });

    return List.of(rb);
  }

  //  @Override
  //  protected void render() {
  //    super.render();
  //    Transform transformTrait = findComponent(Transform.class);
  //
  //    Renderer.fillCircle(
  //      (int) (transformTrait.getX() * Constants.PIXELS_PER_METER),
  //      (int) (transformTrait.getY() * Constants.PIXELS_PER_METER),
  //      (int) (radius * Constants.PIXELS_PER_METER)
  //    ).withColor(Color.RED);
  //  }
}
