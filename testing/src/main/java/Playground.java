import dev.gamekit.core.Application;
import dev.gamekit.core.Input;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.enums.TextAlignment;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Config;
import dev.gamekit.utils.Resolution;

import java.awt.*;

public class Playground extends Scene {
  int x = 0, y = 0;
  String text = "Kwame";

  public Playground() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Config("Playground", Resolution.SVGA, false)
    ) { };
    game.loadScene(new Playground());
    game.run();
  }

  @Override
  public Widget onCreateUI() {
    return Align.create(
      FixedSize.create(
        600, 480,
        Column.create(
            Padding.create(
              Image.create("wide-img.jpg").withSize(300, 150),
              new Spacing(x)
            ),
            Text.create("Hello World")
              .withAlignment(TextAlignment.END)
              .withShadow(true)
              .withShadowColor(Color.BLACK)
              .withShadowOffset(10, 4)
              .withFontStyle(Font.BOLD),
            Row.create(
                Text.create(text)
                  .withFontSize(24),
                Padding.create(
                  Text.create("Another Text")
                    .withColor(Color.CYAN),
                  new Spacing(x)
                )
              ).withMainAxisAlignment(MainAxisAlignment.SPACE_BETWEEN)
              .withCrossAxisAlignment(CrossAxisAlignment.STRETCH)
              .withGapSize(10)
          ).withMainAxisAlignment(MainAxisAlignment.SPACE_BETWEEN)
          .withCrossAxisAlignment(CrossAxisAlignment.STRETCH)
          .withGapSize(10)
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
    Renderer.setColor(Color.DARK_GRAY);
    Renderer.clear();

    //    Renderer.setColor(Color.RED);
    //    Renderer.fillRoundRect(-50 - x, -50 - y, 100, 100, 10, 10);
    //    Renderer.setColor(Color.YELLOW);
    //    Renderer.fillRoundRect(50 + x, -50 - y, 100, 100, 10, 10);
    //    Renderer.setColor(Color.GREEN);
    //    Renderer.fillRoundRect(50 + x, 50 + y, 100, 100, 10, 10);
    //    Renderer.setColor(Color.CYAN);
    //    Renderer.fillRoundRect(-50 - x, 50 + y, 100, 100, 10, 10);
  }
}
