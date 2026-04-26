package dev.gamekit.audio.filters;

import dev.gamekit.core.Audio;

/** {@link DelayFilter} is an audio filter delays an audio signal by a specified time */
public class DelayFilter extends AudioFilter {
  public DelayFilter(double delayMs) {
    super(
      new double[2 * (int) (0.001 * delayMs * Audio.SAMPLE_RATE)],
      new double[2 * (int) (0.001 * delayMs * Audio.SAMPLE_RATE)]
    );
  }

  @Override
  public void performProcess(double sampleL, double sampleR) {
    out[0] = inputSamples[inputSamples.length - 2];
    out[1] = inputSamples[inputSamples.length - 1];
  }
}
