import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.ui.Alignment;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;

import java.awt.*;

public class Playground extends Scene {
  int x = 0, y = 0;
  String text = "Kwame";

  public Playground() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Window.setFullscreen(false);
    Window.setResolution(Window.Resolution._800_600);
    Application game = new Application("Playground") { };

    game.loadScene(new Playground());
    game.run();
  }

  @Override
  protected Widget onCreateWidgetTree() {
    return Align.create(
      Column.create(
        Image.create("wide-img.jpg").withSize(300, 150),
        Text.create("another text").withShadow(true).withShadowColor(Color.BLACK).withShadowOffset(2, 3),
        Row.create(
          Text.create(text).withBackgroundColor(Color.BLACK),
          Padding.create(
            Text.create("text 3").withColor(Color.BLACK),
            new Spacing(15)
          )
        )
      ),
      Alignment.CENTER
    );
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    if (Input.isKeyJustPressed(Input.KEY_SPACE)) {
      updateWidgetTree(() -> text = "Kwame");
    } else if (Input.isKeyJustReleased(Input.KEY_SPACE)) {
      updateWidgetTree(() -> text = "Opare");
    }

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
    Renderer.clearScene();

    Renderer.setColor(Color.RED);
    Renderer.fillRoundRect(-50 - x, -50 - y, 100, 100, 10, 10);
    Renderer.setColor(Color.YELLOW);
    Renderer.fillRoundRect(50 + x, -50 - y, 100, 100, 10, 10);
    Renderer.setColor(Color.GREEN);
    Renderer.fillRoundRect(50 + x, 50 + y, 100, 100, 10, 10);
    Renderer.setColor(Color.CYAN);
    Renderer.fillRoundRect(-50 - x, 50 + y, 100, 100, 10, 10);
  }
}
