package demos;

import dev.gamekit.core.*;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.enums.TextAlignment;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.ui.widgets.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Demo3UserInterface extends Scene {
  int x = 0, y = 0;
  String text = "Kwame";
  BufferedImage squareImage = IO.loadImageResource("square-img.jpg");
  BufferedImage wideImage = IO.loadImageResource("wide-img.jpg");

  public Demo3UserInterface() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application("Demo 3 - Declarative UI") { };
    game.loadScene(new Demo3UserInterface());
    game.run();
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

  @Override
  public Widget onCreateUI() {
    return Align.create(
      Alignment.CENTER,
      FixedSize.create(
        600, 480,
        Column.create(
            Padding.create(
              new Spacing(x),
              Stack.create(
                FixedSize.create(
                  300, 150,
                  Image.create(wideImage)
                ),
                FixedSize.create(
                  400, 200,
                  Image.create(squareImage)
                )
              )
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
                  new Spacing(x),
                  Text.create("Another Text")
                    .withColor(Color.CYAN)
                )
              ).withMainAxisAlignment(MainAxisAlignment.SPACE_BETWEEN)
              .withCrossAxisAlignment(CrossAxisAlignment.STRETCH)
              .withGapSize(10)
          ).withMainAxisAlignment(MainAxisAlignment.SPACE_BETWEEN)
          .withCrossAxisAlignment(CrossAxisAlignment.CENTER)
          .withGapSize(10)
      )
    );
  }
}
