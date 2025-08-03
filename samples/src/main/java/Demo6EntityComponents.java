import dev.gamekit.components.RigidBody;
import dev.gamekit.components.Transform;
import dev.gamekit.core.*;
import dev.gamekit.core.Component;
import dev.gamekit.settings.Antialiasing;
import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.settings.WindowMode;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Vector;
import org.dyn4j.geometry.MassType;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * This demo shows how to use components to enhance entities and performs the following actions:
 * <ul>
 *   <li>Creates an {@link Application application}</li>
 *   <li>Create an ball {@link Entity}</li>
 *   <li>Attach a {@link RigidBody} component to the ball</li>
 *   <li>Detect mouse input to add an impulse to the ball</li>
 *   <li>Overrides the {@link Entity#render()}} method to render the scene</li>
 * </ul>
 */
public class Demo6EntityComponents extends Scene {
  private final Ball ball = new Ball();
  private final Random rnd = new Random();

  public Demo6EntityComponents() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Settings(
        "Demo 6 - Entity Components",
        Resolution.HD,
        WindowMode.WINDOWED,
        Antialiasing.ON
      )
    ) { };

    game.loadScene(new Demo6EntityComponents());
    game.run();
  }

  @Override
  protected void start() {
    RigidBody.DEBUG_DRAW = true;
    addChild(new Wall(Wall.Type.TOP));
    addChild(new Wall(Wall.Type.RIGHT));
    addChild(new Wall(Wall.Type.BOTTOM));
    addChild(new Wall(Wall.Type.LEFT));

    addChild(ball);
  }

  @Override
  protected void update() {
    if (Input.isKeyDown(Input.KEY_SPACE)) {
      RigidBody ballRb = ball.findComponent(RigidBody.class);
      ballRb.applyImpulse(rnd.nextDouble(6) - 3, 5 + rnd.nextDouble(1.5));
    }
  }

  @Override
  protected void render() {
    Renderer.clear(Color.BLACK);
  }

  @Override
  protected Widget createUI() {
    return Stack.create(
      Align.create(
        Align.config().horizontalAlignment(Alignment.CENTER).verticalAlignment(Alignment.END),
        Padding.create(
          Padding.config().padding(24, 24, 24, 24),
          Text.create(
            Text.config().alignment(Alignment.CENTER),
            "Press the space bar to launch the ball"
          )
        )
      )
    );
  }

  public static class Wall extends Entity {
    private static final double[][] TYPE_TRANSFORM = new double[][]{
      new double[]{ 0, 2, 6, 0.125 },
      new double[]{ 2.95, 0, 0.125, 4.11 },
      new double[]{ 0, -2, 6, 0.125 },
      new double[]{ -2.95, 0, 0.125, 4.11 },
    };

    private final Type type;

    public Wall(Type type) {
      super(String.format("Wall %s", type.toString()));
      this.type = type;
    }

    @Override
    protected List<Component> getComponents() {
      // Create a RigidBody component
      RigidBody rb = new RigidBody();
      double[] tx = TYPE_TRANSFORM[type.toIndex()];
      String identifier = String.format("Wall %s", type);

      // Attach an id to the rigid body (For identification in collision)
      rb.setUserData(identifier);
      // Set the position of this rigid body
      rb.setPosition(tx[0], tx[1]);
      // Add a rectangle fixture/shape to the rigid body
      rb.addRectFixture(tx[2], tx[3]);
      // Return a list of components for the Wall entity
      return List.of(rb);
    }

    public enum Type {
      TOP, RIGHT, BOTTOM, LEFT;

      int toIndex() {
        return switch (this) {
          case TOP -> 0;
          case RIGHT -> 1;
          case BOTTOM -> 2;
          case LEFT -> 3;
        };
      }
    }
  }

  public static class Ball extends Entity {
    private final double radius = 0.1;

    public Ball() {
      super("Ball");
    }

    @Override
    protected List<Component> getComponents() {
      // Create a RigidBody component
      RigidBody rb = new RigidBody(
        MassType.NORMAL, new Vector(), 1, 1
      );

      // Attach an id to the rigid body (For identification in collision)
      rb.setUserData("Ball");
      // Add a circle fixture/shape to the rigid body
      rb.addCircleFixture(radius, (fx, circle) -> {
        // Set the circle shape's density
        fx.setDensity(15);
        // Set coefficient of restitution to 0.5
        fx.setRestitution(0.5);
      });

      // Apply an instantaneous impulse to the rigid body
      rb.applyImpulse(-2, -0.5);
      // Apply a rotational torque to the rigid body
      rb.applyTorque(3);

      // Add a collision listener to be notified when this rigid body collides with another
      rb.addCollisionListener((selfBody, selfFixture, otherBody, otherFixture) -> {
        logger.debug("Ball collided with {}", otherBody.getUserData());
      });

      return List.of(rb);
    }

    @Override
    protected void render() {
      super.render();
      Transform tx = findComponent(Transform.class);

      Renderer.fillCircle(
        (int) (tx.getX() * Constants.PIXELS_PER_METER),
        (int) (tx.getY() * Constants.PIXELS_PER_METER),
        (int) (radius * Constants.PIXELS_PER_METER)
      ).withColor(Color.RED);
    }
  }
}
