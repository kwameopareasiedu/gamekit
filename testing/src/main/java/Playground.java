import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.enums.TextAlignment;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Config;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Resolution;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Playground extends Scene {
  static {
    Audio.preload("alert", "cybertruck.wav");
  }

  double pan = 0;
  int halfWindowWidth = 0;
  private final BufferedImage speakerImg = IO.getResourceImage("speaker.png");

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
  protected void onStart() {
    super.onStart();

    halfWindowWidth = Window.getInstance().getFrameWidth() / 2;
  }

  @Override
  protected void onUpdate() {
    super.onUpdate();

    if (Input.isKeyJustPressed(Input.KEY_SPACE)) {
      Audio.setGain("alert", -5f);
      Audio.play("alert");
    }

    Position mousePos = Input.getMousePosition();
    updateUI(() ->
      pan = (double) (mousePos.x - halfWindowWidth) / (halfWindowWidth)
    );
    Audio.setPan("alert", (float) pan);
  }

  @Override
  protected void onRender() {
    super.onRender();

    Renderer.setBackground(Color.DARK_GRAY);
    Renderer.clear();
  }

  @Override
  public Widget onCreateUI() {
    return Column.create(
        Text.create("Press the Space Bar to play/restart the audio")
          .withAlignment(TextAlignment.CENTER),

        Text.create("Move the mouse from left to right to pan the audio")
          .withAlignment(TextAlignment.CENTER),

        Row.create(
            Column.create(
                Opacity.create(
                  pan < 0 ? 1 : 1 - pan, Scaled.create(
                    0.5, Image.create(speakerImg)
                  )
                ),

                Text.create("Left Speaker")
                  .withAlignment(TextAlignment.CENTER)
                  .withFontSize(12)
              )
              .withCrossAxisAlignment(CrossAxisAlignment.CENTER)
              .withGapSize(16),

            Column.create(
                Opacity.create(
                  pan > 0 ? 1 : 1 + pan, Scaled.create(
                    0.5, Image.create(speakerImg)
                  )
                ),

                Text.create("Right Speaker")
                  .withAlignment(TextAlignment.CENTER)
                  .withFontSize(12)
              )
              .withCrossAxisAlignment(CrossAxisAlignment.CENTER)
              .withGapSize(16)
          ).withMainAxisAlignment(MainAxisAlignment.CENTER)
          .withCrossAxisAlignment(CrossAxisAlignment.CENTER)
          .withGapSize(8)
      ).withMainAxisAlignment(MainAxisAlignment.CENTER)
      .withCrossAxisAlignment(CrossAxisAlignment.STRETCH)
      .withGapSize(24);
  }
}
