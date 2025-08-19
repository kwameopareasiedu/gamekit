import dev.gamekit.components.*;
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
import java.util.ArrayList;
import java.util.Arrays;
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
    Collider.DEBUG_DRAW = true;

    addChild(new BoxFrame());
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

  public static class BoxFrame extends Entity {
    private static final double[][] WALL_TRANSFORMS = new double[][]{
      new double[]{ 0, 256, 768, 16.0 },
      new double[]{ 377.6, 0, 16.0, 526.08 },
      new double[]{ 0, -256, 768, 16.0 },
      new double[]{ -377.6, 0, 16.0, 526.08 },
    };

    public BoxFrame() {
      super("Box Frame");
    }

    @Override
    protected List<Component> getComponents() {
      List<Component> components = new ArrayList<>();

      // Create a RigidBody component
      RigidBody rb = new RigidBody();
      // Add the rigid body to the components
      components.add(rb);

      Arrays.stream(WALL_TRANSFORMS).forEach(tx -> {
        // Create a BoxCollider with width and height
        BoxCollider wallCollider = new BoxCollider(tx[2], tx[3]);
        // Set metadata (for collision identification)
        wallCollider.setMetaData("Wall");
        // Offset the box collider
        wallCollider.setOffset(tx[0], tx[1]);
        // Add the box collider to the components
        components.add(wallCollider);
      });

      // Return a list of components for the Wall entity
      return components;
    }
  }

  public static class Ball extends Entity {
    private final double radius = 12.8;

    public Ball() {
      super("Ball");
    }

    @Override
    protected List<Component> getComponents() {
      // Create a RigidBody component
      RigidBody rb = new RigidBody(
        MassType.NORMAL, new Vector(), 1, 1
      );

      rb.setGravityScale(0.5);
      // Attach an id to the rigid body (For identification in collision)
      rb.setMetaData("Ball");
      // Apply an instantaneous impulse to the rigid body
      rb.applyImpulse(0, -0.5);
      // Apply a rotational torque to the rigid body
      rb.applyTorque(0);

      // Add a circle fixture
      CircleCollider circle = new CircleCollider(radius);
      // Set the circle shape's density
      circle.setDensity(15);
      // Set coefficient of restitution to 0.5
      circle.setRestitution(0);
      // Set friction to 0.5
      circle.setFriction(1);
      // Register a collision listener to be notified when this fixture collides with another
      circle.setCollisionListener(new Physics.CollisionListener() {
        @Override
        public void onCollisionEnter(Collider.BodyAttachedFixture otherFixture) {
          logger.debug("Ball collided with {}", otherFixture.getUserData());
        }

        @Override
        public void onCollisionExit(Collider.BodyAttachedFixture otherFixture) {
          logger.debug("Ball no longer colliding with {}", otherFixture.getUserData());
        }
      });

      return List.of(rb, circle);
    }

    @Override
    protected void render() {
      Transform tx = findComponent(Transform.class);
      Renderer.fillCircle((int) (tx.getX()), (int) (tx.getY()), (int) radius)
        .withColor(Color.RED);
    }
  }
}
