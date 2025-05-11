package demos;

import dev.gamekit.core.Application;
import dev.gamekit.core.IO;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.WindowMode;
import dev.gamekit.settings.*;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.widgets.Button;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Demo3UserInterface extends Scene {
  BufferedImage backdrop = IO.getResourceImage("planetfall-artwork.jpg");
  BufferedImage icon = IO.getResourceImage("planetfall-logo.png");
  BufferedImage scrim = IO.getResourceImage("transparent-black.png");

  public Demo3UserInterface() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Settings(
        "Demo 3 - Declarative UI",
        Resolution.NATIVE,
        WindowMode.FULLSCREEN,
        Antialiasing.OFF,
        TextAntialiasing.ON,
        AlphaInterpolation.SPEED,
        ImageInterpolation.NEAREST,
        RenderingStrategy.SPEED,
        Dithering.OFF
      )
    ) { };
    game.loadScene(new Demo3UserInterface());
    game.run();
  }

  @Override
  public Widget createUI() {
    return Stack.create(
      Image.create(backdrop),

      Align.create(
        Align.options().horizontalAlignment(Alignment.START).verticalAlignment(Alignment.START),
        Padding.create(
          Padding.options().padding(new Spacing(24, 0)),
          Sized.create(
            Sized.options().width(480).height(480),
            Image.create(icon)
          )
        )
      ),
      Align.create(
        Align.options().horizontalAlignment(Alignment.START).verticalAlignment(Alignment.CENTER),
        Padding.create(
          Padding.options().padding(new Spacing(256, 0, 0, 96)),
          Column.create(
            Column.options()
              .mainAxisAlignment(MainAxisAlignment.START)
              .crossAxisAlignment(CrossAxisAlignment.STRETCH)
              .gapSize(24),
            MainMenuButton.create("Tutorial", e -> System.out.println("0: " + e.type)),
            MainMenuButton.create("New Planet", e -> System.out.println("1: " + e.type)),
            MainMenuButton.create("New Campaign", null),
            MainMenuButton.create("Load Game", null),
            MainMenuButton.create("Online Multiplayer", null),
            Column.create(
              Column.options()
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
      ),
      Align.create(
        Align.options().verticalAlignment(Alignment.START),
        Sized.create(
          Sized.options().fractionalWidth(1).fractionalHeight(0.15),
          Image.create(scrim)
        )
      ),
      Align.create(
        Align.options().verticalAlignment(Alignment.END),
        Sized.create(
          Sized.options().fractionalWidth(1).fractionalHeight(0.15),
          Stack.create(
            Expanded.create(
              Image.create(scrim)
            ),
            Expanded.create(
              Row.create(
                Row.options()
                  .mainAxisAlignment(MainAxisAlignment.END)
                  .crossAxisAlignment(CrossAxisAlignment.CENTER)
                  .gapSize(24),
                Sized.create(
                  Sized.options().width(48).height(48),
                  Colored.create(
                    Colored.options().color(Color.RED).borderRadius(4)
                  )
                ),
                Button.create(
                  Button.options().padding(new Spacing(12, 12, 18, 12)),
                  Text.create(
                    Text.options().fontSize(12).fontStyle(Font.BOLD),
                    "Create Account"
                  )
                ),
                Button.create(
                  Button.options().padding(new Spacing(12, 12, 18, 12)),
                  Text.create(
                    Text.options().fontSize(12).fontStyle(Font.BOLD),
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
    protected final String text;

    public MainMenuButton(String text, MouseEvent.Listener mouseListener) {
      super(
        Button.create(
          Button.options()
            .padding(new Spacing(12, 12, 16, 12))
            .mouseListener(mouseListener),
          Text.create(
            Text.options()
              .fontSize(20)
              .fontStyle(Font.BOLD),
            text
          )
        )
      );

      this.text = text;
    }

    public static MainMenuButton create(
      String text,
      MouseEvent.Listener mouseListener) {
      return new MainMenuButton(text, mouseListener);
    }

    @Override
    public boolean stateEquals(Widget widget) {
      if (widget instanceof MainMenuButton mainMenuButton) {
        return Objects.equals(text, mainMenuButton.text);
      }

      return false;
    }
  }

  static class SubMenuButton extends Compose {
    protected final String text;

    public SubMenuButton(String text) {
      super(
        Button.create(
          Button.options()
            .padding(new Spacing(6, 6, 8, 6)),
          Text.create(
            Text.options()
              .fontSize(12)
              .fontStyle(Font.BOLD),
            text
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
      if (widget instanceof SubMenuButton subMenuButton) {
        return Objects.equals(text, subMenuButton.text);
      }

      return false;
    }
  }
}
