package dev.gamekit.core;

import dev.gamekit.audio.AudioClip;
import dev.gamekit.audio.AudioGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.HashMap;

/** Audio handles loading and playback of sounds in GameKit */
public class Audio {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final HashMap<Object, AudioClip> CLIP_CACHE = new HashMap<>();

  private Audio() { }

  /**
   * Loads a non-spatial audio clip
   * @see #load(Object, String, AudioGroup, double)
   */
  public static void load(
    Object key,
    String resPath,
    AudioGroup group,
    double maxVolume
  ) {
    load(key, resPath, group, maxVolume, false);
  }

  /**
   * Loads an audio clip resource at {@code resPath} into memory. This creates
   * and caches a {@link AudioClip} object
   * @param key       A unique identifier for the loaded clip
   * @param resPath   The path to the clip in the resource folder
   * @param group     The group which this clip belongs to
   * @param maxVolume The max volume of this clip (0.0 - 1.0)
   * @param spatial   Whether this clip is spatial or non-spatial
   */
  public static void load(
    Object key,
    String resPath,
    AudioGroup group,
    double maxVolume,
    boolean spatial
  ) {
    Clip clip = getResourceAudioClip(resPath);

    if (clip != null) {
      AudioClip clipHolder = new AudioClip(clip, group, maxVolume, spatial);
      CLIP_CACHE.put(key, clipHolder);
    } else LOGGER.warn("Clip not found at \"{}\"", resPath);
  }

  public static void play(Object key) {
    if (!CLIP_CACHE.containsKey(key)) {
      LOGGER.warn("Clip with key {} not loaded", key);
    } else CLIP_CACHE.get(key).play();
  }

  public static void play(Object key, boolean loop) {
    if (!CLIP_CACHE.containsKey(key)) {
      LOGGER.warn("Audio with key {} not loaded", key);
    } else CLIP_CACHE.get(key).play(loop);
  }

  public static void resume(Object key) {
    if (!CLIP_CACHE.containsKey(key)) {
      LOGGER.warn("Audio with key {} not loaded", key);
    } else CLIP_CACHE.get(key).resume();
  }

  public static void pause(Object key) {
    if (!CLIP_CACHE.containsKey(key)) {
      LOGGER.warn("Audio with key {} not loaded", key);
    } else CLIP_CACHE.get(key).pause();
  }

  public static void stop(Object key) {
    if (!CLIP_CACHE.containsKey(key)) {
      LOGGER.warn("Clip with key {} not loaded", key);
    } else CLIP_CACHE.get(key).stop();
  }

  /** Called internally to perform update logic */
  static void update() {
    CLIP_CACHE.forEach((key, clip) -> clip.update());
  }

  private static Clip getResourceAudioClip(String resPath) {
    try {
      LOGGER.debug("Loading audio clip at {}", resPath);

      Clip clip = AudioSystem.getClip();
      clip.open(AudioSystem.getAudioInputStream(IO.getResourceStream(resPath)));
      return clip;
    } catch (LineUnavailableException e) {
      LOGGER.error("Could not get audio clip resource from system mixer", e);
      return null;
    } catch (UnsupportedAudioFileException e) {
      LOGGER.error("Unsupported audio file format", e);
      return null;
    } catch (IOException e) {
      LOGGER.error("Unable to load resource audio clip at {}", resPath);
      LOGGER.catching(e);
      return null;
    }
  }
}
