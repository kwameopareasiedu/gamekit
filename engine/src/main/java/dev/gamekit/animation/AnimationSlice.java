package dev.gamekit.animation;

/**
 * {@link AnimationSlice} observes a slice of an {@link Animation}'s value and maps it to a new 0 - 1 range.
 * <p>
 * For example, if you are interested in the animation's value between 0.5 and 0.8, you can create an
 * {@link AnimationSlice} with this range. In this case, the slice's value will remain at 0 until the backing animation
 * value reaches 0.5
 */
public class AnimationSlice {
  private final Animation animation;
  private final AnimationCurve curve;
  private final double min;
  private final double max;
  private final double gradient;
  private final double intercept;

  public AnimationSlice(Animation animation, double min, double max) {
    this(animation, null, min, max);
  }

  public AnimationSlice(Animation animation, AnimationCurve curve, double min, double max) {
    this.animation = animation;
    this.min = min;
    this.max = max;
    this.curve = curve;

    if (min < 0 || min > 1) throw new IllegalArgumentException("AnimationRange min value must be between 0 and 1");
    if (max < 0 || max > 1) throw new IllegalArgumentException("AnimationRange max value must be between 0 and 1");
    if (max <= min) throw new IllegalArgumentException("AnimationRange min value must be less than its max value");

    gradient = 1 / (max - min);
    intercept = 1 - (max / (max - min));
  }

  /** Computes and returns the value of this {@link AnimationSlice} */
  public double getValue() {
    double animVal = animation.getValue();

    if (animVal < min) return 0;
    else if (animVal > max) return 1;

    double val = gradient * animVal + intercept;

    return curve != null ? curve.get(val) : val;
  }
}
