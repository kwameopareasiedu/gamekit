import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.*;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.widgets.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class Playground extends Scene {
  private static final Logger LOGGER = LogManager.getLogger();

  public Playground() {
    super("Playground");
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Settings(
        "Playground",
        WindowMode.WINDOWED,
        Resolution.HD,
        TextAntialiasing.ON
      )
    ) { };
    game.loadScene(new Playground());
    game.run();
  }

  @Override
  protected void render() {
    Renderer.clear(Color.BLACK);
  }

  @Override
  protected Widget createUI() {
    return Center.create(
      Align.create(
        Align.config().horizontalAlignment(Alignment.CENTER).verticalAlignment(Alignment.CENTER),
        Column.create(
          Column.config().crossAxisAlignment(CrossAxisAlignment.STRETCH),
          Text.create(
            Text.config().fontSize(72).alignment(Alignment.CENTER),
            "Hello GameKit"
          ),
          Text.create(
            Text.config().fontSize(24).alignment(Alignment.CENTER),
            "User Interface Sample"
          )
        )
      )
    );
  }
}
