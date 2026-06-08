package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;
import dev.gamekit.utils.GMath;

import java.util.UUID;

/**
 * {@link LowPassFilter} is an audio filter which allows frequencies of an audio signal below a certain cutoff to
 * pass unaffected while heavily attenuating higher frequencies.
 */
public class LowPassFilter extends AudioEffect {
  private double b0, b1, b2, a1, a2;
  private double il1, il2, ir1, ir2;
  private double cutoff;
  private double resonance;

  public LowPassFilter(String id, double cutoff, double resonance) {
    super(id, 3);
    this.cutoff = Math.max(0, cutoff);
    this.resonance = Math.max(0, resonance);

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

  public LowPassFilter(double cutoff, double resonance) {
    this(UUID.randomUUID().toString(), cutoff, 1);
  }

  public LowPassFilter(double cutoff) {
    this(cutoff, 1);
  }

  /** Sets the cutoff frequency of this filter */
  public void setCutoff(double cutoff) {
    synchronized (this) {
      this.cutoff = Math.max(0, cutoff);
      updateParameters();
    }
  }

  /** Sets the resonance coefficient of this filter */
  public void setResonance(double resonance) {
    synchronized (this) {
      this.resonance = Math.max(0, resonance);
      updateParameters();
    }
  }

  @Override
  public void performProcess(double[] out, double il0, double ir0) {
    synchronized (this) {
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

  /** Updates the internal parameters of this filter */
  private void updateParameters() {
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
}
