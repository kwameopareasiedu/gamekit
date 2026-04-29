package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;

public class ReverbFilter extends AudioEffect {
  private final CombFilter[] combFilters;
  private final AllPassFilter[] allPassFilters;
  private final double decayFactor;
  private final double mixRatio;

  public ReverbFilter(double delayMs, double decayFactor, double mixRatio) {
    super(
      new double[2 * (int) (0.001 * delayMs * Audio.SAMPLE_RATE)],
      new double[2 * (int) (0.001 * delayMs * Audio.SAMPLE_RATE)]
    );
    this.decayFactor = decayFactor;
    this.mixRatio = mixRatio;

    combFilters = new CombFilter[]{
      new CombFilter(25.306122448979593, 0.8),
      new CombFilter(26.93877551020408, 0.8),
      new CombFilter(28.95691609977324, 0.8),
      new CombFilter(30.74829931972789, 0.8)
    };

    allPassFilters = new AllPassFilter[]{
      new AllPassFilter(12.607709750566894, 0.5),
      new AllPassFilter(12.607709750566894, 0.5),
    };
  }

  @Override
  public void performProcess(double sampleL, double sampleR) {
    out[0] = out[1] = 0;

    for (CombFilter f : combFilters) {
      double[] res = f.process(sampleL, sampleR);
      out[0] += res[0];
      out[1] += res[1];
    }

    out[0] /= combFilters.length;
    out[1] /= combFilters.length;

    for (AllPassFilter f : allPassFilters) {
      double[] res = f.process(out[0], out[1]);
      out[0] = res[0];
      out[1] = res[1];
    }

    out[0] = (1 - mixRatio) * sampleL + mixRatio * out[0];
    out[1] = (1 - mixRatio) * sampleR + mixRatio * out[1];
  }
}
