import dev.gamekit.core.*;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.enums.TextAlignment;
import dev.gamekit.ui.widgets.Button;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Config;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Resolution;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Playground extends Scene {
  private static final Color CLEAR_COLOR = new Color(0xff1f1f1f);

  int x = 0, y = 0;
  String text = "Kwame";
  BufferedImage squareImage = IO.loadImageResource("square-img.jpg");
  BufferedImage wideImage = IO.loadImageResource("wide-img.jpg");

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
  public void onUpdate() {
    super.onUpdate();

    if (Input.isButtonPressed(Input.BUTTON_LMB)) {
      Position mousePos = Input.getMousePosition();
      Position pt = Camera.screenToWorldPoint(mousePos.x, mousePos.y);
      x = pt.x;
      y = pt.y;
    }
  }

  @Override
  public void onRender() {
    super.onRender();
    Renderer.setColor(CLEAR_COLOR);
    Renderer.clear();
    Renderer.setColor(Color.RED);
    Renderer.fillRoundRect(-50 + x, -50 + y, 100, 100, 10, 10);
    Renderer.setColor(Color.YELLOW);
    Renderer.fillRoundRect(50 + x, -50 + y, 100, 100, 10, 10);
    Renderer.setColor(Color.GREEN);
    Renderer.fillRoundRect(50 + x, 50 + y, 100, 100, 10, 10);
    Renderer.setColor(Color.CYAN);
    Renderer.fillRoundRect(-50 + x, 50 + y, 100, 100, 10, 10);
  }

  @Override
  public Widget onCreateUI() {
    return Align.create(
      FixedSize.create(
        640, 480,
        Column.create(
            Padding.create(
              Button.create(
                Stack.create(
                  Image.create(wideImage).withSize(300, 150),
                  Image.create(squareImage).withSize(400, 200)
                )
              ),
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
          .withCrossAxisAlignment(CrossAxisAlignment.CENTER)
          .withGapSize(10)
      ),
      Alignment.CENTER
    );
  }
}
