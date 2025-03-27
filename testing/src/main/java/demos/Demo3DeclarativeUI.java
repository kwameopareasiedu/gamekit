package demos;

import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;

import java.awt.*;

public class Demo3DeclarativeUI extends Scene {
  int x = 0, y = 0;
  String text = "Kwame";

  public Demo3DeclarativeUI() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Window.setFullscreen(false);
    Window.setResolution(Window.Resolution._800_600);
    Application game = new Application("Demo 3 - Declarative UI") { };

    game.loadScene(new Demo3DeclarativeUI());
    game.run();
  }

  @Override
  public Widget onCreateUI() {
    return Align.create(
      Column.create(
        Padding.create(
          Image.create("wide-img.jpg").withSize(300, 150),
          new Spacing(x)
        ),
        Text.create("another text").withShadow(true).withShadowColor(Color.BLACK).withShadowOffset(10, 4),
        Row.create(
          Text.create(text),
          Padding.create(
            Text.create("text 3").withColor(Color.BLACK),
            new Spacing(x)
          )
        )
      ),
      Alignment.CENTER
    );
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    if (Input.isKeyJustPressed(Input.KEY_SPACE)) {
      updateUI(() -> {
        text = "Kwame";
        x = y = 50;
      });
    } else if (Input.isKeyJustReleased(Input.KEY_SPACE)) {
      updateUI(() -> {
        text = "Opare";
        x = y = 0;
      });
    }
  }

  @Override
  public void onRender() {
    super.onRender();
    // Clear the screen with black
    Renderer.setColor(Color.BLACK);
    Renderer.clear();
  }
}
