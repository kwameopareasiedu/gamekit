package features;

import dev.gamekit.animation.Animation;
import dev.gamekit.animation.AnimationCurves;
import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.scene.Scene;

import java.awt.*;

import static dev.gamekit.utils.MathUtils.toInt;

public class AnimationTesting extends Scene {
  private final Animation bounceAnimation = new Animation(
    4, Animation.RepeatMode.REVERSE, AnimationCurves.EASE_IN_OUT_BOUNCE
  );

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
    Renderer.fillCircle(0, toInt(-200 * bounceAnimation.getValue()), 50);
  }
}