import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.ui.Container;
import dev.gamekit.ui.Text;
import dev.gamekit.ui.VBox;

import java.awt.*;

public class Playground extends Scene {
  private int x = 0, y = 0;

  public Playground() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Window.setFullscreen(true);
    Window.setResolution(Window.Resolution._800_600);
    Application game = new Application("Playground") { };

    game.loadScene(new Playground());
    game.run();
  }

  @Override
  public void onStart() {
    super.onStart();
    Container vbox = new VBox();
    vbox.getPadding().set(0);

    Text text = new Text("The quick brown fox jumps over the lazy dog");
    text.getPadding().set(25);

    Text text2 = new Text("I've got a shadow");
    text2.toggleShadow(true);
    text2.setShadowColor(Color.GRAY);
//    text2.setBgColor(Color.BLACK);
    text2.setShadowOffset(2, 2);
    text2.getPadding().set(4, 25);

    vbox.addChild(text);
    vbox.addChild(text2);
    rootNode = vbox;
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    if (Input.isKeyPressed(Input.KEY_SPACE)) {
      x = y = 50;
    } else {
      x = y = 0;
    }
  }

  @Override
  public void onRender() {
    // Clear the screen with black
    Renderer.setColor(Color.DARK_GRAY);
    Renderer.clear();

    Renderer.setColor(Color.RED);
    Renderer.fillRoundRect(x - 50, y - 50, 100, 100, 10, 10);
    Renderer.setColor(Color.YELLOW);
    Renderer.fillRoundRect(x + 50, y - 50, 100, 100, 10, 10);
    Renderer.setColor(Color.GREEN);
    Renderer.fillRoundRect(x + 50, y + 50, 100, 100, 10, 10);
    Renderer.setColor(Color.CYAN);
    Renderer.fillRoundRect(x - 50, y + 50, 100, 100, 10, 10);

    super.onRender();
  }
}
