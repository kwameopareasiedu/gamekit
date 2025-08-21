import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;

import java.awt.*;

/**
 * This demo shows how to draw stuff unto the screen using the {@link Renderer}. It performs the
 * following actions:
 * <ul>
 *   <li>Creates an {@link Application application}</li>
 *   <li>Overrides the {@link Scene#render} method to draw</li>
 *   <li>Uses the {@link Renderer} to clear the screen to dark gray</li>
 *   <li>Uses the {@link Renderer} to draw a red box</li>
 * </ul>
 */
public class Demo2Renderer extends Scene {
  public Demo2Renderer() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application("Demo 2 - Renderer") { };
    game.loadScene(new Demo2Renderer());
    game.run();
  }

  @Override
  public void render() {
    // Clear the screen with dark gray
    Renderer.clear(Color.DARK_GRAY);
    // Draw a red box
    Renderer.fillRect(0, 0, 100, 100).withColor(Color.RED);
  }
}
