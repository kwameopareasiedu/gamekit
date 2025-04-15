import dev.gamekit.audio.*;
import dev.gamekit.audio.shapes.AudioShapeCircle;
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
import dev.gamekit.utils.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.gamekit.ui.widgets.FlexParam.*;
import static dev.gamekit.ui.widgets.TextParam.*;

public class Playground extends Scene {
  private static final Logger LOGGER = LogManager.getLogger();
  final Vector listenerPos;

  double pan = 0;
  int halfWindowWidth = 0;
  private final BufferedImage speakerImg = IO.getResourceImage("speaker.png");

  public Playground() {
    super("Main Scene");

    listenerPos = new Vector(0, 0);

    Audio.preload("alert",
      new AudioClip2D("cybertruck.wav", AudioGroup.MUSIC, 0.5)
    );

    Audio.preload("waterflow",
      new AudioClip3D("waterflow.wav", AudioGroup.EFFECTS, 1,
        AudioAttenuation.LINEAR, new AudioShapeCircle(5, 30)
      )
    );
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
    Audio.<AudioClip3D>get("waterflow").setPosition(0, 0);
    Audio.get("waterflow").play(true);
  }

  @Override
  protected void onUpdate() {
    super.onUpdate();

    if (Input.isKeyJustPressed(Input.KEY_SPACE)) {
      Audio.get("alert").play();
    }

    Position center = Window.getInstance().getCenter();
    Position mousePos = Input.getMousePosition();
    listenerPos.set(
      0.1 * (mousePos.x - center.x),
      0.1 * (center.y - mousePos.y)
    );

    AudioListener.setPosition(listenerPos);
    //    updateUI(() ->
    //      pan = (double) (mousePos.x - halfWindowWidth) / (halfWindowWidth)
    //    );
    //    Audio.setPan("alert", (float) pan);
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
      mainAxisAlignment(MainAxisAlignment.CENTER),
      crossAxisAlignment(CrossAxisAlignment.STRETCH),
      gapSize(24),
      children(
        Text.create(
          text("Press the Space Bar to play/restart the audio"),
          alignment(TextAlignment.CENTER)
        ),
        Text.create(
          text("Move the mouse from left to right to pan the audio"),
          alignment(TextAlignment.CENTER)
        ),
        Row.create(
          mainAxisAlignment(MainAxisAlignment.CENTER),
          crossAxisAlignment(CrossAxisAlignment.CENTER),
          gapSize(8),
          children(
            Column.create(
              crossAxisAlignment(CrossAxisAlignment.CENTER),
              gapSize(16),
              children(
                Opacity.create(
                  pan < 0 ? 1 : 1 - pan, Scaled.create(
                    0.5, Image.create(speakerImg)
                  )
                ),
                Text.create(
                  text("Left Speaker"),
                  alignment(TextAlignment.CENTER),
                  fontSize(12)
                )
              )
            ),
            Column.create(
              crossAxisAlignment(CrossAxisAlignment.CENTER),
              gapSize(16),
              children(
                Opacity.create(
                  pan > 0 ? 1 : 1 + pan, Scaled.create(
                    0.5, Image.create(speakerImg)
                  )
                ),
                Text.create(
                  text("Right Speaker"),
                  alignment(TextAlignment.CENTER),
                  fontSize(12)
                )
              )
            )
          )
        )
      )
    );
  }
}
