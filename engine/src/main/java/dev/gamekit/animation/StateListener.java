package dev.gamekit.animation;

/** Callback interface for {@link Animation} state changes */
public interface StateListener {
  /** Called with the new {@link Animation.State} of the animation */
  void onStateChanged(Animation.State state);
}
