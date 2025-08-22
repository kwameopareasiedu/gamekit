import dev.gamekit.utils.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static dev.gamekit.utils.Math.radToDeg;

public class Playground {
  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) {
    Vector origin = new Vector();
    Vector[] targets = new Vector[]{
      new Vector(0, 1),
      new Vector(1, 1),
      new Vector(1, 0),
      new Vector(1, -1),
      new Vector(0, -1),
      new Vector(-1, -1),
      new Vector(-1, 0),
      new Vector(-1, 1),
      new Vector(0, 1),
    };

    for (Vector target : targets) {
      LOGGER.debug("Angle: {}°", radToDeg(Vector.angle(origin, target)));
    }
  }
}
