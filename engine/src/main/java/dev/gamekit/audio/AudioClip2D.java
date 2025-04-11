package dev.gamekit.audio;

import javax.sound.sampled.Clip;

import static dev.gamekit.utils.Math.clamp;

/**
 * {@code AudioClip2D} is a {@link AudioClip} which updates its audio
 * parameters without spatialization.
 * <p>
 * {@link AudioClip2D} sound the same as if you played it through a media player
 */
public class AudioClip2D extends AudioClip {
  // Caches the effective volume since log10() is an expensive operation to
  // be performing every frame
  private double effectiveVolume = -1;

  public AudioClip2D(Clip clip, AudioGroup group, double maxVolume) {
    super(clip, group, maxVolume);
  }

  /** Called internally to update the clip's parameters */
  @Override
  public void update() {
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
  }
}
