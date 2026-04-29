package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;
import dev.gamekit.utils.GMath;

/**
 * {@link LowPassFilter} is an audio filter which allows frequencies of an audio signal below a certain cutoff to
 * pass unaffected while heavily attenuating higher frequencies.
 */
public class LowPassFilter extends AudioEffect {
  private final double b0, b1, b2, a1, a2;
  private double il1, il2, ir1, ir2;

  /** Creates a new {@link LowPassFilter} filter with the {@code cutoff} frequency in Hz and resonance factor */
  public LowPassFilter(double cutoff, double resonance) {
    super(3);
    double omega = (GMath.TWO_PI * cutoff / Audio.SAMPLE_RATE);
    double alpha = Math.sin(omega) / (2 * resonance);
    double cs = Math.cos(omega);

    double a0 = 1 + alpha;
    b0 = ((1 - cs) / 2) / a0;
    b1 = (1 - cs) / a0;
    b2 = ((1 - cs) / 2) / a0;
    a1 = (-2 * cs) / a0;
    a2 = (1 - alpha) / a0;
    il1 = il2 = ir1 = ir2 = 0;
  }


  /** Creates a new {@link LowPassFilter} filter with the {@code cutoff} frequency in Hz and a resonance factor of 1 */
  public LowPassFilter(double cutoff) {
    this(cutoff, 1);
  }

  @Override
  public void performProcess(double[] out, double il0, double ir0) {
    int dh1 = getWrappedIndex(head - 1);
    int dh2 = getWrappedIndex(head - 2);

    out[0] = bufferL[head] = b0 * il0 + b1 * il1 + b2 * il2 - a1 * bufferL[dh1] - a2 * bufferL[dh2];
    out[1] = bufferR[head] = b0 * ir0 + b1 * ir1 + b2 * ir2 - a1 * bufferR[dh1] - a2 * bufferR[dh2];

    il2 = il1;
    il1 = il0;

    ir2 = ir1;
    ir1 = ir0;
  }
}
