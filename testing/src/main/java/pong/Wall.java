package pong;

import dev.gamekit.components.RigidBody;
import dev.gamekit.core.Component;
import dev.gamekit.core.Entity;

import java.util.List;

public class Wall extends Entity {
  private static final double[][] TYPE_TRANSFORM = new double[][]{
    new double[]{ 0, 2, 6, 0.125 },
    new double[]{ 2.95, 0, 0.125, 4.11 },
    new double[]{ 0, -2, 6, 0.125 },
    new double[]{ -2.95, 0, 0.125, 4.11 },
  };

  private final Type type;
  private double rotation = 0;

  public Wall(Type type) {
    super(String.format("Wall %s", type.toString()));

    this.type = type;
  }

  @Override
  protected List<Component> getComponents() {
    RigidBody rb = new RigidBody();

    rb.setUserData(
      String.format("Wall %s", type.toString())
    );

    double[] tx = TYPE_TRANSFORM[type.toIndex()];

    rb.setPosition(tx[0], tx[1]);
    rb.addRectFixture(tx[2], tx[3], (fx, shape) -> {});

    return List.of(rb);
  }

  @Override
  protected void update() {
    double[] tx = TYPE_TRANSFORM[type.toIndex()];
    RigidBody rb = findComponent(RigidBody.class);

    rb.setRotation(30, 0, 0, tx[0], tx[1]);
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
