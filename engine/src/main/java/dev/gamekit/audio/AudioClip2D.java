package dev.gamekit.audio;

import static dev.gamekit.utils.Math.clamp;

/**
 * {@code AudioClip2D} is a {@link AudioClip} which updates its audio
 * parameters without spatialization.
 * <p>
 * {@link AudioClip2D} sound the same as if you played it through a media player
 */
public class AudioClip2D extends AudioClip {
  // Cached effective volume since log10() is expensive to compute every frame
  private double effectiveVolume = -1;

  public AudioClip2D(String resPath, AudioGroup group, double maxVolume) {
    super(resPath, group, maxVolume);
  }

  @Override
  public void performUpdate() {
    double effectiveVolume = !group.isMuted() ?
      group.getMaxVolume() * maxVolume : 0;

    if (this.effectiveVolume != effectiveVolume) {
      if (gainControl != null) {
        double gain = 20 * Math.log10(effectiveVolume);
        gain = clamp(gain, gainControl.getMinimum(), gainControl.getMaximum());
        gainControl.setValue((float) gain);
      }

      this.effectiveVolume = effectiveVolume;
    }
  }
}
