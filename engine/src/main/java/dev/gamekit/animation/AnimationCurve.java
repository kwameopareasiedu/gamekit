package dev.gamekit.animation;

import static dev.gamekit.utils.Constants.*;

/** Animation curves transforms an animation's value from a (0 - 1) range to a point on a curve */
public abstract class AnimationCurve {
  public static final AnimationCurve LINEAR = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value;
    }
  };

  public static final AnimationCurve EASE_IN_SINE = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return 1 - Math.cos((value * Math.PI) / 2);
    }
  };

  public static final AnimationCurve EASE_OUT_SINE = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return Math.sin((value * Math.PI) / 2);
    }
  };

  public static final AnimationCurve EASE_IN_OUT_SINE = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return -(Math.cos(Math.PI * value) - 1) / 2;
    }
  };

  public static final AnimationCurve EASE_IN_QUAD = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value * value;
    }
  };

  public static final AnimationCurve EASE_OUT_QUAD = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return 1 - (1 - value) * (1 - value);
    }
  };

  public static final AnimationCurve EASE_IN_OUT_QUAD = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value < 0.5 ? 2 * value * value : 1 - Math.pow(-2 * value + 2, 2) / 2;
    }
  };

  public static final AnimationCurve EASE_IN_CUBIC = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value * value * value;
    }
  };

  public static final AnimationCurve EASE_OUT_CUBIC = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return 1 - value * value * value;
    }
  };

  public static final AnimationCurve EASE_IN_OUT_CUBIC = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value < 0.5 ? 4 * value * value * value : 1 - Math.pow(-2 * value + 2, 3) / 2;
    }
  };

  public static final AnimationCurve EASE_IN_EXPO = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value == 0 ? 0 : Math.pow(2, 10 * value - 10);
    }
  };

  public static final AnimationCurve EASE_OUT_EXPO = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value == 1 ? 1 : 1 - Math.pow(2, -10 * value);
    }
  };

  public static final AnimationCurve EASE_IN_OUT_EXPO = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value == 0 ? 0 : value == 1 ? 1
        : value < 0.5 ? Math.pow(2, 20 * value - 10) / 2
        : (2 - Math.pow(2, -20 * value + 10)) / 2;
    }
  };

  public static final AnimationCurve EASE_IN_BACK = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return C3 * value * value * value - C1 * value * value;
    }
  };

  public static final AnimationCurve EASE_OUT_BACK = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return 1 + C3 * Math.pow(value - 1, 3) + C1 * Math.pow(value - 1, 2);
    }
  };

  public static final AnimationCurve EASE_IN_OUT_BACK = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value < 0.5
        ? (Math.pow(2 * value, 2) * ((C2 + 1) * 2 * value - C2)) / 2
        : (Math.pow(2 * value - 2, 2) * ((C2 + 1) * (value * 2 - 2) + C2) + 2) / 2;
    }
  };

  public static final AnimationCurve EASE_IN_ELASTIC = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value == 0 ? 0
        : value == 1 ? 1
        : -Math.pow(2, 10 * value - 10) * Math.sin((value * 10 - 10.75) * C4);
    }
  };

  public static final AnimationCurve EASE_OUT_ELASTIC = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value == 0 ? 0
        : value == 1 ? 1
        : Math.pow(2, -10 * value) * Math.sin((value * 10 - 0.75) * C4) + 1;
    }
  };

  public static final AnimationCurve EASE_IN_OUT_ELASTIC = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value == 0
        ? 0
        : value == 1 ? 1
        : value < 0.5 ? -(Math.pow(2, 20 * value - 10) * Math.sin((20 * value - 11.125) * C5)) / 2
        : (Math.pow(2, -20 * value + 10) * Math.sin((20 * value - 11.125) * C5)) / 2 + 1;
    }
  };

  public static final AnimationCurve EASE_OUT_BOUNCE = new AnimationCurve() {
    @Override
    public double transform(double value) {
      if (value < 1 / D1) {
        return N1 * value * value;
      } else if (value < 2 / D1) {
        return N1 * (value -= 1.5 / D1) * value + 0.75;
      } else if (value < 2.5 / D1) {
        return N1 * (value -= 2.25 / D1) * value + 0.9375;
      } else {
        return N1 * (value -= 2.625 / D1) * value + 0.984375;
      }
    }
  };

  public static final AnimationCurve EASE_IN_BOUNCE = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return 1 - EASE_OUT_BOUNCE.transform(1 - value);
    }
  };

  public static final AnimationCurve EASE_IN_OUT_BOUNCE = new AnimationCurve() {
    @Override
    public double transform(double value) {
      return value < 0.5
        ? (1 - EASE_OUT_BOUNCE.transform(1 - 2 * value)) / 2
        : (1 + EASE_OUT_BOUNCE.transform(2 * value - 1)) / 2;
    }
  };

  /** Transforms the value provided into a new value by some logic or math equation */
  public abstract double transform(double value);
}
