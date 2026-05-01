import dev.gamekit.audio.AudioClip;
import dev.gamekit.audio.AudioListener;
import dev.gamekit.audio.AudioMixer;
import dev.gamekit.audio.attenuation.LinearAttenuation;
import dev.gamekit.audio.effects.AudioEffect;
import dev.gamekit.audio.effects.LowPassFilter;
import dev.gamekit.audio.effects.ReverbFilter;
import dev.gamekit.core.*;
import dev.gamekit.core.Window;
import dev.gamekit.settings.*;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.utils.GMath;
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
  private final int halfWindowWidth;
  private final Vector listenerPos;
  private final Position prevMousePos;
  private final AudioClip clip;
  private final AudioEffect[] effects;
  private final String[] effectNames;
  private int effectIndex = 0;
  private double pan = 0;

  public Demo5Audio() {
    super("Main Scene");

    halfWindowWidth = Window.getInstance().getDisplayWidth() / 2;
    listenerPos = new Vector(0, 0);
    prevMousePos = new Position(0, 0);

    effects = new AudioEffect[]{
      null,
      new LowPassFilter("lp", 1200, 1),
      new ReverbFilter("rv", 300, 0.8, 0.7)
    };

    effectNames = new String[]{
      "None",
      "Low Pass Filter [Cutoff=1.2KHz]",
      "Reverb Filter: [Delay=300ms, Damping=0.8, Mix=0.7]"
    };

    try {
      clip = Audio.loadClip("cybertruck.wav").setSpatial(true).setEventListener(logger::debug);
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
    clip.setPosition(0, 0);
    AudioListener.setPosition(0, 0);
  }

  @Override
  protected void update() {
    if (Input.isKeyDown(Input.KEY_SPACE))
      clip.play();

    if (Input.isKeyDown(Input.KEY_ESCAPE))
      clip.stop();

    if (Input.isKeyDown(Input.KEY_S)) {
      effectIndex = GMath.cycle(effectIndex + 1, 0, effects.length - 1);
      Audio.getMixer(AudioMixer.DEFAULT_ID).setEffects(effects[effectIndex]);
      updateUI();
    }

    Window win = Window.getInstance();
    Position mousePos = Input.getMousePosition();

    if (!Objects.equals(prevMousePos, mousePos)) {
      listenerPos.set(
        0.1 * (win.getCenterX() - mousePos.x),
        0.1 * (win.getCenterY() - mousePos.y)
      );

      AudioListener.setPosition(listenerPos);

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
          props.text = "'S' to cycle through audio effects";
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
        Column.create(
          props -> props.crossAxisAlignment = CrossAxisAlignment.CENTER,
          Text.create(
            props -> {
              props.text = "Current Effect";
              props.fontSize = 32;
            }
          ),
          Text.create(
            props -> {
              props.text = effectNames[effectIndex];
              props.fontSize = 24;
              props.fontStyle = Text.BOLD;
              props.alignment = Alignment.CENTER;
            }
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
