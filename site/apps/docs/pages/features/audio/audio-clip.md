# Audio Clip

_[Back To Audio](overview.md)_

`AudioClip` class stores 16-bit, dual channel audio data retrieved from a resource file, and provides methods to control
playback.

## Loading Audio Clips

Use the static `Audio` utility to load a clip from file into and `AudioClip`.

```java
try {
  AudioClip clip = Audio.loadClip("path-to-audio-resource");
  AudioClip clip2 = Audio.loadClip("path-to-audio-resource", "<mixer-id>");
} catch (UnsupportedAudioFileException | IOException e) {
  // Handle error
}
```

> Audio clips are cached, so loading the same clip does not perform additional disk operations.

## Spatialization

`AudioClip` supports spatialization, which is the ability to appear to originate from a specific location in your game
world. This can be enabled or disabled using the `setSpatial` method on the clip.

### Non-Spatial Audio

Non-spatial (2D) audio, plays back at the same volume through both speakers and does not pan (I.e. it sounds the same
through both speakers). This is the type of audio used for background music, UI sound effects and narration voice-overs.

### Spatial Audio

Spatial (3D) audio refers to directional audio whose output through stereo speakers depends on the position of the
[AudioListener](audio-listener.md) instance (I.e. panning).

This means, if the audio is positioned to the right of the listener, it will be heard more from the right speaker than
the left and vice versa.

Spatial audio is ideal for sound-emitting objects in your game world, which are panned relative to the player (E.g.
explosions, gunshots, waterfall).

## Attaching To A Mixer

[Audio mixers](audio-mixer.md) allow for group control of multiple audio clips. To add this clip to a mixer, specify the
mixer id when loading the clip, or use the `setMixer` method.

```java
// Specify the mixer when loading the clip
try {
  AudioClip clip = Audio.loadClip("path-to-audio-resource", "<mixer-id>");
} catch (UnsupportedAudioFileException | IOException e) {
  e.printStackTrace();
}

/* or */

// Set/Update the mixer on the clip instance
clip.setMixer(Audio.getMixer("<mixer-id>"));
```

## Public Methods

| Method             | Description                                                                          |
|--------------------|--------------------------------------------------------------------------------------|
| `isPlaying`        | Returns whether the clip is playing                                                  |
| `play`             | Begins playback from the beginning                                                   |
| `pause`            | Pauses playback without resetting the head                                           |
| `stop`             | Stops playback and resets the head                                                   |
| `isLooping`        | Returns whether the clip is set to loop                                              |
| `setLooping`       | Sets the clip to loop playback or not                                                |
| `getVolume`        | Returns the volume of this clip                                                      |
| `setVolume`        | Sets the volume of the clip (Clamped between 0 and 1.5)                              |
| `isMuted`          | Returns whether the clip is muted                                                    |
| `setMuted`         | Mutes or unmutes the clip                                                            |
| `isSpatial`        | Returns whether the clip is spatial                                                  |
| `setSpatial`       | Sets whether this clip is spatial                                                    |
| `getMixer`         | Returns mixer to which this clip is attached                                         |
| `setMixer`         | Sets the audio mixer of this clip                                                    |
| `getPosition`      | Returns the position of this clip (Relevant to spatial clips only)                   |
| `setPosition`      | Sets the position of this clip (Relevant to spatial clips only)                      |
| `setAttenuation`   | Sets the attenuation/falloff behaviour of this clip (Relevant to spatial clips only) |
| `setEventListener` | Adds a listener to this clip to be notified of playback events                       |

> All setter methods return the clip instance, allowing for method chaining
