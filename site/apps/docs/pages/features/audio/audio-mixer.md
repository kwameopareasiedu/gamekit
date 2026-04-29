# Audio Group

_[Back To Audio](overview.md)_

Audio groups are the answer to the question **_"How do I control the properties of multiple audio clips at once?"_**.
By requiring an audio group on every audio clip, the volume and muted status of all clips in the group can be
controlled at the same time.

The most practical example is implementation of a sound settings screen within your game.

GameKit ships with two (2) default audio groups:

- `AudioGroup.MUSIC`
- `AudioGroup.EFFECTS`

However, you can create more by extending the `AudioGroup` class as shown in the sample below:

```java
import dev.gamekit.audio.AudioGroup;

/** An audio group called "Narration" with an initial max volume of 50% */
public class NarrationAudioGroup extends AudioGroup {
  public static final NarrationAudioGroup INSTANCE = new NarrationAudioGroup();

  private NarrationAudioGroup() {
    super("Narration", 0.5);
  }
}

```

The new audio group can then be used on audio clips.

```java
Audio.preload("opening-dialog", new AudioClip2D("path-to-audio-resource", NarrationAudioGroup.INSTANCE, 1);
```

> It is important to use the same instance of your custom audio group.

## Public Methods

| Method         | Description                           |
|----------------|---------------------------------------|
| `getName`      | Returns the name of the group         |
| `isMuted`      | Returns the muted status of the group |
| `setMuted`     | Mutes/Unmutes the group               |
| `getMaxVolume` | Returns the max volume of the group   |
| `setMaxVolume` | Sets the max volume of the group      |
