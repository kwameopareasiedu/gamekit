import dev.gamekit.audio.*;
import dev.gamekit.audio.shapes.AudioShapeCircle;
import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;

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
      new Settings("Playground", Resolution.SVGA, false)
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
  public Widget createUI() {
    return Column.create(
      Column.options()
        .mainAxisAlignment(MainAxisAlignment.CENTER)
        .crossAxisAlignment(CrossAxisAlignment.STRETCH)
        .gapSize(24),
      Text.create(
        Text.options().alignment(Alignment.CENTER),
        "Press the Space Bar to play/restart the audio"
      ),
      Text.create(
        Text.options().alignment(Alignment.CENTER),
        "Move the mouse from left to right to pan the audio"
      ),
      Row.create(
        Row.options()
          .mainAxisAlignment(MainAxisAlignment.CENTER)
          .crossAxisAlignment(CrossAxisAlignment.CENTER)
          .gapSize(8),
        Opacity.create(
          Opacity.options().opacity(pan < 0 ? 1 : 1 - pan),
          Scaled.create(
            Scaled.options().scale(0.5),
            Image.create(speakerImg)
          )
        ),
        Opacity.create(
          Opacity.options().opacity(pan > 0 ? 1 : 1 + pan),
          Scaled.create(
            Scaled.options().scale(0.5),
            Image.create(speakerImg)
          )
        )
      )
    );
  }
}
