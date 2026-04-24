package dev.gamekit.audio.filters;

public class ExponentialMovingAverageFilter implements AudioFilter {
  private final double[] out = new double[2];
  private final double alpha;

  public ExponentialMovingAverageFilter(double alpha) {
    this.alpha = alpha;
  }

  @Override
  public double[] process(double leftValue, double rightValue) {
    out[0] = (int) (alpha * leftValue + (1 - alpha) * out[0]);
    out[1] = (int) (alpha * rightValue + (1 - alpha) * out[1]);
    return out;
  }
}
