import dev.gamekit.audio.*;
import dev.gamekit.audio.shapes.AudioShapeCircle;
import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
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
import static dev.gamekit.ui.widgets.ImageParam.image;
import static dev.gamekit.ui.widgets.OpacityParam.opacity;
import static dev.gamekit.ui.widgets.ScaledParam.scale;
import static dev.gamekit.ui.widgets.SingleChildParentParam.child;
import static dev.gamekit.ui.widgets.TextParam.alignment;
import static dev.gamekit.ui.widgets.TextParam.text;

public class Playground extends Scene {
  private static final Logger LOGGER = LogManager.getLogger();
  final Vector listenerPos;
  final Position prevMousePos;

  double pan = 0;
  int halfWindowWidth = 0;
  BufferedImage speakerImg = IO.getResourceImage("speaker.png");

  public Playground() {
    super("Main Scene");

    listenerPos = new Vector(0, 0);
    prevMousePos = new Position(0, 0);

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
  protected void start() {
    super.start();

    halfWindowWidth = Window.getInstance().getFrameWidth() / 2;
    Audio.<AudioClip3D>get("waterflow").setPosition(0, 0);
    //    Audio.get("waterflow").play(true);
  }

  @Override
  protected void update() {
    super.update();

    if (Input.isKeyDown(Input.KEY_SPACE)) {
      Audio.get("alert").play();
    }

    Position center = Window.getInstance().getCenter();
    Position mousePos = Input.getMousePosition();
    listenerPos.set(
      0.1 * (mousePos.x - center.x),
      0.1 * (center.y - mousePos.y)
    );

    AudioListener.setPosition(listenerPos);

    if (!prevMousePos.equals(mousePos)) {
      updateUI(() ->
        pan = (double) (mousePos.x - halfWindowWidth) / (halfWindowWidth)
      );
    }
    //    Audio.setPan("alert", (float) pan);

    prevMousePos.set(mousePos);
  }

  @Override
  protected void render() {
    super.render();

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
          alignment(Alignment.CENTER)
        ),
        Text.create(
          text("Move the mouse from left to right to pan the audio"),
          alignment(Alignment.CENTER)
        ),
        Row.create(
          mainAxisAlignment(MainAxisAlignment.CENTER),
          crossAxisAlignment(CrossAxisAlignment.CENTER),
          gapSize(8),
          children(
            Opacity.create(
              opacity(pan < 0 ? 1 : 1 - pan),
              child(
                Scaled.create(
                  scale(0.5),
                  child(
                    Image.create(
                      image(speakerImg)
                    )
                  )
                )
              )
            ),
            Opacity.create(
              opacity(pan > 0 ? 1 : 1 + pan),
              child(
                Scaled.create(
                  scale(0.5),
                  child(
                    Image.create(
                      image(speakerImg)
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
}
