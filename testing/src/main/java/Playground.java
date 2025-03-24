import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Alignment;
import dev.gamekit.utils.Spacing;

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
    Image img = new Image("wide-img.jpg");
    img.getSize().set(300, 150);

    uiManager.setRoot(
      new Align(
        new Column(
          List.of(
            img,
            Text.create("another text").withShadow(true).withShadowColor(Color.BLACK).withShadowOffset(2, 3).get(),
            new Row(
              List.of(
                Text.create("with bg color text").withBackgroundColor(Color.BLACK).get(),
                new Padding(
                  Text.create("text 3").get(),
                  new Spacing(15)
                )
              )
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
    super.onRender();
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
  }
}
