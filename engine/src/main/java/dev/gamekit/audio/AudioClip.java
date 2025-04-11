package dev.gamekit.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;

import static dev.gamekit.utils.Math.clamp;

/**
 * Abstract class which stores and handles playback for audio content.
 * <p>
 * All {@link AudioClip} must belong to an {@link AudioGroup}. This makes it
 * possible to control multiple {@link AudioClip} by adjusting the group's
 * properties.
 */
public abstract class AudioClip {
  protected final Clip clip;
  protected final AudioGroup group;
  protected final double maxVolume;
  protected final FloatControl gainControl;
  protected final FloatControl panControl;

  public AudioClip(Clip clip, AudioGroup group, double maxVolume) {
    this.clip = clip;
    this.group = group;
    this.maxVolume = clamp(maxVolume, 0, 1);

    gainControl = getControl(FloatControl.Type.MASTER_GAIN);
    panControl = getControl(FloatControl.Type.PAN);
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
  public abstract void update();

  @SuppressWarnings("unchecked")
  protected <T extends Control> T getControl(T.Type controlType) {
    if (clip != null && clip.isControlSupported(controlType))
      return (T) clip.getControl(controlType);
    return null;
  }
}
