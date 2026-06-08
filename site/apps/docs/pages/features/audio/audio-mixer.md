# Audio Mixer

_[Back To Audio](overview.md)_

Audio mixers are the channels through which [audio clip](audio-clip.md) data is sent to the speakers. By routing audio
clips through mixers, you can effectively control multiple audio clips properties using the mixer controls.

With mixers, you can perform the following:

- Control the volume/gain of multiple audio clips
- Mute/Unmute multiple audio clips
- Apply audio filters to multiple audio clips

## Obtain A Mixer

Use the static `Audio` utility to create or get a mixer.

```java
AudioMixer newMixer = Audio.createMixer("<mixer-id>");    // Create a new mixer
AudioMixer existingMixer = Audio.getMixer("<mixer-id>");  // Obtain an existing mixer
```

## Mixer Audio Effects

You can modify the output of mixers by applying [audio effects](audio-effect.md). These can filter the frequency ranges
of the sound or apply reverb and other effects.

Use the `addEffects` method to add one or more audio effect to a mixer.

```java
Audio.getMixer("<mixer-id>").addEffects(
  new LowPassFilter(1500),
  new ReverbFilter(10, 0.5, 0.9)
);
```

## Public Methods

| Method       | Description                                              |
|--------------|----------------------------------------------------------|
| `addEffects` | Adds one or more effects to this mixer                   |
| `getVolume`  | Returns the volume of this mixer                         |
| `setVolume`  | Sets the volume of the mixer (Clamped between 0 and 1.5) |
| `isMuted`    | Returns whether the mixer is muted                       |
| `setMuted`   | Mutes or unmutes the mixer                               |
