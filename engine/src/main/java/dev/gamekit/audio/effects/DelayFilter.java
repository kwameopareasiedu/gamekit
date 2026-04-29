package dev.gamekit.audio.effects;

import dev.gamekit.core.Audio;

/** {@link DelayFilter} is an audio filter delays an audio signal by a specified time */
public class DelayFilter extends AudioEffect {
  public DelayFilter(double delayMs) {
    super((int) (0.001 * delayMs * Audio.SAMPLE_RATE));
  }

  @Override
  public void performProcess(double[] out, double sampleL, double sampleR) {
    int delayHead = getWrappedIndex(head - 1);
    bufferL[delayHead] = sampleL;
    bufferR[delayHead] = sampleR;

    out[0] = bufferL[head];
    out[1] = bufferR[head];
  }
}
