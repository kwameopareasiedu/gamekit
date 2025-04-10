package dev.gamekit.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;

import static dev.gamekit.utils.Math.clamp;

/**
 * {@code AudioClip} stores and handles playback for audio content.
 * <p>
 * All {@code AudioClip} must belong to an {@link AudioGroup}. This makes it
 * possible to control multiple {@code AudioClip} by adjusting the group's
 * properties.
 */
public final class AudioClip {
  private final Clip clip;
  private final AudioGroup group;
  private final double maxVolume;
  private final boolean spatial;
  private final FloatControl gainControl;
  private final FloatControl panControl;

  // Caches the effective volume since log10() is an expensive operation to
  // be performing every frame
  private double effectiveVolume = -1;

  public AudioClip(Clip clip, AudioGroup group, double maxVolume) {
    this(clip, group, maxVolume, false);
  }

  public AudioClip(
    Clip clip,
    AudioGroup group,
    double maxVolume,
    boolean spatial
  ) {
    this.clip = clip;
    this.group = group;
    this.maxVolume = clamp(maxVolume, 0, 1);
    this.spatial = spatial;

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
  public void update() {
    if (!spatial) {
      double effectiveVolume = !group.isMuted() ?
        maxVolume * group.getMaxVolume() : 0;

      if (gainControl != null && this.effectiveVolume != effectiveVolume) {
        double gain = 20 * Math.log10(effectiveVolume);
        gain = clamp(gain, gainControl.getMinimum(), gainControl.getMaximum());
        gainControl.setValue((float) gain);
        this.effectiveVolume = effectiveVolume;
      }

      if (panControl != null && panControl.getValue() != 0f)
        panControl.setValue(0);
    } else {
      // TODO: Compute spatial volume and pan
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Control> T getControl(T.Type controlType) {
    if (clip != null && clip.isControlSupported(controlType))
      return (T) clip.getControl(controlType);
    return null;
  }
}
