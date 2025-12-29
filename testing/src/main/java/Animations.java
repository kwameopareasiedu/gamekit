import dev.gamekit.animation.Animation;
import dev.gamekit.animation.AnimationCurve;
import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.settings.WindowMode;
import dev.gamekit.ui.widgets.Align;
import dev.gamekit.ui.widgets.Padding;
import dev.gamekit.ui.widgets.Text;
import dev.gamekit.ui.widgets.Widget;

import java.awt.*;

public class Animations extends Scene {

  double animationValue = 0;
  private final Animation bounceAnimation = new Animation(
    4000, Animation.RepeatMode.ALTERNATE, AnimationCurve.EASE_IN_OUT_BOUNCE
  ).setValueListener((value) -> {
    animationValue = value;
    updateUI();
  });

  public Animations() {
    super("Animation Testing");
  }

  public static void main(String[] args) {
    // Create a new game application
    Application game = new Application(
      new Settings("Animation Testing", Resolution.HD, WindowMode.WINDOWED)
    ) { };
    // Load an instance of our Scene class
    game.loadScene(new Animations());
    // Run the game application
    game.run();
  }

  @Override
  public void start() {
    bounceAnimation.start();
  }

  @Override
  public void render() {
    Renderer.clear(Color.BLACK);
    Renderer.fillCircle(0, (int) (-200 * animationValue) + 150, 50).withColor(Color.CYAN);
  }

  @Override
  public Widget createUI() {
    return Align.create(
      props -> { },
      Padding.create(
        48, 16, 48, 16,
        Text.create(String.format("Value: %f", animationValue))
      )
    );
  }
}