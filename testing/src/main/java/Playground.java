import dev.gamekit.animation.Animation;
import dev.gamekit.animation.AnimationSlice;
import dev.gamekit.core.Application;
import dev.gamekit.core.Scene;

public class Playground extends Scene {
  private final Animation animation;
  private final AnimationSlice slice;

  public Playground() {
    super("Playground");

    animation = new Animation(1000);
    animation.setStateListener(state -> {
      if (state == Animation.State.STOPPED)
        Application.getInstance().quit();
    });

    slice = new AnimationSlice(animation, 0.5, 1);
  }

  public static void main(String[] args) {
    Application play = new Application("Playground") { };
    play.loadScene(new Playground());
    play.run();
  }

  @Override
  protected void start() {
    animation.start();
  }

  @Override
  protected void update() {
    logger.debug("Values: [{}, {}]", animation.getValue(), slice.getValue());
  }
}
