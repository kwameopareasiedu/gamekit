import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Alignment;
import dev.gamekit.utils.Spacing;

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

    uiManager.setRoot(
      Align.create(
        Column.create(
          Image.create("wide-img.jpg").withSize(300, 150).get(),
          Text.create("another text").withShadow(true).withShadowColor(Color.BLACK).withShadowOffset(2, 3).get(),
          Row.create(
            Text.create("with bg color text").withBackgroundColor(Color.BLACK).get(),
            Padding.create(
              Text.create("text 3").withColor(Color.BLACK).get(),
              new Spacing(15)
            ).get()
          ).get()
        ).get(),
        Alignment.CENTER
      ).get()
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
