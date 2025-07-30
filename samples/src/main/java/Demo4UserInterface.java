import dev.gamekit.core.Application;
import dev.gamekit.core.IO;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.*;
import dev.gamekit.ui.BorderData;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.ImageFit;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.ui.widgets.Button;
import dev.gamekit.ui.widgets.Checkbox;
import dev.gamekit.ui.widgets.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * This demo shows how to build user interfaces and performs the following actions:
 * <ul>
 *   <li>Creates an {@link Application application}</li>
 *   <li>Overrides the {@link Scene#createUI()}} method to construct the user interface</li>
 * </ul>
 */
public class Demo4UserInterface extends Scene {
  private static final BufferedImage BACKDROP = IO.getResourceImage("planetfall-artwork.jpg");
  private static final BufferedImage LOGO = IO.getResourceImage("planetfall-logo.png");
  private static final BufferedImage SCRIM = IO.getResourceImage("transparent-black.png");

  private String fieldValue = "Hello";
  private boolean checkboxValue = false;

  public Demo4UserInterface() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Settings(
        "Demo 4 - Declarative UI",
        Resolution.HD,
        WindowMode.WINDOWED,
        Antialiasing.ON,
        TextAntialiasing.ON,
        AlphaInterpolation.SPEED,
        ImageInterpolation.NEAREST,
        RenderingStrategy.SPEED,
        Dithering.OFF
      )
    ) { };
    game.loadScene(new Demo4UserInterface());
    game.run();
  }

  @Override
  public Widget createUI() {
    return Stack.create(
      Image.create(BACKDROP),

      Align.create(
        Align.config().horizontalAlignment(Alignment.START).verticalAlignment(Alignment.START),
        Padding.create(
          Padding.config().padding(24, 0),
          Sized.create(
            Sized.config().width(480).height(480),
            Image.create(LOGO)
          )
        )
      ),

      Align.create(
        Align.config().horizontalAlignment(Alignment.START).verticalAlignment(Alignment.CENTER),
        Padding.create(
          Padding.config().padding(256, 8, 16, 96),
          Theme.create(
            Theme.config().textFontSize(10).textFontStyle(Font.BOLD).textFontStyle(Font.ITALIC),
            Column.create(
              Column.config()
                .mainAxisAlignment(MainAxisAlignment.START)
                .crossAxisAlignment(CrossAxisAlignment.STRETCH)
                .gapSize(24),
              MainMenuButton.create("Tutorial", e -> System.out.println("0: " + e.type)),
              MainMenuButton.create("New Planet", e -> System.out.println("1: " + e.type)),
              Field.create(
                Field.config().fontSize(24).fontStyle(Font.PLAIN)
                  .padding(8).defaultBorder(new BorderData(4, 24, Color.RED))
                  .changeListener(ev -> {
                    fieldValue = ev.value;
                    updateUI();
                  }),
                fieldValue
              ),
              Checkbox.create(
                Checkbox.config().value(checkboxValue).changeListener(ev -> {
                  checkboxValue = ev.value;
                  updateUI();
                }),
                Text.create(
                  Text.config().fontSize(20).fontStyle(Font.PLAIN),
                  "Active"
                )
              ),
              Column.create(
                Column.config()
                  .mainAxisAlignment(MainAxisAlignment.START)
                  .crossAxisAlignment(CrossAxisAlignment.START)
                  .gapSize(12),
                SubMenuButton.create("Commander Customization"),
                SubMenuButton.create("Options"),
                SubMenuButton.create("Credits"),
                SubMenuButton.create("Exit Game")
              )
            )
          )
        )
      ),

      Align.create(
        Align.config().verticalAlignment(Alignment.START),
        Sized.create(
          Sized.config().fractionalWidth(1).fractionalHeight(0.15),
          Image.create(
            Image.config().fit(ImageFit.CROP),
            SCRIM
          )
        )
      ),

      Align.create(
        Align.config().verticalAlignment(Alignment.END),
        Sized.create(
          Sized.config().fractionalWidth(1).fractionalHeight(0.15),
          Stack.create(
            Sized.create(
              Sized.config().fractionalWidth(1).fractionalHeight(1),
              Image.create(
                Image.config().fit(ImageFit.STRETCH),
                SCRIM
              )
            ),
            Sized.create(
              Sized.config().fractionalWidth(1).fractionalHeight(1),
              Row.create(
                Row.config()
                  .mainAxisAlignment(MainAxisAlignment.END)
                  .crossAxisAlignment(CrossAxisAlignment.CENTER)
                  .gapSize(24),
                Sized.create(
                  Sized.config().width(48).height(48),
                  Colored.create(
                    Colored.config().color(Color.RED).borderRadius(4)
                  )
                ),
                Button.create(
                  Button.config().ninePatch(12, 12, 18, 12),
                  Text.create(
                    Text.config().fontSize(12).fontStyle(Font.BOLD),
                    "Create Account"
                  )
                ),
                Button.create(
                  Button.config().ninePatch(12, 12, 18, 12),
                  Text.create(
                    Text.config().fontSize(12).fontStyle(Font.BOLD),
                    "Login"
                  )
                )
              )
            )
          )
        )
      )
    );
  }

  static class MainMenuButton extends Compose {
    protected String text;

    public MainMenuButton(String text, MouseEvent.Handler mouseListener) {
      super(
        Button.create(
          Button.config().ninePatch(12, 12, 16, 12).mouseListener(mouseListener),
          Padding.create(
            Padding.config().padding(12, 12, 16, 12),
            Text.create(
              Text.config().fontSize(20).fontStyle(Font.BOLD),
              text
            )
          )
        )
      );

      this.text = text;
    }

    public static MainMenuButton create(String text, MouseEvent.Handler mouseListener) {
      return new MainMenuButton(text, mouseListener);
    }

    @Override
    public boolean stateEquals(Widget widget) {
      if (widget instanceof MainMenuButton mainMenuButton)
        return Objects.equals(text, mainMenuButton.text);

      return false;
    }

    @Override
    protected void performUpdateState(Widget widget) {
      this.text = ((MainMenuButton) widget).text;
    }
  }

  static class SubMenuButton extends Compose {
    protected String text;

    public SubMenuButton(String text) {
      super(
        Button.create(
          Button.config().ninePatch(6, 6, 8, 6),
          Padding.create(
            Padding.config().padding(6, 6, 8, 6),
            Text.create(
              Text.config(),
              text
            )
          )
        )
      );

      this.text = text;
    }

    public static SubMenuButton create(String text) {
      return new SubMenuButton(text);
    }

    @Override
    public boolean stateEquals(Widget widget) {
      if (widget instanceof SubMenuButton subMenuButton)
        return Objects.equals(text, subMenuButton.text);

      return false;
    }

    @Override
    protected void performUpdateState(Widget widget) {
      this.text = ((SubMenuButton) widget).text;
    }
  }
}
