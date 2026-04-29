package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;

/**
 * {@link AllPassFilter} is an audio filter which allows all frequencies of an audio signal to pass, but affects the
 * phase relationship between frequencies
 */
public class AllPassFilter extends AudioEffect {
  private final DelayFilter delayFilter;
  private final CombFilter combFilter;
  private final double damping;
  private final double delayedDamping;

  public AllPassFilter(double delayMs, double damping) {
    super((int) (0.001 * delayMs * Audio.SAMPLE_RATE));

    this.delayFilter = new DelayFilter(delayMs);
    this.combFilter = new CombFilter(delayMs, damping);
    this.damping = damping;
    this.delayedDamping = 1 - Math.pow(damping, 2);
  }

  @Override
  public void performProcess(double[] out, double sampleL, double sampleR) {
    double[] combSample = combFilter.process(sampleL, sampleR);
    double[] delayedSample = delayFilter.process(combSample[0], combSample[1]);

    out[0] = bufferL[head] = -damping * sampleL + delayedDamping * delayedSample[0];
    out[1] = bufferR[head] = -damping * sampleR + delayedDamping * delayedSample[1];
  }
}
