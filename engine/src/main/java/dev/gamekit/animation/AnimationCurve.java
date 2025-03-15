package dev.gamekit.animation;

/** Animation curves transforms an animation's value from a (0 - 1) range to a point on a curve */
public abstract class AnimationCurve {
  /** Transforms the value provided into a new value by some logic or math equation */
  public abstract double transform(double value);
}
