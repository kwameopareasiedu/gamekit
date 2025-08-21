import dev.gamekit.audio.AudioAttenuation;
import dev.gamekit.audio.AudioClip3D;
import dev.gamekit.audio.AudioGroup;
import dev.gamekit.audio.AudioListener;
import dev.gamekit.audio.shapes.AudioShape;
import dev.gamekit.audio.shapes.AudioShapeCircle;
import dev.gamekit.core.*;
import dev.gamekit.core.Window;
import dev.gamekit.settings.*;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Vector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * This demo shows how to work with audio in GameKit and performs the following actions:
 * <ul>
 *   <li>Creates an {@link Application application}</li>
 *   <li>
 *     Preloads a {@link AudioClip3D 3D audio clip} with a circle {@link AudioShape shape} and
 *     linear {@link AudioAttenuation attenuation}
 *   </li>
 *   <li>Detects mouse input using {@link Input} to start/restart/stop the audio playback</li>
 *   <li>Overrides the {@link Scene#createUI}} method to construct a simple user interface</li>
 * </ul>
 */
public class Demo5Audio extends Scene {
  private static final BufferedImage SPEAKER_IMG = IO.getResourceImage("speaker.png");
  private static final String MUSIC_KEY = "music";

  private double pan = 0;
  private final int halfWindowWidth;
  private final Vector listenerPos;
  private final Position prevMousePos;

  public Demo5Audio() {
    super("Main Scene");

    halfWindowWidth = Window.getInfo().frameWidth() / 2;
    listenerPos = new Vector(0, 0);
    prevMousePos = new Position(0, 0);

    Audio.preload(MUSIC_KEY,
      new AudioClip3D("cybertruck.wav", AudioGroup.MUSIC, 1,
        AudioAttenuation.LINEAR, new AudioShapeCircle(5, 30)
      )
    );
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Settings(
        "Demo 5 - Audio",
        Resolution.SVGA,
        WindowMode.WINDOWED,
        TextAntialiasing.ON,
        Antialiasing.ON
      )
    ) { };
    game.loadScene(new Demo5Audio());
    game.run();
  }

  @Override
  protected void start() {
    Audio.<AudioClip3D>get(MUSIC_KEY).setPosition(0, 0);
  }

  @Override
  protected void update() {
    if (Input.isKeyDown(Input.KEY_SPACE))
      Audio.get(MUSIC_KEY).play();

    if (Input.isKeyDown(Input.KEY_ESCAPE))
      Audio.get(MUSIC_KEY).stop();

    Window.Info windowInfo = Window.getInfo();
    Position mousePos = Input.getMousePosition();

    listenerPos.set(
      0.1 * (windowInfo.displayCenterX() - mousePos.x),
      0.1 * (windowInfo.displayCenterY() - mousePos.y)
    );

    AudioListener.setPosition(listenerPos);

    if (!Objects.equals(prevMousePos, mousePos)) {
      pan = (double) (mousePos.x - halfWindowWidth) / halfWindowWidth;
      prevMousePos.set(mousePos);
      updateUI();
    }
  }

  @Override
  protected void render() {
    Renderer.clear(Color.BLACK);
  }

  @Override
  public Widget createUI() {
    return Column.create(
      Column.config()
        .mainAxisAlignment(MainAxisAlignment.CENTER)
        .crossAxisAlignment(CrossAxisAlignment.STRETCH)
        .gapSize(8),
      Text.create(
        Text.config().alignment(Alignment.CENTER),
        "Space Bar to play/restart the audio"
      ),
      Text.create(
        Text.config().alignment(Alignment.CENTER),
        "Escape to stop the playback"
      ),
      Text.create(
        Text.config().alignment(Alignment.CENTER),
        "Move the mouse from left to right to pan the audio"
      ),
      Row.create(
        Row.config()
          .mainAxisAlignment(MainAxisAlignment.CENTER)
          .crossAxisAlignment(CrossAxisAlignment.CENTER)
          .gapSize(8),
        Opacity.create(
          Opacity.config().opacity(pan < 0 ? 1 : 1 - pan),
          Scaled.create(
            Scaled.config().scale(0.5),
            Image.create(
              Image.config().interpolation(ImageInterpolation.BICUBIC),
              SPEAKER_IMG
            )
          )
        ),
        Opacity.create(
          Opacity.config().opacity(pan > 0 ? 1 : 1 + pan),
          Scaled.create(
            Scaled.config().scale(0.5),
            Image.create(
              Image.config().interpolation(ImageInterpolation.BICUBIC),
              SPEAKER_IMG
            )
          )
        )
      )
    );
  }
}
