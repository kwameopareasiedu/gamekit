package dev.gamekit.components;

import dev.gamekit.core.Component;
import dev.gamekit.core.Constants;
import dev.gamekit.core.Physics;
import dev.gamekit.core.Renderer;
import dev.gamekit.utils.Vector;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.util.UUID;

import static dev.gamekit.utils.Math.toInt;

/**
 * {@link Collider} defines the physics shape of an entity for the purposes of physics collision
 * detection
 */
public abstract class Collider extends Component {
  private static final Stroke SENSOR_STROKE = new BasicStroke(
    1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{ 10, 2 }, 0
  );

  public static boolean DEBUG_DRAW = false;

  protected final BodyAttachedFixture fixture;
  protected Physics.CollisionListener collisionListener;

  public Collider(BodyAttachedFixture fixture) {
    this.fixture = fixture;
  }

  /**
   * Sets the metadata object
   * @see org.dyn4j.dynamics.BodyFixture#setUserData(Object)
   */
  public void setMetaData(Object metadata) {
    fixture.setUserData(metadata);
  }

  /** Sets the local offset of this fixture relative to the entity */
  public void setOffset(double x, double y) {
    fixture.getShape().translate(x / Constants.PIXELS_PER_METER, y / Constants.PIXELS_PER_METER);
  }

  /**
   * Sets a {@link Physics.CollisionListener} to be notified when this collider collides with
   * another collider
   */
  public void setCollisionListener(Physics.CollisionListener collisionListener) {
    this.collisionListener = collisionListener;
  }

  /** @see org.dyn4j.dynamics.BodyFixture#setDensity(double) */
  public void setDensity(double density) {
    fixture.setDensity(density);
  }

  /** @see org.dyn4j.dynamics.BodyFixture#setRestitution(double) */
  public void setRestitution(double restitution) {
    fixture.setRestitution(restitution);
  }

  /** @see org.dyn4j.dynamics.BodyFixture#setFriction(double) */
  public void setFriction(double friction) {
    fixture.setFriction(friction);
  }

  /** @see org.dyn4j.dynamics.BodyFixture#setSensor(boolean) */
  public void setSensor(boolean sensor) {
    fixture.setSensor(sensor);
  }

  @Override
  protected void render() {
    if (DEBUG_DRAW) {
      Transform tx = entity.findComponent(Transform.class);
      Vector globalPosition = tx.getGlobalPosition();
      int positionX = toInt(globalPosition.x);
      int positionY = toInt(globalPosition.y);
      double rotation = tx.getGlobalRotation();

      Convex shape = fixture.getShape();
      Vector2 shapeCenter = shape.getCenter();
      int shapePositionX = toInt(globalPosition.x + shapeCenter.x * Constants.PIXELS_PER_METER);
      int shapePositionY = toInt(globalPosition.y + shapeCenter.y * Constants.PIXELS_PER_METER);
      Stroke stroke = fixture.isSensor() ? SENSOR_STROKE : null;

      if (shape instanceof Circle circle) {
        int radius = toInt(circle.getRadius() * Constants.PIXELS_PER_METER);

        Renderer.drawCircle(shapePositionX, shapePositionY, radius)
          .withColor(Color.CYAN).withStroke(stroke)
          .withRotation(positionX, positionY, rotation);
        Renderer.drawVerticalLine(shapePositionX, shapePositionY, shapePositionY + radius)
          .withRotation(positionX, positionY, rotation);
      } else if (shape instanceof Rectangle rect) {
        int width = toInt(rect.getWidth() * Constants.PIXELS_PER_METER);
        int height = toInt(rect.getHeight() * Constants.PIXELS_PER_METER);

        Renderer.drawRect(shapePositionX, shapePositionY, width, height)
          .withColor(Color.CYAN).withStroke(stroke)
          .withRotation(positionX, positionY, rotation);
        Renderer.drawVerticalLine(shapePositionX, shapePositionY, shapePositionY + height / 2)
          .withRotation(positionX, positionY, rotation);
      }

      Renderer.fillCircle(shapePositionX, shapePositionY, 2).withColor(Color.ORANGE)
        .withRotation(positionX, positionY, rotation);
    }
  }

  /**
   * {@link BodyAttachedFixture} extends {@link BodyFixture} adding a reference to the
   * {@link Body} to which it is attached
   */
  public static class BodyAttachedFixture extends BodyFixture {
    public final String id = UUID.randomUUID().toString();

    Body body;

    /** Creates a {@link BodyAttachedFixture} with the associated {@link Convex shape} */
    public BodyAttachedFixture(Convex shape) {
      super(shape);
    }

    /** Adds this fixture to the specified {@link Body} and stores a reference to this body */
    void addToBody(Body body) {
      this.body = body;
      body.addFixture(this);
    }

    /**
     * Returns the metadata object
     * @see BodyFixture#getUserData()
     */
    public Object getMetaData() {
      return super.getUserData();
    }
  }
}
