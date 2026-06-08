package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;
import dev.gamekit.utils.GMath;

import java.util.UUID;

/** {@link CombFilter} is an audio filter which adds delayed version of an audio signal back unto itself */
public class CombFilter extends AudioEffect {
  private double damping;

  public CombFilter(String id, double delayMs, double damping) {
    super(id, (int) (0.001 * delayMs * Audio.SAMPLE_RATE));
    this.damping = GMath.clamp(damping, 0, 1);
  }

  public CombFilter(double delayMs, double damping) {
    this(UUID.randomUUID().toString(), delayMs, damping);
  }

  /** Sets the damping factor of this filter */
  public void setDamping(double damping) {
    synchronized (this) {
      this.damping = GMath.clamp(damping, 0, 1);
    }
  }

  @Override
  public void performProcess(double[] out, double sampleL, double sampleR) {
    synchronized (this) {
      int delayHead = getWrappedIndex(head + 1);
      double delayedOutputL = bufferL[delayHead];
      double delayedOutputR = bufferR[delayHead];

      out[0] = bufferL[head] = sampleL + damping * delayedOutputL;
      out[1] = bufferR[head] = sampleR + damping * delayedOutputR;
    }
  }
}
