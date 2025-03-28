package dev.gamekit.animation;

import dev.gamekit.core.Application;

import static dev.gamekit.utils.Math.clamp;

/**
 * Animation holds a value which increments from 0 to 1 over some duration.
 * The value can then be connected to any property for smooth transitions.
 * <p>
 * Animation can be set to run once or repeat (either restart or revers).
 * Additionally, an {@link AnimationCurve} can be attached to change how
 * the animation's value is interpolated.
 */
public class Animation {
  private final RepeatMode repeatMode;
  private final AnimationCurve curve;
  private ValueListener valueListener;
  private StateListener stateListener;
  private double rate;
  private State state;
  private double value;

  /**
   * Creates a non-repeating animation with a duration
   * @param duration The duration of this animation in seconds
   */
  public Animation(double duration) {
    this(duration, RepeatMode.NONE, null);
  }

  /**
   * Creates an animation with a duration and repeat mode
   * @param duration   The duration of this animation in seconds
   * @param repeatMode The repeat mode of this animation
   */
  public Animation(double duration, RepeatMode repeatMode) {
    this(duration, repeatMode, null);
  }

  /**
   * Creates an animation with a duration, repeat mode and animation curve
   * @param duration   The duration of this animation in seconds
   * @param repeatMode The repeat mode of this animation
   * @param curve      The animation curve for transforming the value
   */
  public Animation(double duration, RepeatMode repeatMode, AnimationCurve curve) {
    if (duration <= 0) throw new IllegalArgumentException("Animation duration must be positive");

    this.repeatMode = repeatMode;
    this.curve = curve;
    rate = 1.0 / duration;
    state = State.IDLE;
    value = 0;
  }

  public State getState() { return state; }

  public double getValue() { return curve != null ? curve.get(value) : value; }

  /** Sets the value listener and returns this animation */
  public Animation setValueListener(ValueListener listener) {
    this.valueListener = listener;
    return this;
  }

  /** Sets the state listener and returns this animation */
  public Animation setStateListener(StateListener listener) {
    this.stateListener = listener;
    return this;
  }

  /** Starts this animation and changes its state to {@link State#RUNNING} */
  public void start() {
    if (state == State.IDLE) {
      state = State.RUNNING;
      if (stateListener != null)
        stateListener.onStateChanged(state);
      Application.getInstance().scheduleAnimation(this);
    }
  }

  /**
   * Stops this animation and changes its state to {@link State#ENDED}.
   * Ended animation cannot be restarted.
   */
  public void stop() {
    state = State.ENDED;
    if (stateListener != null)
      stateListener.onStateChanged(state);
  }

  /** Called internally by the application game loop to update this animation */
  public void update() {
    if (state == State.RUNNING) {
      value = clamp(value + 0.001 * rate * Application.FRAME_TIME, 0, 1);
      if (valueListener != null) valueListener.onValueChanged(value);

      if ((value >= 1 && rate > 0) || (value <= 0 && rate < 0)) {
        switch (repeatMode) {
          case NONE -> {
            state = State.ENDED;
            if (stateListener != null)
              stateListener.onStateChanged(state);
          }
          case RESTART -> value = 0;
          case REVERSE -> rate *= -1;
        }
      }
    }
  }

  public enum State {
    IDLE, RUNNING, ENDED
  }

  public enum RepeatMode {
    NONE, RESTART, REVERSE
  }

  /** Callback interface for animation value changes */
  public interface ValueListener {
    /**
     * Called with the new base value of the animation
     * without apply the animation curve transformation
     */
    void onValueChanged(double value);
  }

  /** Callback interface for animation state changes */
  public interface StateListener {
    /** Called with the new state of the animation */
    void onStateChanged(State state);
  }
}
