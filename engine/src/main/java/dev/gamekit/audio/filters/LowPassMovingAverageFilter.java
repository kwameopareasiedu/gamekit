package dev.gamekit.audio.filters;

import dev.gamekit.utils.GMath;

/**
 * {@link LowPassMovingAverageFilter} implements the
 * <a href="https://en.wikipedia.org/wiki/Moving_average">Moving Average</a> filter for low pass frequency filtering
 */
public class LowPassMovingAverageFilter implements AudioFilter {
  private final double[] out = new double[2];
  private final double alpha;

  /** Creates a new {@link LowPassMovingAverageFilter} filter with the {@code cutoff} frequency in Hz */
  public LowPassMovingAverageFilter(double cutoff) {
    this.alpha = (GMath.TWO_PI * cutoff) / (44100 + (GMath.TWO_PI * cutoff));
  }

  @Override
  public double[] process(double leftValue, double rightValue) {
    out[0] = (int) (alpha * leftValue + (1 - alpha) * out[0]);
    out[1] = (int) (alpha * rightValue + (1 - alpha) * out[1]);
    return out;
  }
}
