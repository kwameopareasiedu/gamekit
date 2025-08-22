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
 *   <li>Overrides the {@link Entity#render}} method to render the scene</li>
 * </ul>
 */
public class Demo6EntityComponents extends Scene {
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
    addChild(new Ball());
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
      new double[]{ 0, 0, 512, 16 },
      new double[]{ 0, 256, 512, 16 },
      new double[]{ 256, 0, 16, 512 },
      new double[]{ 0, -256, 512, 16 },
      new double[]{ -256, 0, 16, 512 },
    };

    private double rotation = 0;
    private RigidBody rbRef;

    public BoxFrame() {
      super("Box Frame");
    }

    @Override
    protected List<Component> getComponents() {
      List<Component> components = new ArrayList<>();

      // Create a RigidBody component
      RigidBody rb = rbRef = new RigidBody();
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

        if (tx == WALL_TRANSFORMS[0])
          wallCollider.setSensor(true);
      });

      // Return a list of components for the Wall entity
      return components;
    }

    @Override
    protected void update() {
      double rotationRate = 0.001;
      rotation = (rotation + rotationRate) % (2 * Math.PI);
      rbRef.setRotation(rotation);
    }
  }

  public static class Ball extends Entity {
    private final Random rnd = new Random();
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

      Application.getInstance().scheduleTask(() -> {
        // Apply an instantaneous impulse to the rigid body
        rb.applyImpulse(-2, 0.5);
        // Apply a rotational torque to the rigid body
        rb.applyTorque(3);
      }, 100);

      // Add a circle fixture
      CircleCollider circle = new CircleCollider(radius);
      // Set the circle shape's density
      circle.setDensity(15);
      // Set coefficient of restitution to 0.5
      circle.setRestitution(0.5);
      // Set friction to 0.5
      circle.setFriction(0.5);
      // Register a collision listener to be notified when this collider collides with another
      // collider
      circle.setCollisionListener(new Physics.CollisionListener() {
        @Override
        public void onCollisionEnter(Collider otherCollider) {
          logger.debug("Ball collided with {}", otherCollider.getMetaData());
        }

        @Override
        public void onCollisionExit(Collider otherCollider) {
          logger.debug("Ball no longer colliding with {}", otherCollider.getMetaData());
        }
      });

      return List.of(rb, circle);
    }

    @Override
    protected void start() {
      addChild(new BallChild());
    }

    @Override
    protected void update() {
      if (Input.isKeyDown(Input.KEY_SPACE)) {
        RigidBody rb = findComponent(RigidBody.class);
        rb.applyImpulse(-1.5 + rnd.nextDouble(3), 0.5 + rnd.nextDouble(2));
      }
    }

    @Override
    protected void render() {
      Transform tx = findComponent(Transform.class);
      Vector globalPosition = tx.getGlobalPosition();
      int posX = (int) (globalPosition.x);
      int posY = (int) (globalPosition.y);
      Renderer.fillCircle(posX, posY, (int) radius).withColor(Color.RED)
        .withRotation(posX, posY, tx.getGlobalRotation());
    }
  }

  public static class BallChild extends Entity {
    public BallChild() {
      super("Ball Child");
    }

    @Override
    protected void start() {
      findComponent(Transform.class).setGlobalPosition(10, 10);
    }

    @Override
    protected void render() {
      Transform tx = findComponent(Transform.class);
      Vector globalPosition = tx.getGlobalPosition();
      double radius = 5;
      int posX = (int) (globalPosition.x);
      int posY = (int) (globalPosition.y);

      Renderer.fillCircle(posX, posY, (int) radius).withColor(Color.YELLOW)
        .withRotation(posX, posY, tx.getGlobalRotation());
      Renderer.drawVerticalLine(posX, posY, posY + (int) radius).withColor(Color.RED)
        .withRotation(posX, posY, tx.getGlobalRotation());
    }
  }
}
