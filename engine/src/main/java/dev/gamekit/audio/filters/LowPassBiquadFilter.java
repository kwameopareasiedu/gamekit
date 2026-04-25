package dev.gamekit.audio.filters;

import dev.gamekit.utils.GMath;

/**
 * {@link LowPassBiquadFilter} implements the
 * <a href="https://en.wikipedia.org/wiki/Digital_biquad_filter">Digital biquad filter</a>
 * for low pass frequency filtering
 */
public class LowPassBiquadFilter implements AudioFilter {
  private final double b0, b1, b2, a1, a2;
  private final double[] out, out1, out2;
  private double l1, l2, r1, r2;

  /** Creates a new {@link LowPassBiquadFilter} filter with the {@code cutoff} frequency in Hz and resonance factor */
  public LowPassBiquadFilter(double cutoff, double resonance) {
    double omega = (GMath.TWO_PI * cutoff / 44100);
    double alpha = Math.sin(omega) / (2 * resonance);
    double cs = Math.cos(omega);

    double a0 = 1 + alpha;
    b0 = ((1 - cs) / 2) / a0;
    b1 = (1 - cs) / a0;
    b2 = ((1 - cs) / 2) / a0;
    a1 = (-2 * cs) / a0;
    a2 = (1 - alpha) / a0;
    l1 = r1 = l2 = r2 = 0;

    out = new double[]{ 0, 0 };
    out1 = new double[]{ 0, 0 };
    out2 = new double[]{ 0, 0 };
  }

  /**
   * Creates a new {@link LowPassBiquadFilter} filter with the {@code cutoff} frequency in Hz and a resonance factor
   * of 1
   */
  public LowPassBiquadFilter(double cutoff) {
    this(cutoff, 1);
  }

  @Override
  public double[] process(double l0, double r0) {
    out[0] = b0 * l0 + b1 * l1 + b2 * l2 - a1 * out1[0] - a2 * out2[0];
    out[1] = b0 * r0 + b1 * r1 + b2 * r2 - a1 * out1[1] - a2 * out2[1];

    l2 = l1;
    l1 = l0;

    r2 = r1;
    r1 = r0;

    out2[0] = out1[0];
    out1[0] = out[0];

    out2[1] = out1[1];
    out1[1] = out[1];
    return out;
  }
}
