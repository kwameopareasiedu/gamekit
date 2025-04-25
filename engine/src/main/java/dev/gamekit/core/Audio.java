package dev.gamekit.core;

import dev.gamekit.audio.AudioClip;

import java.util.HashMap;

/** {@link Audio} handles loading and playback of sounds in GameKit */
public class Audio {
  private static final HashMap<Object, AudioClip> CLIP_CACHE = new HashMap<>();

  private Audio() { }

  /** Loads an {@link AudioClip} into memory with a unique key */
  public static void preload(Object key, AudioClip audioClip) {
    if (audioClip == null)
      throw new NullPointerException("Audio clip cannot be null");

    if (CLIP_CACHE.containsKey(key))
      throw new RuntimeException(
        String.format("Audio clip with key: %s already exists", key)
      );

    CLIP_CACHE.put(key, audioClip);
  }

  /** Gets a reference to an {@link AudioClip} with a matching key */
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

  static void dispose() {
    CLIP_CACHE.forEach((key, clip) -> clip.dispose());
    CLIP_CACHE.clear();
  }
}
