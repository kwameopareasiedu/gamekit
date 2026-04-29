package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;

/** {@link CombFilter} is an audio filter which adds delayed version of an audio signal back unto itself */
public class CombFilter extends AudioEffect {
  private final double damping;

  public CombFilter(double delayMs, double damping) {
    super((int) (0.001 * delayMs * Audio.SAMPLE_RATE));

    this.damping = damping;
  }

  @Override
  public void performProcess(double[] out, double sampleL, double sampleR) {
    int delayHead = getWrappedIndex(head + 1);
    double delayedOutputL = bufferL[delayHead];
    double delayedOutputR = bufferR[delayHead];

    out[0] = bufferL[head] = sampleL + damping * delayedOutputL;
    out[1] = bufferR[head] = sampleR + damping * delayedOutputR;
  }
}
