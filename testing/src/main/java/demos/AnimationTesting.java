package demos;

import dev.gamekit.animation.Animation;
import dev.gamekit.animation.AnimationCurve;
import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.widgets.Align;
import dev.gamekit.ui.widgets.Padding;
import dev.gamekit.ui.widgets.Text;
import dev.gamekit.ui.widgets.Widget;

import java.awt.*;

import static dev.gamekit.ui.widgets.AlignParam.*;
import static dev.gamekit.ui.widgets.PaddingParam.padding;
import static dev.gamekit.ui.widgets.TextParam.text;
import static dev.gamekit.utils.Math.toInt;

public class AnimationTesting extends Scene {
  double animationValue = 0;

  private final Animation bounceAnimation = new Animation(
    4, Animation.RepeatMode.REVERSE, AnimationCurve.EASE_IN_OUT_BOUNCE
  ).setValueListener(value -> updateUI(() -> animationValue = value));

  public AnimationTesting() {
    super("Animation Testing");
  }

  public static void main(String[] args) {
    // Create a new game application
    Application game = new Application("Animation Testing") { };
    // Load an instance of our Scene class
    game.loadScene(new AnimationTesting());
    // Run the game application
    game.run();
  }

  @Override
  public void onStart() {
    super.onStart();
    bounceAnimation.start();
  }

  @Override
  public void onRender() {
    super.onRender();
    // Clear the screen with black
    Renderer.setColor(Color.BLACK);
    Renderer.clear();

    Renderer.setColor(Color.CYAN);
    Renderer.fillCircle(0, toInt(-200 * bounceAnimation.getValue()) + 150, 50);
  }

  @Override
  public Widget onCreateUI() {
    return Align.create(
      horizontalAlignment(Alignment.START),
      child(
        Padding.create(
          padding(new Spacing(16, 48)),
          child(
            Text.create(
              text(String.format("Value: %f", animationValue))
            )
          )
        )
      )
    );
  }
}