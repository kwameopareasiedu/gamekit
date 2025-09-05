import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static dev.gamekit.utils.Math.*;

public class Playground {
  private static final Logger LOGGER = LogManager.getLogger();

  public static void main(String[] args) {
    double angle = degToRad(355);
    double desiredAngle = degToRad(315);

    while (!isPracticallyZero(angle - desiredAngle)) {
      angle = lerpAngle(angle, desiredAngle, 0.05);
      LOGGER.debug("Angle: {}°", radToDeg(angle));
    }
  }
}
