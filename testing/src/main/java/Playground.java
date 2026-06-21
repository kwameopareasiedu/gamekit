import dev.gamekit.core.Application;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;

public class Playground extends Scene {
  public Playground() {
    super("Playground");
  }

  public static void main(String[] args) {
    Application game = new Application(new Settings("Playground", Resolution.FULL_HD)) { };
//    game.loadScene(new Playground());
    game.run();
  }
}
