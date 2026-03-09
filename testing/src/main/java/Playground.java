import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.Settings;
import dev.gamekit.settings.WindowMode;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.ui.widgets.Panel;

import java.awt.*;
import java.util.Arrays;

public class Playground extends Scene {
  public Playground() {
    super("Playground");
  }

  public static void main(String[] args) {
    Application play = new Application(
      new Settings(
        "Playground",
        WindowMode.BORDERLESS
      )
    ) { };
    play.loadScene(new Playground());
    play.run();
  }

  @Override
  protected void render() {
    Renderer.clear(Color.BLACK);
  }

  @Override
  protected Widget createUI() {
    return Center.create(
      Sized.create(
        props -> {
          props.fixedWidth = 384.0;
          props.fixedHeight = 384.0;
        },
        Grid.create(
          props -> {
            props.columnCount = 3;
            props.columnGapSize = 10;
            props.rowGapSize = 50;
          },
//          Arrays.stream("Hello GameKit".split(""))
          Arrays.stream(
              new Color[]{
                Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
                Color.BLUE, Color.PINK, Color.DARK_GRAY, Color.CYAN,
                Color.WHITE
              })
            .map(
              (color) -> Panel.create(
                props -> props.color = color,
                Empty.create()
              )
            )
            .toArray(Widget[]::new)
        )
      )
    );
  }
}
