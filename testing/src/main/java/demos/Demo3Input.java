package demos;

import dev.gamekit.core.Application;
import dev.gamekit.core.Input;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;

import java.awt.*;

import static dev.gamekit.utils.Math.cycle;

/**
 * This demo shows how to detect input using the {@link Input} interface.
 * <p>
 * This demo performs the following actions:
 * <ul>
 *   <li>Create an {@link Application application}</li>
 *   <li>Override the {@link Scene#render()} method to draw</li>
 *   <li>Use the {@link Renderer} to draw a red box</li>
 *   <li>Detect mouse input using {@link Input} to change the color</li>
 * </ul>
 */
public class Demo3Input extends Scene {
  public Demo3Input() {
    super("Main Scene");
  }

  private static final Color[] COLORS = new Color[] {
    Color.RED,
    Color.YELLOW,
    Color.GREEN,
    Color.BLUE,
  };

  private int colorIndex = 0;

  public static void main(String[] args) {
    Application game = new Application("Demo 3 - Input") { };
    game.loadScene(new Demo3Input());
    game.run();
  }

  @Override
  protected void update() {
    if (Input.isButtonDown(Input.BUTTON_LMB)) {
      colorIndex = cycle(colorIndex + 1, 0, COLORS.length - 1);
    }
  }

  @Override
  public void render() {
    super.render();
    // Clear the screen with dark gray
    Renderer.setColor(Color.DARK_GRAY);
    Renderer.clear();
    // Draw a red box
    Renderer.setColor(COLORS[colorIndex]);
    Renderer.fillRect(0, 0, 100, 100);
  }
}
