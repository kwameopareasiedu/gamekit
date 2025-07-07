import dev.gamekit.audio.*;
import dev.gamekit.audio.shapes.AudioShapeCircle;
import dev.gamekit.core.Window;
import dev.gamekit.core.*;
import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.settings.WindowMode;
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
import java.util.Objects;

public class Playground extends Scene {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final BufferedImage SPEAKER_IMG = IO.getResourceImage("speaker.png");

  private double pan = 0;
  private final int halfWindowWidth;
  private final Vector listenerPos;
  private final Position prevMousePos;

  public Playground() {
    super("Main Scene");

    halfWindowWidth = Window.getInstance().getFrameWidth() / 2;
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
      new Settings("Playground", Resolution.SVGA, WindowMode.WINDOWED)
    ) { };
    game.loadScene(new Playground());
    game.run();
  }

  @Override
  protected void start() {
    Audio.<AudioClip3D>get("waterflow").setPosition(0, 0);
    Audio.get("waterflow").play(true);
  }

  @Override
  protected void update() {
    if (Input.isKeyDown(Input.KEY_SPACE)) {
      Audio.get("alert").play();
    }

    Window win = Window.getInstance();
    Position mousePos = Input.getMousePosition();
    listenerPos.set(
      0.1 * (mousePos.x - win.getDisplayCenterX()),
      0.1 * (win.getDisplayCenterY() - mousePos.y)
    );

    AudioListener.setPosition(listenerPos);

    if (!Objects.equals(prevMousePos, mousePos)) {
      pan = (double) (mousePos.x - halfWindowWidth) / halfWindowWidth;
      prevMousePos.set(mousePos);
      updateUI();
    }
  }

  @Override
  protected void render(Renderer renderer) {
    renderer.clear(Color.DARK_GRAY);
    renderer.fillRect(0, 0, 64, 64).withColor(Color.CYAN);
    renderer.drawCircle(0, -100, 45).withColor(Color.BLUE);
//    RendererOld.setBackground(Color.DARK_GRAY);
//    RendererOld.clear();
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
            Image.create(SPEAKER_IMG)
          )
        ),
        Opacity.create(
          Opacity.options().opacity(pan > 0 ? 1 : 1 + pan),
          Scaled.create(
            Scaled.options().scale(0.5),
            Image.create(SPEAKER_IMG)
          )
        )
      )
    );
  }
}
