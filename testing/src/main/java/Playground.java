import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.ui.Alignment;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.WidgetState;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;

import java.awt.*;

public class Playground extends Scene {
  private final WidgetState<Integer> x = new WidgetState<>(0);
  private final WidgetState<Integer> y = new WidgetState<>(0);

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

    createWidgetTree(
      Align.create(
        Column.create(
          Image.create("wide-img.jpg").withSize(300, 150),
          Text.create("another text").withShadow(true).withShadowColor(Color.BLACK).withShadowOffset(2, 3),
          Row.create(
            Text.create("with bg color text").withBackgroundColor(Color.BLACK),
            Padding.create(
              Text.create("text 3").withColor(Color.BLACK),
              new Spacing(15)
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
      x.set(50);
      y.set(50);
    } else {
      x.set(0);
      y.set(0);
    }
  }

  @Override
  public void onRender() {
    super.onRender();
    // Clear the screen with black
    Renderer.setColor(Color.DARK_GRAY);
    Renderer.clearScene();

    Renderer.setColor(Color.RED);
    Renderer.fillRoundRect(-50 - x.get(), -50 - y.get(), 100, 100, 10, 10);
    Renderer.setColor(Color.YELLOW);
    Renderer.fillRoundRect(50 + x.get(), -50 - y.get(), 100, 100, 10, 10);
    Renderer.setColor(Color.GREEN);
    Renderer.fillRoundRect(50 + x.get(), 50 + y.get(), 100, 100, 10, 10);
    Renderer.setColor(Color.CYAN);
    Renderer.fillRoundRect(-50 - x.get(), 50 + y.get(), 100, 100, 10, 10);
  }
}
