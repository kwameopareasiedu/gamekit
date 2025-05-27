import dev.gamekit.core.Application;
import dev.gamekit.core.RendererOld;
import dev.gamekit.core.Scene;

import java.awt.*;

/**
 * This demo shows how to draw stuff unto the screen using the {@link RendererOld}. It performs the
 * following actions:
 * <ul>
 *   <li>Creates an {@link Application application}</li>
 *   <li>Overrides the {@link Scene#render()} method to draw</li>
 *   <li>Uses the {@link RendererOld} to clear the screen to dark gray</li>
 *   <li>Uses the {@link RendererOld} to draw a red box</li>
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
    RendererOld.setColor(Color.DARK_GRAY);
    RendererOld.clear();
    // Draw a red box
    RendererOld.setColor(Color.RED);
    RendererOld.fillRect(0, 0, 100, 100);
  }
}
