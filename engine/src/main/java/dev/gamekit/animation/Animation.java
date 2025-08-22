package dev.gamekit.animation;

import dev.gamekit.core.Application;
import dev.gamekit.core.Constants;

import static dev.gamekit.utils.Math.clamp;

/**
 * {@link Animation} holds a value which increments from 0 to 1 over some duration. The value can
 * then be connected to any property for smooth transitions.
 * <p>
 * Animation can be set to run once or repeat (either restart or alternate). Additionally, an
 * {@link AnimationCurve} can be attached to change how the animation's value is interpolated.
 */
public class Animation {
  private final RepeatMode repeatMode;
  private final AnimationCurve curve;
  private StateListener stateListener;
  private ValueListener valueListener;
  private State state;
  private double rate;
  private double value;

  public Animation(double durationMs) {
    this(durationMs, RepeatMode.NONE, null);
  }

  public Animation(double durationMs, RepeatMode repeatMode) {
    this(durationMs, repeatMode, null);
  }

  public Animation(double durationMs, RepeatMode repeatMode, AnimationCurve curve) {
    if (durationMs <= 0)
      throw new IllegalArgumentException("Animation duration must be positive");

    this.repeatMode = repeatMode;
    this.curve = curve;
    rate = 1000.0 / durationMs;
    state = State.IDLE;
    value = 0;
  }

  public State getState() { return state; }

  public double getValue() {
    return curve != null ? curve.get(value) : value;
  }

  /** Sets the state listener and returns this animation */
  public Animation setStateListener(StateListener listener) {
    this.stateListener = listener;
    return this;
  }

  /** Sets the value listener and returns this animation */
  public Animation setValueListener(ValueListener listener) {
    this.valueListener = listener;
    return this;
  }

  /** Starts / Restarts this animation and changes its state to {@link State#RUNNING} */
  public void start() {
    if (state == State.IDLE)
      Application.getInstance().playAnimation(this);

    state = State.RUNNING;
    value = 0;

    if (stateListener != null)
      stateListener.onStateChanged(state);

    if (valueListener != null)
      valueListener.onValueChanged(value);
  }

  /**
   * Stops and resets this animation by changing its state to {@link State#STOPPED} and its value
   * to {@code 0}. Stopped animations can be restarted by calling {@link #start}
   */
  public void stop() {
    state = State.STOPPED;

    if (stateListener != null)
      stateListener.onStateChanged(state);
  }

  /**
   * Ends this animation and changes its state to {@link State#ENDED}. Ended animations are
   * removed from the engine and cannot be restarted.
   */
  public void end() {
    state = State.ENDED;

    if (stateListener != null)
      stateListener.onStateChanged(state);
  }

  public boolean isEnded() {
    return state == State.ENDED;
  }

  /** Called internally by the application game loop to update this animation */
  public void update() {
    if (state == State.RUNNING) {
      value = clamp(value + 0.001 * rate * Constants.FRAME_INTERVAL_MS, 0, 1);

      if (valueListener != null)
        valueListener.onValueChanged(getValue());

      if ((value >= 1 && rate > 0) || (value <= 0 && rate < 0)) {
        switch (repeatMode) {
          case NONE -> stop();
          case RESTART -> value = 0;
          case ALTERNATE -> rate *= -1;
        }
      }
    }
  }

  /** Constants for the state of an {@link Animation} */
  public enum State {
    /** Indicates a new animation which hasn't started */
    IDLE,
    /** Indicates a started animation */
    RUNNING,
    /** Indicates a stopped animation which can be restarted */
    STOPPED,
    /** Indicates an ended animation which cannot be restarted */
    ENDED
  }

  /** Indicates how an animation behaves when it reaches its end */
  public enum RepeatMode {
    /**
     * Indicates a running animation not repeat and transition to {@link State#ENDED} when at its
     * end
     */
    NONE,
    /** Indicates a running animation start over when at its end */
    RESTART,
    /** Indicates a running animation changes direction when at its end */
    ALTERNATE
  }

  /** Callback interface for {@link Animation} state changes */
  public interface StateListener {
    /** Called with the new {@link State} of the animation */
    void onStateChanged(State state);
  }

  /** Callback interface for {@link Animation} value changes */
  public interface ValueListener {
    /** Called with the new value of the animation */
    void onValueChanged(double value);
  }
}
