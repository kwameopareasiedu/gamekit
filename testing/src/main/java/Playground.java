import dev.gamekit.components.BoxCollider;
import dev.gamekit.components.Collider;
import dev.gamekit.components.RigidBody;
import dev.gamekit.core.*;
import dev.gamekit.core.Component;
import dev.gamekit.utils.Vector;

import java.awt.*;
import java.util.List;

import static dev.gamekit.utils.Math.degToRad;
import static dev.gamekit.utils.Math.radToDeg;

public class Playground extends Scene {
  public Playground() {
    super("Playground");
    Collider.DEBUG_DRAW = true;
  }

  public static void main(String[] args) {
    Application play = new Application("Playground") { };
    play.loadScene(new Playground());
    play.run();
  }

  @Override
  protected void start() {
    addChild(new Box());
  }

  @Override
  protected void update() {
    if (Input.isKeyDown(Input.KEY_SPACE)) {
      for (double posAngle = 0; posAngle <= 2 * Math.PI; posAngle += degToRad(45)) {
        Vector pos = Vector.from(100, posAngle);

        for (double rayAngle = 0; rayAngle <= 2 * Math.PI; rayAngle += degToRad(45)) {
          List<Physics.RaycastHit> raycastHits = Physics.raycast(pos, rayAngle, 200, 1);

          if (!raycastHits.isEmpty()) {
            logger.debug("Raycasting from {} at {}{} hit", pos, radToDeg(rayAngle), dev.gamekit.utils.Math.DEGREE_SYM);
          }
        }

        System.out.println();
      }
    }
  }

  @Override
  protected void render() {
    Renderer.clear(Color.BLACK);
  }

  private static class Box extends Entity {
    public Box() {
      super("Box");
    }

    @Override
    protected List<Component> getComponents() {
      RigidBody rb = new RigidBody();

      BoxCollider collider = new BoxCollider(100, 100);
      collider.setCollisionFilter(1, 1);

      return List.of(rb, collider);
    }
  }
}
