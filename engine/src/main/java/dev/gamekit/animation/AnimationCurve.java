package dev.gamekit.animation;

import static dev.gamekit.utils.GMath.*;

/**
 * {@link AnimationCurve} transforms an {@link Animation} value from a (0 - 1) range to a point on a curve equation
 * defined in {@link #get} method.
 */
public interface AnimationCurve {
  AnimationCurve LINEAR = value -> value;

  AnimationCurve EASE_IN_SINE = value -> 1 - Math.cos((value * Math.PI) / 2);

  AnimationCurve EASE_OUT_SINE = value -> Math.sin((value * Math.PI) / 2);

  AnimationCurve EASE_IN_OUT_SINE = value -> -(Math.cos(Math.PI * value) - 1) / 2;

  AnimationCurve EASE_IN_QUAD = value -> value * value;

  AnimationCurve EASE_OUT_QUAD = value -> 1 - (1 - value) * (1 - value);

  AnimationCurve EASE_IN_OUT_QUAD = value -> value < 0.5
    ? 2 * value * value
    : 1 - Math.pow(-2 * value + 2, 2) / 2;

  AnimationCurve EASE_IN_CUBIC = value -> value * value * value;

  AnimationCurve EASE_OUT_CUBIC = value -> 1 - Math.pow(1 - value, 3);

  AnimationCurve EASE_IN_OUT_CUBIC = value -> value < 0.5
    ? 4 * value * value * value
    : 1 - Math.pow(-2 * value + 2, 3) / 2;

  AnimationCurve EASE_IN_EXPO = value -> value == 0 ? 0 : Math.pow(2, 10 * value - 10);

  AnimationCurve EASE_OUT_EXPO = value -> value == 1 ? 1 : 1 - Math.pow(2, -10 * value);

  AnimationCurve EASE_IN_OUT_EXPO = value -> value == 0
    ? 0
    : value == 1 ? 1
    : value < 0.5 ? Math.pow(2, 20 * value - 10) / 2
    : (2 - Math.pow(2, -20 * value + 10)) / 2;

  AnimationCurve EASE_IN_BACK = value -> C3 * value * value * value - C1 * value * value;

  AnimationCurve EASE_OUT_BACK = value -> 1 + C3 * Math.pow(value - 1, 3) + C1 * Math.pow(value - 1, 2);

  AnimationCurve EASE_IN_OUT_BACK = value -> value < 0.5
    ? (Math.pow(2 * value, 2) * ((C2 + 1) * 2 * value - C2)) / 2
    : (Math.pow(2 * value - 2, 2) * ((C2 + 1) * (value * 2 - 2) + C2) + 2) / 2;

  AnimationCurve EASE_IN_ELASTIC = value -> value == 0
    ? 0
    : value == 1 ? 1
    : -Math.pow(2, 10 * value - 10) * Math.sin((value * 10 - 10.75) * C4);

  AnimationCurve EASE_OUT_ELASTIC = value -> value == 0
    ? 0
    : value == 1 ? 1
    : Math.pow(2, -10 * value) * Math.sin((value * 10 - 0.75) * C4) + 1;

  AnimationCurve EASE_IN_OUT_ELASTIC = value -> value == 0
    ? 0
    : value == 1 ? 1
    : value < 0.5 ? -(Math.pow(2, 20 * value - 10) * Math.sin((20 * value - 11.125) * C5)) / 2
    : (Math.pow(2, -20 * value + 10) * Math.sin((20 * value - 11.125) * C5)) / 2 + 1;

  AnimationCurve EASE_OUT_BOUNCE = value -> value < 1 / D1
    ? N1 * value * value
    : value < 2 / D1 ? N1 * (value -= 1.5 / D1) * value + 0.75
    : value < 2.5 / D1 ? N1 * (value -= 2.25 / D1) * value + 0.9375
    : N1 * (value -= 2.625 / D1) * value + 0.984375;

  AnimationCurve EASE_IN_BOUNCE = value -> 1 - EASE_OUT_BOUNCE.get(1 - value);

  AnimationCurve EASE_IN_OUT_BOUNCE = value -> value < 0.5
    ? (1 - EASE_OUT_BOUNCE.get(1 - 2 * value)) / 2
    : (1 + EASE_OUT_BOUNCE.get(2 * value - 1)) / 2;

  /** Returns the result of the curve equation at the given value */
  double get(double value);
}
