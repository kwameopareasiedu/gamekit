package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;

/** {@link CombFilter} is an audio filter which adds delayed version of an audio signal back unto itself */
public class CombFilter extends AudioEffect {
  private final double delayMs;
  private final double decayFactor;

  public CombFilter(double delayMs, double decayFactor) {
    super(
      new double[2 * (int) (0.001 * delayMs * Audio.SAMPLE_RATE)],
      new double[2 * (int) (0.001 * delayMs * Audio.SAMPLE_RATE)]
    );

    this.delayMs = delayMs;
    this.decayFactor = decayFactor;
  }

  @Override
  public void performProcess(double sampleL, double sampleR) {
    double[] delayedOutput = getDelayedOutputSamples(delayMs);

    out[0] = sampleL + decayFactor * delayedOutput[0];
    out[1] = sampleR + decayFactor * delayedOutput[1];
  }
}
