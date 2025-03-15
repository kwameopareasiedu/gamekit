package dev.gamekit.animation;

import dev.gamekit.core.Application;

import static dev.gamekit.utils.MathUtils.clamp;

public class Animation {
  private final RepeatMode repeatMode;
  private final AnimationCurve curve;
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

  /**
   * Returns the state of this animation
   * @return {@link State} The animation state
   */
  public State getState() { return state; }

  /**
   * Returns the current animation value
   * @return The animation value
   */
  public double getValue() {
    if (curve == null) return value;
    else return curve.transform(value);
  }

  /**
   * Starts this animation.
   * After this the state is changed to {@link State#RUNNING}
   */
  public void start() {
    if (state == State.IDLE) {
      state = State.RUNNING;
      Application.getInstance().addAnimation(this);
    }
  }

  /** Called internally by the application game loop to update this animation */
  public void update() {
    if (state == State.RUNNING) {
      value = clamp(value + 0.001 * rate * Application.FRAME_TIME, 0, 1);

      if ((value >= 1 && rate > 0) || (value <= 0 && rate < 0)) {
        switch (repeatMode) {
          case NONE -> state = State.ENDED;
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
}
