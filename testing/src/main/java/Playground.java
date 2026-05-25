import dev.gamekit.core.Application;
import dev.gamekit.core.IO;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.utils.Picture;

import java.awt.*;

public class Playground extends Scene {
  private static final Picture SIMPLE = IO.getImage("keep-it-simple-light.jpg");
  private static final Picture PLANET_FALL = IO.getImage("planetfall-artwork.jpg");
  private static final Picture MASK = IO.getImage("planetfall-logo-mask.png");

  public Playground() {
    super("Playground");
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Settings(
        "Playground",
        Resolution.FULL_HD
      )
    ) { };
    game.loadScene(new Playground());
    game.run();
  }

  @Override
  public void render() {
    Renderer.clear(Color.BLACK);
    Renderer.drawImage(SIMPLE, 0, 0, SIMPLE.getWidth(), SIMPLE.getHeight());
    Renderer.drawImage(PLANET_FALL, 0, 0, PLANET_FALL.getWidth(), PLANET_FALL.getHeight())
      .withMaskImage(MASK);
  }
}
