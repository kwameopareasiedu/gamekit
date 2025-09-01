# Audio

Sound is crucial in creating games. From background music to environment ambience to sound effects, audio in a game can
make or break your user immersion.

GameKit's audio system allows you to import audio and manipulate them in-game to fit your desired outcomes.

Before proceeding, here are a few points to take note of:

> - GameKit does not support streaming audio from disk/network
> - GameKit only support the wave format (**.wav**)

## Preloading Audio Clips

Before audio can be played in your scene/entity, you first need to preload it using the static `Audio` class. This loads
the file from the resources directory into memory with a unique key. Your application will throw an exception if an
existing key is reused.

The sample below illustrates preloading of non-spatial and spatial audio clips:

```java
import dev.gamekit.audio.AudioAttenuation;
import dev.gamekit.audio.AudioClip2D;
import dev.gamekit.audio.AudioClip3D;
import dev.gamekit.audio.AudioGroup;
import dev.gamekit.audio.shapes.AudioShape;
import dev.gamekit.audio.shapes.AudioShapeCircle;
import dev.gamekit.core.Application;
import dev.gamekit.core.Scene;

public class AudioSample extends Scene {
  private static final String BG_MUSIC_KEY = "music";
  private static final String EXPLOSION_SFX_KEY = "explosion";
  
  private boolean playing = false;

  public AudioSample() {
    super("Main Scene");
    
    // Non-spatial (2D) audio
    Audio.preload(
      BG_MUSIC_KEY,
      new AudioClip2D("bg-music.wav", AudioGroup.MUSIC, 1)
    );
    
    // Spatial (3D) audio
    Audio.preload(
      EXPLOSION_SFX_KEY,
      new AudioClip3D(
        "explosion.wav", AudioGroup.EFFECTS, 1,
        AudioAttenuation.LINEAR, new AudioShapeCircle(5, 30)
      )
    );
  }

  public static void main(String[] args) {
    Application game = new Application("Audio Sample") { };
    game.loadScene(new AudioSample());
    game.run();
  }
}
```

## The Audio Clip

The abstract `AudioClip` class handles the loading of audio files from the resources directory and has methods to
control playback.

We extend the code sample above illustrating the playback methods (Some code is omitted for brevity)

```java
/** Omitted imports */

public class AudioSample extends Scene {
  private static final String BG_MUSIC_KEY = "music";
  private static final String EXPLOSION_SFX_KEY = "explosion";
  
  /** Omitted code */
  
  @Override
  protected void update() {
    Audio.get(BG_MUSIC_KEY).play()        // Start playback from the beginning of the audio (No looping)
    Audio.get(BG_MUSIC_KEY).play(true)    // Start playback with looping
    Audio.get(BG_MUSIC_KEY).pause()       // Pause playback
    Audio.get(BG_MUSIC_KEY).resume()      // Resume playback from previously paused position
    Audio.get(BG_MUSIC_KEY).stop()        // Stop playback and rewind to the beginning
  }
}
```

## Spatial vs Non-Spatial

GameKit's audio is grouped into two categories:

- Non-spatial audio
- Spatial audio

### Non-Spatial Audio

Non-spatial (2D) audio, plays back at the same volume through both stereo speakers and is not panned. This is ideal for
background music, UI sound effects and narration voice-overs.

In GameKit, non-spatial audio is represented by the `AudioClip2D` class which extends the abstract `AudioClip`.

The snippet below creates a new non-spatial audio clip from the _test.wav_ resource file with the `AudioGroup.MUSIC`
group (more on this later) and a max volume of 75%.

```java
new AudioClip2D("test.wav", AudioGroup.MUSIC, 0.75);
```

### Spatial Audio

Spatial (3D) audio refers to positional sound whose output through stereo speakers is panned with respect to the
`AudioListener` instance. This means, if the audio is placed to the right of the listener, it will be heard more from
the right speaker than the left and vice versa.

Spatial audio is great for sound-emitting objects in your game world, which are panned relative to the player (E.g.
explosions, characters, etc.)

The snippet below creates a new spatial audio clip from the _test.wav_ resource file with the `AudioGroup.EFFECTS`
group, a max volume of 100%, a linear attenuation and a circular shape with min and max distances of 5 and 30
respectively.

```java
new AudioClip2D("test.wav", AudioGroup.EFFECTS, 1, AudioAttenuation.LINEAR, new AudioShapeCircle(5, 30));
```

## Audio Groups

## Attenuation

## Audio Shapes
