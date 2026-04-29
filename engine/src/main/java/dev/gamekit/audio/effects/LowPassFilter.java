package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;
import dev.gamekit.utils.GMath;

/**
 * {@link LowPassFilter} is an audio filter which allows frequencies of an audio signal below a certain cutoff to
 * pass unaffected while heavily attenuating higher frequencies.
 */
public class LowPassFilter extends AudioEffect {
  private final double b0, b1, b2, a1, a2;

  /** Creates a new {@link LowPassFilter} filter with the {@code cutoff} frequency in Hz and resonance factor */
  public LowPassFilter(double cutoff, double resonance) {
    super(new double[4], new double[4]);
    double omega = (GMath.TWO_PI * cutoff / Audio.SAMPLE_RATE);
    double alpha = Math.sin(omega) / (2 * resonance);
    double cs = Math.cos(omega);

    double a0 = 1 + alpha;
    b0 = ((1 - cs) / 2) / a0;
    b1 = (1 - cs) / a0;
    b2 = ((1 - cs) / 2) / a0;
    a1 = (-2 * cs) / a0;
    a2 = (1 - alpha) / a0;
  }

  /** Creates a new {@link LowPassFilter} filter with the {@code cutoff} frequency in Hz and a resonance factor of 1 */
  public LowPassFilter(double cutoff) {
    this(cutoff, 1);
  }

  @Override
  public void performProcess(double sampleL, double sampleR) {
    out[0] = b0 * sampleL + b1 * inputSamples[0] + b2 * inputSamples[2] - a1 * outputSamples[0] - a2 * outputSamples[2];
    out[1] = b0 * sampleR + b1 * inputSamples[1] + b2 * inputSamples[3] - a1 * outputSamples[1] - a2 * outputSamples[3];
  }
}
