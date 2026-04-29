import dev.gamekit.audio.AudioClip;
import dev.gamekit.audio.AudioListener;
import dev.gamekit.audio.attenuation.LinearAttenuation;
import dev.gamekit.core.*;
import dev.gamekit.core.Window;
import dev.gamekit.settings.*;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.utils.Picture;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Vector;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

/**
 * This demo shows how to work with audio in GameKit and performs the following actions:
 * <ul>
 *   <li>Creates an {@link Application application}</li>
 *   <li>
 *     Preloads a spatial {@link AudioClip} with {@link LinearAttenuation}
 *   </li>
 *   <li>Detects mouse input using {@link Input} to start/restart/stop the audio playback</li>
 *   <li>Overrides the {@link Scene#createUI}} method to construct a simple user interface</li>
 * </ul>
 */
public class Demo5Audio extends Scene {
  private static final Picture SPEAKER_IMG = IO.getImage("speaker.png");
  private static final String MUSIC_KEY = "music";
  private final int halfWindowWidth;
  private final Vector listenerPos;
  private final Position prevMousePos;
  private final AudioClip clip, clip2;
  private double pan = 0;

  public Demo5Audio() {
    super("Main Scene");

    halfWindowWidth = Window.getInstance().getDisplayWidth() / 2;
    listenerPos = new Vector(0, 0);
    prevMousePos = new Position(0, 0);

    try {
//      Audio.getBus(AudioBus.DEFAULT_ID).addFilter(new LowPassFilter(1000));
//      Audio.getBus(AudioBus.DEFAULT_ID).addFilter(new DelayFilter(100));
//      Audio.getBus(AudioBus.DEFAULT_ID).addFilter(new CombFilter(50, 0.8));
//      Audio.getBus(AudioBus.DEFAULT_ID).addFilter(new AllPassFilter(100, 0.8));
//      Audio.getBus(AudioBus.DEFAULT_ID).addFilter(new ReverbFilter(2500, 0.5, 0.));
      clip = Audio.loadClip("cybertruck.wav").setSpatial(true).setEventListener(logger::debug);
      clip2 = Audio.loadClip("beats.wav").setEventListener(logger::debug);
    } catch (UnsupportedAudioFileException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Settings(
        "Demo 5 - Audio",
        Resolution.WXGA,
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
//    Audio.<AudioClip3D>get(MUSIC_KEY).setPosition(0, 0);
  }

  @Override
  protected void update() {
    if (Input.isKeyDown(Input.KEY_SPACE)) {
      clip.play();
//      clip2.play();
    }

    if (Input.isKeyDown(Input.KEY_ESCAPE)) {
      clip.stop();
//      clip2.stop();
    }

    Window win = Window.getInstance();
    Position mousePos = Input.getMousePosition();

    listenerPos.set(
      0.1 * (win.getCenterX() - mousePos.x),
      0.1 * (win.getCenterY() - mousePos.y)
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
      props -> {
        props.mainAxisAlignment = MainAxisAlignment.CENTER;
        props.crossAxisAlignment = CrossAxisAlignment.STRETCH;
        props.gapSize = 8;
      },
      Text.create(
        props -> {
          props.alignment = Alignment.CENTER;
          props.text = "Space Bar to play/restart the audio";
        }
      ),
      Text.create(
        props -> {
          props.alignment = Alignment.CENTER;
          props.text = "Escape to stop the playback";
        }
      ),
      Text.create(
        props -> {
          props.alignment = Alignment.CENTER;
          props.text = "Move the mouse from left to right to pan the audio ";
        }
      ),
      Row.create(
        props -> {
          props.mainAxisAlignment = MainAxisAlignment.CENTER;
          props.crossAxisAlignment = CrossAxisAlignment.CENTER;
          props.gapSize = 8;
        },
        Opacity.create(
          pan < 0 ? 1 : 1 - pan,
          Scaled.create(
            0.5,
            Image.create(
              props -> {
                props.interpolation = ImageInterpolation.BICUBIC;
                props.image = SPEAKER_IMG;
              }
            )
          )
        ),
        Opacity.create(
          pan > 0 ? 1 : 1 + pan,
          Scaled.create(
            0.5,
            Image.create(
              props -> {
                props.interpolation = ImageInterpolation.BICUBIC;
                props.image = SPEAKER_IMG;
              }
            )
          )
        )
      )
    );
  }
}
