package demos;

import dev.gamekit.animation.Animation;
import dev.gamekit.animation.AnimationCurve;
import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.settings.WindowMode;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.widgets.Align;
import dev.gamekit.ui.widgets.Padding;
import dev.gamekit.ui.widgets.Text;
import dev.gamekit.ui.widgets.Widget;

import java.awt.*;

import static dev.gamekit.utils.Math.toInt;

public class Animations extends Scene<Animations.State> {

  private final Animation bounceAnimation = new Animation(
    4000, Animation.RepeatMode.ALTERNATE, AnimationCurve.EASE_IN_OUT_BOUNCE
  ).setValueListener(value -> {
//    animationValue = value;
//    updateUI();
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
    super.start();
    bounceAnimation.start();
  }

  @Override
  protected void update(State state) {
    state.animationValue = bounceAnimation.getValue();
    updateUI();
  }

  @Override
  public void render(State state) {
    // Clear the screen with black
    Renderer.setColor(Color.BLACK);
    Renderer.clear();

    Renderer.setColor(Color.CYAN);
    Renderer.fillCircle(0, toInt(-200 * state.animationValue) + 150, 50);
  }

  @Override
  protected State createState() {
    return new State();
  }

  @Override
  public Widget createUI(State state) {
    return Align.create(
      Align.options().horizontalAlignment(Alignment.START),
      Padding.create(
        Padding.options().padding(new Spacing(16, 48)),
        Text.create(
          String.format("Value: %f", state.animationValue)
        )
      )
    );
  }

  public static class State extends Scene.State<State> {
    double animationValue = 0;

    @Override
    public void copy(State state) {
      animationValue = state.animationValue;
    }
  }
}