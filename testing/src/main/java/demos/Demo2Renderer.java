package demos;

import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Window;
import dev.gamekit.core.Scene;

import java.awt.*;

/**
 * This demo shows how to draw stuff unto the screen using the {@link Renderer}.
 * <p>
 * This demo performs the following actions:
 * <ul>
 *   <li>Create an {@link Application application}</li>
 *   <li>Override the {@link Scene#onRender()} method to draw</li>
 *   <li>Use the {@link Renderer} to clear the screen to dark gray</li>
 *   <li>Use the {@link Renderer} to draw a red box</li>
 * </ul>
 */
public class Demo2Renderer extends Scene {
  public Demo2Renderer() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Window.setFullscreen(false);
    Window.setResolution(Window.Resolution._800_600);
    Application game = new Application("Demo 2 - Renderer") { };

    game.loadScene(new Demo2Renderer());
    game.run();
  }

  @Override
  public void onRender() {
    super.onRender();
    // Clear the screen with dark gray
    Renderer.setColor(Color.DARK_GRAY);
    Renderer.clearScene();
    // Draw a red box
    Renderer.setColor(Color.RED);
    Renderer.fillRect(0, 0, 100, 100);
  }
}
