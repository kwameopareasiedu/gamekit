package pong;

import dev.gamekit.core.Entity;
import dev.gamekit.core.Input;
import dev.gamekit.core.Component;
import dev.gamekit.components.RigidBody;
import dev.gamekit.components.Transform;
import dev.gamekit.core.Constants;

import java.util.List;

import static dev.gamekit.utils.Math.clamp;

public class Paddle extends Entity {
  private static final double MOVE_SPEED = 3.5;
  private static final double MAX_TRANSLATE_DISTANCE = 1.15;
  private static final double[][] TYPE_TRANSFORM = new double[][]{
    new double[]{ -2.5, 0.5, 0.125, 1.5 },
    new double[]{ 2.5, 0, 0.125, 1.5 },
  };

  private final Type type;

  public Paddle(Type type) {
    super(String.format("Paddle %s", type.toString()));

    this.type = type;
  }

  @Override
  protected List<Component> getComponents() {
    RigidBody physicalTrait = new RigidBody();

    physicalTrait.setUserData(
      String.format("Paddle %s", type.toString())
    );

    double[] tx = TYPE_TRANSFORM[type.toIndex()];

    physicalTrait.setPosition(tx[0], tx[1]);
    physicalTrait.addRectFixture(tx[2], tx[3], (fx, shape) -> {});

    return List.of(physicalTrait);
  }

  @Override
  protected void update() {
    super.update();

    Transform transformTrait = findComponent(Transform.class);
    RigidBody physicalTrait = findComponent(RigidBody.class);

    switch (type) {
      case LEFT -> {
        if (Input.isKeyPressed(Input.KEY_S)) {
          double newY = transformTrait.getY() - 0.001 * MOVE_SPEED * Constants.FRAME_INTERVAL_MS;
          newY = clamp(newY, -MAX_TRANSLATE_DISTANCE, MAX_TRANSLATE_DISTANCE);
          physicalTrait.setPosition(transformTrait.getX(), newY);
        } else if (Input.isKeyPressed(Input.KEY_W)) {
          double newY = transformTrait.getY() + 0.001 * MOVE_SPEED * Constants.FRAME_INTERVAL_MS;
          newY = clamp(newY, -MAX_TRANSLATE_DISTANCE, MAX_TRANSLATE_DISTANCE);
          physicalTrait.setPosition(transformTrait.getX(), newY);
        }
      }
      case RIGHT -> {
        if (Input.isKeyPressed(Input.KEY_DOWN)) {
          double newY = transformTrait.getY() - 0.001 * MOVE_SPEED * Constants.FRAME_INTERVAL_MS;
          newY = clamp(newY, -MAX_TRANSLATE_DISTANCE, MAX_TRANSLATE_DISTANCE);
          physicalTrait.setPosition(transformTrait.getX(), newY);
        } else if (Input.isKeyPressed(Input.KEY_UP)) {
          double newY = transformTrait.getY() + 0.001 * MOVE_SPEED * Constants.FRAME_INTERVAL_MS;
          newY = clamp(newY, -MAX_TRANSLATE_DISTANCE, MAX_TRANSLATE_DISTANCE);
          physicalTrait.setPosition(transformTrait.getX(), newY);
        }
      }
    }
  }

  public enum Type {
    LEFT, RIGHT;

    int toIndex() {
      return switch (this) {
        case LEFT -> 0;
        case RIGHT -> 1;
      };
    }
  }
}
