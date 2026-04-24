package dev.gamekit.audio;

import static dev.gamekit.utils.GMath.clamp;

/**
 * {@link AudioClip2D} is a {@link AudioClip} which plays back consistently at the same volume and pan regardless of
 * the audio listener's position. It's best suited for ambient sounds, menu music and non-diegetic audio.
 */
public class AudioClip2D extends AudioClip {
  // Cached effective volume since log10() is expensive to compute every frame
  private double effectiveVolume = -1;

  public AudioClip2D(String resPath, AudioGroup group, double maxVolume) {
    super(null, null, false);

//    performUpdate();
  }

//  @Override
//  public void performUpdate() {
//    double effectiveVolume = !group.isMuted() ?
//      group.getVolume() * maxVolume : 0;
//
//    if (this.effectiveVolume != effectiveVolume) {
//      if (gainControl != null) {
//        double gain = 20 * Math.log10(effectiveVolume);
//        gain = clamp(gain, gainControl.getMinimum(), gainControl.getMaximum());
//        gainControl.setValue((float) gain);
//      }
//
//      this.effectiveVolume = effectiveVolume;
//    }
//  }
}
