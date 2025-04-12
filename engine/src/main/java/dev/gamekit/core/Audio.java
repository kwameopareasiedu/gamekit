package dev.gamekit.core;

import dev.gamekit.audio.AudioClip;

import java.util.HashMap;

/** Audio handles loading and playback of sounds in GameKit */
public class Audio {
  private static final HashMap<Object, AudioClip> CLIP_CACHE = new HashMap<>();

  private Audio() { }

  /** Loads an {@link AudioClip} with its unique key */
  public static void preload(
    Object key,
    AudioClip audioClip
  ) {
    if (audioClip == null)
      throw new NullPointerException("Audio clip cannot be null");

    if (CLIP_CACHE.containsKey(key))
      throw new RuntimeException(
        String.format("Audio clip with key: %s already exists", key)
      );

    CLIP_CACHE.put(key, audioClip);
  }

  @SuppressWarnings("unchecked")
  public static <T extends AudioClip> T get(Object key) {
    if (!CLIP_CACHE.containsKey(key))
      throw new RuntimeException(
        String.format("Audio clip with key: %s does not exist", key)
      );

    return (T) CLIP_CACHE.get(key);
  }

  /** Called internally to perform update logic */
  static void update() {
    CLIP_CACHE.forEach((key, clip) -> clip.update());
  }
}
