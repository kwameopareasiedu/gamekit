# Audio Clip

_[Back To Audio](overview.md)_

The abstract `AudioClip` class handles the loading of audio files from the resources directory and has methods to
control playback.

## Preloading Audio Clips

Audio must be preloaded into memory before it can be used in your scene. This is handled by the static `Audio` utility.
You preload an audio file as [non-spatial (2D)](#non-spatial-audio) or [spatial (3D)](#spatial-audio).

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
Audio.preload(
  "main-bg-music", 
  new AudioClip2D(
    "path-to-audio-resource", 
    AudioGroup.MUSIC, 
    1
  )
);
```

### Spatial Audio

Spatial (3D) audio refers to positional sound whose output through stereo speakers is panned with respect to the
[AudioListener](audio-listener.md) instance . This means, if the audio is placed to the right of the listener, it will
be heard more from the right speaker than the left and vice versa.

Spatial audio is great for sound-emitting objects in your game world, which are panned relative to the player (E.g.
explosions, gunshots, waterfall).

Using this audio in-game is a bit more involved. Preload the audio file into an `AudioClip3D`, specifying the
group, max volume, [attenuation function](attenuation.md) and [audio shape](audio-shape.md), then start/stop as needed
in-game.

```java
// Preload 3D audio clip
Audio.preload(
  "bomb-explosion",
  new AudioClip3D(
    "path-to-audio-resource", 
    AudioGroup.EFFECTS, 
    1, 
    new LinearAudioAttenuation(), 
    new CircleAudioShape(10, 75)
  )
);
```

## Public Methods

| Method   | Description                                                                                                               |
|----------|---------------------------------------------------------------------------------------------------------------------------|
| `play`   | `+1` Begins playback from the beginning without looping<br/>`+2` Begins playback from the beginning with optional looping |
| `pause`  | Stops playback without resetting the clip                                                                                 |
| `resume` | Resumes playback from paused position                                                                                     |
| `stop`   | Stops playback, resetting the clip to the beginning                                                                       |

_`AudioClip3D` has these methods in addition to the common public methods_.

| Method        | Description                                  |
|---------------|----------------------------------------------|
| `getPosition` | Returns the position vector                  |
| `setPosition` | Sets the position of the clip in world-space |
