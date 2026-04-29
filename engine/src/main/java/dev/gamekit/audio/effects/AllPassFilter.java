package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;

/**
 * {@link AllPassFilter} is an audio filter which allows all frequencies of an audio signal to pass, but affects the
 * phase relationship between frequencies
 */
public class AllPassFilter extends AudioEffect {
  private final DelayFilter delayFilter;
  private final CombFilter combFilter;
  private final double decayFactor;
  private final double delayedDecayFactor;

  public AllPassFilter(double delayMs, double decayFactor) {
    super(
      new double[2 * (int) (0.001 * delayMs * Audio.SAMPLE_RATE)],
      new double[2 * (int) (0.001 * delayMs * Audio.SAMPLE_RATE)]
    );

    this.delayFilter = new DelayFilter(delayMs);
    this.combFilter = new CombFilter(delayMs, decayFactor);
    this.decayFactor = decayFactor;
    this.delayedDecayFactor = Math.pow(1 - decayFactor, 2);
  }

  @Override
  public void performProcess(double sampleL, double sampleR) {
    double[] combOutput = combFilter.process(sampleL, sampleR);
    double[] delayedCombOutput = delayFilter.process(combOutput[0], combOutput[1]);

    out[0] = -decayFactor * sampleL + delayedDecayFactor * delayedCombOutput[0];
    out[1] = -decayFactor * sampleR + delayedDecayFactor * delayedCombOutput[1];
  }
}
