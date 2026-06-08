package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;
import dev.gamekit.utils.GMath;

import java.util.UUID;

public class ReverbFilter extends AudioEffect {
  private final CombFilter[] combFilters;
  private final AllPassFilter[] allPassFilters;
  private double damping;
  private double mix;

  public ReverbFilter(String id, double delayMs, double damping, double mix) {
    super(id, (int) (0.001 * delayMs * Audio.SAMPLE_RATE));
    this.damping = GMath.clamp(damping, 0, 1);
    this.mix = GMath.clamp(mix, 0, 1);

    combFilters = new CombFilter[]{
      new CombFilter(29.7, this.damping),
      new CombFilter(37.1, this.damping),
      new CombFilter(41.1, this.damping),
      new CombFilter(43.7, this.damping)
    };

    allPassFilters = new AllPassFilter[]{
      new AllPassFilter(5, 0.7),
      new AllPassFilter(1.7, 0.7),
    };
  }

  public ReverbFilter(double delayMs, double damping, double mix) {
    this(UUID.randomUUID().toString(), delayMs, damping, mix);
  }

  /** Sets the damping factor of this filter */
  public void setDamping(double damping) {
    synchronized (this) {
      this.damping = GMath.clamp(damping, 0, 1);

      for (CombFilter filter : combFilters) {
        filter.setDamping(this.damping);
      }
    }
  }

  /** Sets the wet mix ratio of this filter */
  public void setMix(double mix) {
    synchronized (this) {
      this.mix = GMath.clamp(mix, 0, 1);
    }
  }

  @Override
  public void performProcess(double[] out, double sampleL, double sampleR) {
    out[0] = out[1] = 0;

    for (CombFilter f : combFilters) {
      double[] filterSample = f.process(sampleL, sampleR);
      out[0] += filterSample[0];
      out[1] += filterSample[1];
    }

    out[0] /= combFilters.length;
    out[1] /= combFilters.length;

    for (AllPassFilter f : allPassFilters) {
      double[] filterSample = f.process(out[0], out[1]);
      out[0] = filterSample[0];
      out[1] = filterSample[1];
    }

    out[0] = bufferL[head] = (1 - mix) * sampleL + mix * out[0];
    out[1] = bufferR[head] = (1 - mix) * sampleR + mix * out[1];
  }
}
