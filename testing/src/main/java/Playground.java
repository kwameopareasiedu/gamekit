import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.settings.WindowMode;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.ui.widgets.Button;

import java.awt.*;

public class Playground extends Scene {

  public Playground() {
    super("Main Scene");
    //    Widget.DEBUG_DRAW = true;
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Settings("Playground", Resolution.SVGA, WindowMode.WINDOWED)
    ) { };
    game.loadScene(new Playground());
    game.run();
  }

  @Override
  protected void render() {
    Renderer.clear(Color.LIGHT_GRAY);
  }

  @Override
  public Widget createUI() {
    return Center.create(
      Sized.create(
        Sized.config().fractionalWidth(0.5).intrinsicHeight(),
        Column.create(
          Column.config().crossAxisAlignment(CrossAxisAlignment.CENTER),
          Text.create(
            Text.config().alignment(Alignment.CENTER).shadow(true, 2, 3, Color.BLACK)
              .backgroundColor(Color.DARK_GRAY),
            "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Donec odio. Quisque volutpat mattis eros. Nullam malesuada erat ut turpis. Suspendisse urna nibh viverra non semper suscipit posuere a pede."
          ),
          Button.create(
            Button.config(),
            Padding.create(
              Padding.config().padding(8, 12, 16, 12),
              Text.create("Hello There!")
            )
          )
        )
      )
    );
  }
}
