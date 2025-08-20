import dev.gamekit.utils.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Playground {
  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) {
    Vector origin = new Vector();

    for (int deg = 0; deg < 360; deg += 15) {
      Vector target = new Vector(1, 0);
      origin.rotatePoint(target, deg);
      LOGGER.debug("Rotated about {}° = {}", deg, target);
    }
  }
}
