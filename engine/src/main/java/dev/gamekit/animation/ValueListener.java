package dev.gamekit.animation;

/** Callback interface for {@link Animation} value changes */
public interface ValueListener {
  /**
   * Called with the new base value of the animation without the
   * {@link AnimationCurve} transformation
   */
  void onValueChanged(double value);
}
