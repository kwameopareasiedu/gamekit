# Audio Clip

_[Back To Audio](overview.md)_

The abstract `AudioClip` class handles the loading of audio files from the resources directory and has methods to
control playback.

## Preloading Audio Clips

Audio must be preloaded into memory before it can be used in your scene. This is handled by the static `Audio` utility.
You preload an audio file as non-spatial (2D) or spatial (3D) (_More on this later_).

```java
Audio.preload(<Unique string key>, new AudioClip2D("path-to-audio-resource", <Audio Group>, <Max Volume>));
// or
Audio.preload(<Unique string key>, new AudioClip3D("path-to-audio-resource", <Audio Group>, <Max Volume>, <Attenutation Function>, <Audio Shape>));
```

## Spatial vs Non-Spatial

You may have noticed that we used two different audio clip classes in the samples so far which are `AudioClip2D` and
`AudioClip3D`. This is because GameKit supports two different kinds of audio. These are:

- Non-spatial or 2D audio
- Spatial or 3D audio

### Non-Spatial Audio

Non-spatial (2D) audio, plays back at the same volume through both speakers and does not pan (I.e. it sounds the same
through both speakers). This is the type of audio used for background music, UI sound effects and narration voice-overs.

Using this audio in-game is relatively straightforward. Preload the audio file into an `AudioClip2D`, specifying the
group and max volume, start/stop as needed in-game.

```java
// Preload 2D audio clip
Audio.preload("main-bg-music", new AudioClip2D("path-to-audio-resource", AudioGroup.MUSIC, 1));

// Control playback
Audio.get("main-bg-music").play();      // One-shot playback
Audio.get("main-bg-music").play(true);  // Loop playback
Audio.get("main-bg-music").pause();     // Pause playback
Audio.get("main-bg-music").resume();    // Resume playback
Audio.get("main-bg-music").stop();      // Stop and reset playback
```
