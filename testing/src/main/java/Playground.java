import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Window;
import dev.gamekit.core.Scene;

import java.awt.*;

public class Playground extends Scene {
  private int x = 0, y = 0;

  public Playground() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Window.setFullscreen(true);
    Window.setResolution(Window.Resolution._800_600);
    Application game = new Application("PlayGround") { };

    game.loadScene(new Playground());
    game.run();
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

  }

  @Override
  public void onRender() {
    // Clear the screen with black
    Renderer.setColor(Color.DARK_GRAY);
    Renderer.clear();

    Renderer.setColor(Color.RED);
    Renderer.fillRect(x - 50, y - 50, 100, 100);
    Renderer.setColor(Color.YELLOW);
    Renderer.fillRect(x + 50, y - 50, 100, 100);
    Renderer.setColor(Color.GREEN);
    Renderer.fillRect(x + 50, y + 50, 100, 100);
    Renderer.setColor(Color.CYAN);
    Renderer.fillRect(x - 50, y + 50, 100, 100);

    super.onRender();
  }
}
