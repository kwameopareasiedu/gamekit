import dev.gamekit.core.Application;
import dev.gamekit.core.Input;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Spacing;

import java.awt.*;

import static dev.gamekit.utils.Math.cycle;

/**
 * This demo shows how to detect input using the {@link Input} interface and performs the
 * following actions:
 * <ul>
 *   <li>Creates an {@link Application application}</li>
 *   <li>Overrides the {@link Scene#render}} method to draw</li>
 *   <li>Detects mouse input using {@link Input} to change the color</li>
 *   <li>Uses the {@link Renderer} to draw box with the current color</li>
 * </ul>
 */
public class Demo3Input extends Scene {
  private static final Color[] COLORS = new Color[]{
    Color.RED,
    Color.YELLOW,
    Color.GREEN,
    Color.BLUE,
  };
  private int colorIndex = 0;

  public Demo3Input() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application("Demo 3 - Input") { };
    game.loadScene(new Demo3Input());
    game.run();
  }

  @Override
  protected void update() {
    if (Input.isButtonDown(Input.BUTTON_LMB) || Input.isKeyPressed(Input.KEY_SPACE)) {
      colorIndex = cycle(colorIndex + 1, 0, COLORS.length - 1);
    }
  }

  @Override
  public void render() {
    // Clear the screen with dark gray
    Renderer.clear(Color.BLACK);
    // Draw a red box
    Renderer.fillRect(0, 0, 100, 100).withColor(COLORS[colorIndex]);
  }

  @Override
  protected Widget createUI() {
    return Align.create(
      AlignConfig.horizontalAlignment(Alignment.CENTER),
      AlignConfig.verticalAlignment(Alignment.END),
      AlignConfig.child(
        Padding.create(
          PaddingConfig.padding(new Spacing(48, 48, 48, 48)),
          PaddingConfig.child(
            Text.create(
              TextConfig.alignment(Alignment.CENTER),
              TextConfig.text("Click the Left Mouse Button or press the Space Bar to change color")
            )
          )
        )
      )
    );
  }
}
