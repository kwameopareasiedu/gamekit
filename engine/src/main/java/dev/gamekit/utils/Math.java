package dev.gamekit.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Provides commonly used math utility methods */
public final class Math {
  public static final double EPSILON = 1e-6;
  public static final double TWO_PI = 6.283185307179586;
  public static final double HALF_PI = 1.5707963267948966;
  public static final double DEGREES_TO_RADIANS = 0.017453292519943295;
  public static final double RADIANS_TO_DEGREES = 57.29577951308232;
  public static final String DEGREE_SYM = "°";
  // Animation curve constants
  public static final double C1 = 1.70158;
  public static final double C3 = C1 + 1;
  public static final double C2 = C1 * 1.525;
  public static final double C4 = (2 * java.lang.Math.PI) / 3;
  public static final double C5 = (2 * java.lang.Math.PI) / 4.5;
  public static final double N1 = 7.5625;
  public static final double D1 = 2.75;

  private static final Logger LOGGER = LogManager.getLogger();

  private Math() { }

  /** Clamps a double to be within the range of {@code min} and {@code max} */
  public static double clamp(double val, double min, double max) {
    return java.lang.Math.min(max, java.lang.Math.max(min, val));
  }

  /** Clamps an integer to be within the range of {@code min} and {@code max} */
  public static int clamp(int val, int min, int max) {
    return java.lang.Math.min(max, java.lang.Math.max(min, val));
  }

  /** Cycles an integer within the range of {@code min} and {@code max} */
  public static int cycle(int val, int min, int max) {
    int diff = max - min + 1;
    while (val < min) val += diff;
    return min + val % (max + 1);
  }

  /** Converts a rotation in degree to radian */
  public static double degToRad(double deg) {
    return deg * DEGREES_TO_RADIANS;
  }

  /** Converts a rotation in radian to degree */
  public static double radToDeg(double rad) {
    return rad * RADIANS_TO_DEGREES;
  }

  /** Returns {@code true} if the given value is less than the engine's epsilon */
  public static boolean isPracticallyZero(double value) {
    return java.lang.Math.abs(value) < EPSILON;
  }

  /** Linearly interpolates between two values using a specified rate */
  public static double lerp(double start, double end, double rate) {
    return start + rate * (end - start);
  }

  /**
   * Linearly interpolates between two angles in radian using a specified rate, wrapping the angle
   * around {@code 2π} if it's a shorter path to reach the desired angle
   */
  public static double lerpAngle(double start, double end, double rate) {
    double cos = (1.0 - rate) * java.lang.Math.cos(start) + rate * java.lang.Math.cos(end);
    double sin = (1.0 - rate) * java.lang.Math.sin(start) + rate * java.lang.Math.sin(end);
    return java.lang.Math.atan2(sin, cos);
  }
}
