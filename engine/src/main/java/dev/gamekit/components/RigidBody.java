package dev.gamekit.components;

import dev.gamekit.core.*;
import dev.gamekit.core.Component;
import dev.gamekit.utils.Vector;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Rectangle;

import java.awt.*;

import static dev.gamekit.utils.Math.toInt;

/**
 * The {@link RigidBody} component enables physics-based motion for the entity.
 * <p>
 * During its {@link #update()} phase and after a physics simulation step is complete, this
 * component will update the attached entity's {@link Transform} component's position and rotation
 */
public class RigidBody extends Component {
  public static boolean DEBUG_DRAW = false;

  private final Body body;

  public RigidBody() {
    body = new Body();
    body.setMassType(MassType.INFINITE);
  }

  public RigidBody(MassType massType, Vector massCenter, double mass, double inertia) {
    body = new Body();
    body.setMassType(massType);
    body.setMass(new Mass(new Vector2(massCenter.x, massCenter.y), mass, inertia));
  }

  public void setCustomData(Object data) {
    body.setUserData(data);
  }

  public void setGravityScale(double scale) {
    body.setGravityScale(scale);
  }

  public void setWorldPosition(double x, double y) {
    Vector2 center = body.getWorldCenter();
    body.translate(center.multiply(-1).add(x, y));
  }

  public void addCircleFixture(double radius, BodyFixtureTuner tuner) {
    Circle circle = new Circle(radius);
    BodyFixture fx = new BodyFixture(circle);
    tuner.tuneFixture(fx);
    body.addFixture(fx);
  }

  public void addCircleFixture(double radius) {
    addCircleFixture(radius, fx -> { });
  }

  public void addRectFixture(double width, double height, BodyFixtureTuner tuner) {
    Rectangle rect = new Rectangle(width, height);
    BodyFixture fx = new BodyFixture(rect);
    tuner.tuneFixture(fx);
    body.addFixture(fx);
  }

  public void addRectFixture(double width, double height) {
    addRectFixture(width, height, fx -> { });
  }

  public void applyImpulse(Vector vector) {
    body.applyImpulse(new Vector2(vector.x, vector.y));
  }

  public void addCollisionListener(Physics.CollisionListener listener) {
    Physics.addCollisionListener(body, listener);
  }

  @Override
  protected void start() {
    super.start();
    Physics.addBody(body);
  }

  @Override
  protected void update() {
    super.update();

    Transform transformComponent = entity.findComponent(Transform.class);

    if (transformComponent != null) {
      Vector2 center = body.getWorldCenter();
      transformComponent.setPosition(center.x, center.y);
    }
  }

  @Override
  protected void render() {
    super.render();

    if (DEBUG_DRAW) {
      Vector2 bodyCenter = body.getWorldCenter();

      for (BodyFixture fx : body.getFixtures()) {
        Convex shape = fx.getShape();
        Vector2 shapeCenter = shape.getCenter();
        int shapeCenterX = toInt((bodyCenter.x + shapeCenter.x) * Constants.PIXELS_PER_METER);
        int shapeCenterY = toInt((bodyCenter.y + shapeCenter.y) * Constants.PIXELS_PER_METER);

        if (shape instanceof Circle circle) {
          int radius = toInt(circle.getRadius() * Constants.PIXELS_PER_METER);
          Renderer.drawCircle(shapeCenterX, shapeCenterY, radius).withColor(Color.CYAN);
        } else if (shape instanceof Rectangle rect) {
          int width = toInt(rect.getWidth() * Constants.PIXELS_PER_METER);
          int height = toInt(rect.getHeight() * Constants.PIXELS_PER_METER);
          Renderer.drawRect(shapeCenterX, shapeCenterY, width, height).withColor(Color.CYAN);
        }
      }
    }
  }

  @Override
  protected void dispose() {
    super.dispose();

    Application.getInstance().scheduleTask(() -> {
      Physics.removeBody(body);
    });
  }

  /** Interface for an object which tunes a {@link BodyFixture} */
  public interface BodyFixtureTuner {
    /** Called with the new {@link BodyFixture} for tuning before adding to a {@link Body} */
    void tuneFixture(BodyFixture fixture);
  }
}
