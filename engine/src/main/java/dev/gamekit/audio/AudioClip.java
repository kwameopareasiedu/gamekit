package dev.gamekit.audio;

import dev.gamekit.core.IO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.IOException;

import static dev.gamekit.utils.Math.clamp;

/**
 * {@link AudioClip} is abstract class which stores and handles playback for audio in GameKit.
 * <p>
 * All {@link AudioClip} must belong to an {@link AudioGroup}. This makes it possible to control
 * the properties of all audio clips in specific groups by adjusting the group's properties.
 */
public abstract class AudioClip {
  protected final Logger logger = LogManager.getLogger(getClass());
  protected final Clip clip;
  protected final AudioGroup group;
  protected final double maxVolume;
  protected final FloatControl gainControl;

  public AudioClip(String resPath, AudioGroup group, double maxVolume) {
    this.clip = loadClip(resPath);
    this.group = group;
    this.maxVolume = clamp(maxVolume, 0, 1);

    gainControl = getControl(FloatControl.Type.MASTER_GAIN);
  }

  public void play() { play(false); }

  public void play(boolean loop) {
    stop();

    if (loop) {
      clip.loop(Clip.LOOP_CONTINUOUSLY);
    } else clip.start();
  }

  public void resume() { clip.start(); }

  public void pause() { clip.stop(); }

  public void stop() {
    clip.stop();
    clip.flush();
    clip.setMicrosecondPosition(0);
  }

  /** Called internally to update the clip's parameters */
  public final void update() {
    if (clip.isRunning())
      performUpdate();
  }

  /** Delegate method which performs the actual update and must be overridden in subclasses */
  public abstract void performUpdate();

  @SuppressWarnings("unchecked")
  protected <T extends Control> T getControl(T.Type controlType) {
    if (clip != null && clip.isControlSupported(controlType))
      return (T) clip.getControl(controlType);
    return null;
  }

  private Clip loadClip(String resPath) {
    try {
      logger.debug("Loading audio clip at {}", resPath);

      Clip clip = AudioSystem.getClip();
      clip.open(AudioSystem.getAudioInputStream(IO.getResourceStream(resPath)));
      return clip;
    } catch (LineUnavailableException e) {
      logger.error("Could not get audio clip resource from system mixer", e);
      throw new RuntimeException(e);
    } catch (UnsupportedAudioFileException e) {
      logger.error("Unsupported audio file format", e);
      throw new RuntimeException(e);
    } catch (IOException e) {
      logger.error("Unable to load resource audio clip at {}", resPath);
      throw new RuntimeException(e);
    }
  }
}
