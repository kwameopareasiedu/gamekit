package dev.gamekit.animation;

import dev.gamekit.core.Application;
import dev.gamekit.utils.ValueCallback;

import static dev.gamekit.utils.Math.clamp;

/**
 * {@link Animation} holds a value which increments from 0 to 1 over some duration. The value can then be connected
 * to any property for smooth transitions.
 * <p>
 * Animation can be set to run once or repeat (either restart or alternate).
 * <p>
 * Additionally, an {@link AnimationCurve} can be attached to change how the animation's value is interpolated.
 */
public class Animation {
  private final RepeatMode repeatMode;
  private final AnimationCurve curve;
  private ValueCallback<State> stateListener;
  private ValueCallback<Double> valueListener;
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
    if (durationMs <= 0) throw new IllegalArgumentException("Animation duration must be positive");

    this.repeatMode = repeatMode;
    this.curve = curve;
    rate = 1000.0 / durationMs;
    state = State.IDLE;
    value = 0;
  }

  /** Returns the current state */
  public State getState() {
    return state;
  }

  /**
   * Returns the current value (0 - 1).
   * <p>
   * If an {@link AnimationCurve} is attached, this returns the result of
   * {@link AnimationCurve#get} called with the value
   */
  public double getValue() {
    return curve != null ? curve.get(value) : value;
  }

  /** Returns the duration in milliseconds */
  public double getDuration() {
    return 1000.0 / rate;
  }

  /** Returns {@code true} if the animation is ended and false otherwise */
  public boolean isEnded() {
    return state == State.ENDED;
  }

  /** Sets the state listener and returns this animation */
  public Animation setStateListener(ValueCallback<State> listener) {
    this.stateListener = listener;
    return this;
  }

  /** Sets the value listener and returns this animation */
  public Animation setValueListener(ValueCallback<Double> listener) {
    this.valueListener = listener;
    return this;
  }

  /** Starts / Restarts this animation and changes its state to {@link State#RUNNING} */
  public void start() {
    if (state == State.ENDED)
      return;

    if (state == State.IDLE)
      Application.getInstance().playAnimation(this);

    if (state != State.PAUSED)
      value = 0;

    state = State.RUNNING;

    if (stateListener != null)
      stateListener.invoke(state);

    if (valueListener != null)
      valueListener.invoke(getValue());
  }

  /**
   * Pauses this animation by changing its state to {@link State#PAUSED}.
   * <p>
   * Paused animations can be restarted by calling {@link #start}
   */
  public void pause() {
    state = State.PAUSED;

    if (stateListener != null)
      stateListener.invoke(state);
  }

  /**
   * Stops and resets this animation by changing its state to {@link State#STOPPED} and its value to {@code 0}.
   * <p>
   * Stopped animations can be restarted by calling {@link #start}
   */
  public void stop() {
    state = State.STOPPED;

    if (stateListener != null)
      stateListener.invoke(state);
  }

  /**
   * Ends this animation and changes its state to {@link State#ENDED}. Ended animations are
   * removed from the engine and cannot be restarted.
   */
  public void end() {
    state = State.ENDED;

    if (stateListener != null)
      stateListener.invoke(state);
  }

  /** Called internally by the {@link Application} to update this animation */
  public void update() {
    if (state == State.RUNNING) {
      value = clamp(value + 0.001 * rate * Application.FRAME_INTERVAL_MS, 0, 1);

      if (valueListener != null)
        valueListener.invoke(getValue());

      if ((value >= 1 && rate > 0) || (value <= 0 && rate < 0)) {
        switch (repeatMode) {
          case NONE -> stop();
          case RESTART -> {
            value = 0;

            if (stateListener != null)
              stateListener.invoke(State.RESTARTED);
          }
          case ALTERNATE -> {
            rate *= -1;

            if (stateListener != null)
              stateListener.invoke(State.REVERSED);
          }
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
    /** Indicates a paused animation which can be resumed */
    PAUSED,
    /** Pseudo-state indicating an animation has restarted */
    RESTARTED,
    /** Pseudo-state indicating an animation whose direction changes */
    REVERSED,
    /** Indicates a stopped animation which can be restarted */
    STOPPED,
    /** Indicates an ended animation which cannot be restarted */
    ENDED
  }

  /** Indicates how an animation behaves when it reaches its end */
  public enum RepeatMode {
    /** Indicates an animation stops when at its end */
    NONE,
    /** Indicates an animation start over when at its end */
    RESTART,
    /** Indicates an animation changes direction when at its end */
    ALTERNATE
  }
}
