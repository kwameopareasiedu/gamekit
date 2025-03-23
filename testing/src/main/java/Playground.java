import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Alignment;

import java.awt.*;
import java.util.List;

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
    Text text = new Text("The quick brown fox jumps over the lazy dog");
    text.setBackgroundColor(Color.BLACK);

    Text text2 = new Text("I've got a shadow");
    text2.toggleShadow(true);
    text2.setShadowColor(Color.LIGHT_GRAY);
    text2.setShadowOffset(2, 3);

    Text text3 = new Text("Text 3");

    Image img = new Image("wide-img.jpg");
    img.getSize().set(300, 150);

    uiManager.setRoot(
      new Align(
        new Column(
          List.of(
            img,
            text,
            new Row(
              List.of(text2, text3)
            )
          )
        ),
        Alignment.CENTER
      )
    );
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
