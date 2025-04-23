package demos;

import dev.gamekit.core.Application;
import dev.gamekit.core.IO;
import dev.gamekit.core.Scene;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.widgets.Button;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Config;
import dev.gamekit.utils.Resolution;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.gamekit.ui.widgets.AlignParam.horizontalAlignment;
import static dev.gamekit.ui.widgets.AlignParam.verticalAlignment;
import static dev.gamekit.ui.widgets.ButtonParam.mouseListener;
import static dev.gamekit.ui.widgets.ButtonParam.spacing;
import static dev.gamekit.ui.widgets.FlexParam.*;
import static dev.gamekit.ui.widgets.ImageParam.image;
import static dev.gamekit.ui.widgets.MultiChildParentParam.children;
import static dev.gamekit.ui.widgets.PaddingParam.padding;
import static dev.gamekit.ui.widgets.SingleChildParentParam.child;
import static dev.gamekit.ui.widgets.SizedParam.height;
import static dev.gamekit.ui.widgets.SizedParam.width;
import static dev.gamekit.ui.widgets.TextParam.*;

public class Demo3UserInterface extends Scene {
  BufferedImage backdrop = IO.getResourceImage("planetfall-artwork.jpg");
  BufferedImage icon = IO.getResourceImage("planetfall-logo.png");
  BufferedImage scrim = IO.getResourceImage("transparent-black.png");

  public Demo3UserInterface() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Config("Demo 3 - Declarative UI", Resolution.HD, false)
    ) { };
    game.loadScene(new Demo3UserInterface());
    game.run();
  }

  @Override
  public Widget createUI() {
    return Stack.create(
      children(
        Image.create(
          image(backdrop)
        ),
        Align.create(
          horizontalAlignment(Alignment.START),
          verticalAlignment(Alignment.START),
          child(
            Padding.create(
              padding(new Spacing(24, 0)),
              child(
                Sized.create(
                  width(480),
                  height(480),
                  child(
                    Image.create(
                      image(icon)
                    )
                  )
                )
              )
            )
          )
        ),
        Align.create(
          horizontalAlignment(Alignment.START),
          verticalAlignment(Alignment.CENTER),
          child(
            Padding.create(
              padding(new Spacing(256, 0, 0, 96)),
              child(
                Column.create(
                  mainAxisAlignment(MainAxisAlignment.START),
                  crossAxisAlignment(CrossAxisAlignment.STRETCH),
                  gapSize(24),
                  children(
                    MainMenuButton.create("Tutorial",
                      e -> System.out.println("0: " + e.type)),
                    MainMenuButton.create("New Planet",
                      e -> System.out.println("1: " + e.type)),
                    MainMenuButton.create("New Campaign", null),
                    MainMenuButton.create("Load Game", null),
                    MainMenuButton.create("Online Multiplayer", null),

                    Column.create(
                      mainAxisAlignment(MainAxisAlignment.START),
                      crossAxisAlignment(CrossAxisAlignment.START),
                      gapSize(12),
                      children(
                        SubMenuButton.create("Commander Customization"),
                        SubMenuButton.create("Options"),
                        SubMenuButton.create("Credits"),
                        SubMenuButton.create("Exit Game")
                      )
                    )
                  )
                )
              )
            )
          )
        ),
        Align.create(
          verticalAlignment(Alignment.START),
          child(
            Sized.create(
              width(Resolution.NATIVE.width()),
              height(128),
              child(
                Image.create(
                  image(scrim)
                )
              )
            )
          )
        ),
        Align.create(
          verticalAlignment(Alignment.END),
          child(
            Stack.create(
              children(
                Sized.create(
                  width(Resolution.NATIVE.width()),
                  height(128),
                  child(
                    Image.create(
                      image(scrim)
                    )
                  )
                ),
                Sized.create(
                  width(Resolution.NATIVE.width()),
                  height(128),
                  child(
                    Row.create(
                      mainAxisAlignment(MainAxisAlignment.END),
                      crossAxisAlignment(CrossAxisAlignment.CENTER),
                      gapSize(24),
                      children(
                        Button.create(
                          spacing(new Spacing(12)),
                          child(
                            Padding.create(
                              padding(new Spacing(12, 12, 18, 12)),
                              child(
                                Text.create(
                                  text("Create Account"),
                                  fontSize(12),
                                  fontStyle(Font.BOLD)
                                )
                              )
                            )
                          )
                        ),
                        Button.create(
                          spacing(new Spacing(12)),
                          child(
                            Padding.create(
                              padding(new Spacing(12, 12, 18, 12)),
                              child(
                                Text.create(
                                  text("Login"),
                                  fontSize(12),
                                  fontStyle(Font.BOLD)
                                )
                              )
                            )
                          )
                        )
                      )
                    )
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
          mouseListener(mouseListener),
          child(
            Padding.create(
              padding(new Spacing(12, 12, 16, 12)),
              child(
                Text.create(
                  text(text),
                  fontSize(20),
                  fontStyle(Font.BOLD)
                )
              )
            )
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
          spacing(new Spacing(12)),
          child(
            Padding.create(
              padding(new Spacing(6, 6, 12, 6)),
              child(
                Text.create(
                  text(text),
                  fontSize(12),
                  fontStyle(Font.BOLD)
                )
              )
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
      if (widget instanceof SubMenuButton subMenuButton) {
        return Objects.equals(text, subMenuButton.text);
      }

      return false;
    }
  }
}
