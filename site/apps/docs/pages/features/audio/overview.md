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
> - GameKit only supports the wave format (**.wav**)

## A Simple Sample

```java
import dev.gamekit.audio.AudioClip;
import dev.gamekit.audio.AudioGroup;
import dev.gamekit.core.Application;
import dev.gamekit.core.Audio;
import dev.gamekit.core.Input;
import dev.gamekit.core.Scene;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class AudioSample extends Scene {
  private AudioClip clip;

  public AudioSample() {
    super("Main Scene");

    try{
      clip = Audio.loadClip("bg-music.wav");
    } catch (UnsupportedAudioFileException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    Application game = new Application("Audio Sample") { };
    game.loadScene(new AudioSample());
    game.run();
  }
  
  @Override
  protected void update() {
    if (Input.isKeyDown(Input.KEY_SPACE)) {
      if (!clip.isPlaying()) {
        clip.play();
      } else {
        clip.stop();
      }
    }
  }
}
```

### What we have done

- We use the `Audio` utility to load an audio resource file named "bg-music.wav".
- In the `update` lifecycle method, we start/stop playback of the audio clip when the space bar is pressed.
- In the static `main` method, we created an `Application` instance with title, _"Audio Sample"_, loaded an instance of
  our scene subclass and called the `run` method to start the application.

---

GameKit audio is a bit of a broad topic and has been categorized in the sections below:

- [Audio Clip](audio-clip.md)
- [Audio Mixer](audio-mixer.md)
- [Audio Effect](audio-effect.md)
- [Audio Listener](audio-listener.md)
- [Attenuation](attenuation.md)

<configure hideToc contentWidth="center"></configure>
