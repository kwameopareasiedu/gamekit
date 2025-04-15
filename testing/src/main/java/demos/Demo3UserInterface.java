package demos;

import dev.gamekit.core.*;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.enums.TextAlignment;
import dev.gamekit.ui.widgets.Button;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.ui.widgets.ButtonParam.*;
import static dev.gamekit.ui.widgets.FlexParam.*;
import static dev.gamekit.ui.widgets.TextParam.*;
import static dev.gamekit.ui.widgets.WidgetParam.mouseListener;

public class Demo3UserInterface extends Scene {
  int x = 0, y = 0;
  String text = "Kwame";
  BufferedImage squareImage = IO.getResourceImage("square-img.jpg");
  BufferedImage wideImage = IO.getResourceImage("wide-img.jpg");
  BufferedImage btnBgImage = IO.getResourceImage("btn-bg.png");
  BufferedImage btnHoverImage = IO.getResourceImage("btn-hover.png");

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
      Sized.create(
        600, 480,
        Column.create(
          mainAxisAlignment(MainAxisAlignment.SPACE_BETWEEN),
          crossAxisAlignment(CrossAxisAlignment.CENTER),
          gapSize(10),
          children(
            Padding.create(
              new Spacing(x),
              Stack.create(
                Sized.create(
                  300, 150,
                  Image.create(wideImage)
                ),
                Sized.create(
                  400, 200,
                  Image.create(squareImage)
                ),
                Sized.create(
                  150, 60,
                  Button.create(
                    defaultBackground(btnBgImage),
                    hoverBackground(btnHoverImage),
                    pressedBackground(btnBgImage),
                    spacing(new Spacing(10, 8, 25, 8)),
                    mouseListener((e) -> System.out.println(e.type)),
                    child(
                      Text.create(
                        text("Click Me")
                      )
                    )
                  )
                )
              )
            ),
            Sized.create(
              128, 80,
              Text.create(
                text("Hello World"),
                alignment(TextAlignment.CENTER),
                verticalAlignment(TextAlignment.END),
                shadowEnabled(true),
                shadowColor(Color.GRAY),
                shadowOffsetX(10),
                shadowOffsetY(10),
                fontStyle(Font.BOLD)
              )
            ),
            Row.create(
              mainAxisAlignment(MainAxisAlignment.SPACE_BETWEEN),
              crossAxisAlignment(CrossAxisAlignment.STRETCH),
              gapSize(10),
              children(
                Text.create(
                  text(text),
                  fontSize(24)
                ),
                Padding.create(
                  new Spacing(x),
                  Text.create(
                    text("Another Text"),
                    color(Color.CYAN)
                  )
                )
              )
            )
          )
        )
      )
    );
  }
}
