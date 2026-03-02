# Audio

_[Back To Features](../overview.md)_

Sound is crucial in creating games. From background music to environment ambience to sound effects, audio in a game can
make or break your player immersion.

GameKit's audio system allows you to import audio and manipulate them in-game to fit your desired outcomes.

The general workflow for GameKit audio is as follows:

- Preload your audio file into an audio clip
- Assign the audio clip to an audio group
- Begin/Pause/Stop playback of your clip within your game

Before proceeding, here are a few points to take note of:

> - GameKit does not support streaming audio from disk/network
> - GameKit only support the wave format (**.wav**)

## A Simple Sample

```java
import dev.gamekit.audio.AudioClip2D;
import dev.gamekit.audio.AudioGroup;
import dev.gamekit.core.Application;
import dev.gamekit.core.Audio;
import dev.gamekit.core.Input;
import dev.gamekit.core.Scene;

public class AudioSample extends Scene {
  private static final String BG_MUSIC_KEY = "music";

  private boolean playing = false;

  public AudioSample() {
    super("Main Scene");

    Audio.preload(
      BG_MUSIC_KEY,
      new AudioClip2D("bg-music.wav", AudioGroup.MUSIC, 1)
    );
  }

  public static void main(String[] args) {
    Application game = new Application("Audio Sample") { };
    game.loadScene(new AudioSample());
    game.run();
  }
  
  @Override
  protected void update() {
    if (Input.isKeyDown(Input.KEY_SPACE)) {
      if (!playing) {
        Audio.get(BG_MUSIC_KEY).play();
        playing = true;
      } else {
        Audio.get(BG_MUSIC_KEY).stop();
        playing = false;
      }
    }
  }
}
```

### What we have done

- We use the `Audio` utility to preload a resource file named "bg-music.wav" with the key `BG_MUSIC_KEY` into the
  `AudioGroup.MUSIC` group with maximum volume of 100%.
- In the `update` lifecycle method, we start/stop playback of the preloaded audio clip when the space bar is pressed.
- In the static `main` method, we created an `Application` instance with title, _"Audio Sample"_, loaded an instance of
  our scene subclass and called the `run` method to start the application.

---

GameKit audio is a bit of a broad topic and has been categorized in the sections below:

- [Audio Clip](audio-clip.md)
- [Audio Group](audio-group.md)
- [Audio Listener](audio-listener.md)
- [Attenuation](attenuation.md)
- [Audio Shape](audio-shape.md)

<configure hideToc contentWidth="center"></configure>
