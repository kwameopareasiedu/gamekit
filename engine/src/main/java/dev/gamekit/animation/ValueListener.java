package dev.gamekit.animation;

/** Callback interface for {@link Animation} value changes */
public interface ValueListener {
  /**
   * Called with the new base value of the animation
   * without apply the animation curve transformation
   */
  void onValueChanged(double value);
}
