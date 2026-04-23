package dev.gamekit.audio;

import java.util.ArrayList;
import java.util.List;

public class AudioBus {
  public static final String DEFAULT_ID = "default";

  final Object id;
  private final List<AudioClip> clips;

  public AudioBus(Object id) {
    this.id = id;
    clips = new ArrayList<>();
  }

  void addClip(AudioClip clip) {
    if (clips.contains(clip))
      return;

    clips.add(clip);
  }

  public void dispose() {

  }
}
