package dev.gamekit.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.HashMap;

/** Audio handles loading and playback of sounds in GameKit */
public class Audio {
  private static final Logger LOGGER = LogManager.getLogger();
  private static final HashMap<Object, Clip> CLIP_CACHE = new HashMap<>();

  private Audio() { }

  /**
   * Loads the entire audio resource file at {@code resPath} into memory to
   * be played back later
   */
  public static void preload(Object key, String resPath) {
    Clip clip = getResourceAudioClip(resPath);

    if (clip == null) {
      LOGGER.warn("Audio with key {} not found at \"{}\"", key, resPath);
      return;
    }

    CLIP_CACHE.put(key, clip);
  }

  /** Starts the playback of a clip with the specified key */
  public static void play(Object key) {
    if (!CLIP_CACHE.containsKey(key)) {
      LOGGER.warn("Audio with key {} not preloaded", key);
      return;
    }

    Clip clip = CLIP_CACHE.get(key);

    stop(key);
    clip.setMicrosecondPosition(0);
    clip.start();
  }

  /** Stops the playback of a clip with the specified key */
  public static void stop(Object key) {
    if (!CLIP_CACHE.containsKey(key)) {
      LOGGER.warn("Audio with key {} not preloaded", key);
      return;
    }

    Clip clip = CLIP_CACHE.get(key);
    clip.stop();
    clip.flush();
  }

  /** Sets the number of times a clip with the specified key should loop */
  public static void loop(Object key, int loopCount) {
    if (!CLIP_CACHE.containsKey(key)) {
      LOGGER.warn("Audio with key {} not preloaded", key);
      return;
    }

    Clip clip = CLIP_CACHE.get(key);
    clip.loop(loopCount);
  }

  /**
   * Sets the gain in decibels a clip with the specified key.
   * <p>
   * This should be called prior to calling {@link #play(Object)}
   */
  public static void setGain(Object key, float gainDb) {
    setControl(key, FloatControl.Type.MASTER_GAIN, gainDb);
  }

  /** Sets the volume of a clip with the specified key */
  public static void setVolume(Object key, float volume) {
    setControl(key, FloatControl.Type.VOLUME, volume);
  }

  /**
   * Sets the left-to-right positioning of a stereo clip with the specified
   * key.
   * <p>
   * A value of {@code -1} plays only the left channel of the clip (i.e.
   * plays on the left speaker) and a value of {@code 1} plays only the right
   * channel.
   */
  public static void setPan(Object key, float pan) {
    setControl(key, FloatControl.Type.PAN, pan);
  }

  private static void setControl(Object key,
                                 FloatControl.Type controlType, float value) {
    if (!CLIP_CACHE.containsKey(key)) {
      LOGGER.warn("Audio with key {} not preloaded", key);
      return;
    }

    Clip clip = CLIP_CACHE.get(key);

    if (!clip.isControlSupported(controlType)) {
      LOGGER.warn("Clip does not support control type: {}", controlType);
      return;
    }

    FloatControl control =
      (FloatControl) clip.getControl(controlType);
    control.setValue(value);
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
