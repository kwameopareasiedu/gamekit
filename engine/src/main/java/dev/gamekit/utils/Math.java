package dev.gamekit.utils;

/** Provides commonly used math utility methods */
public final class Math {
  private Math() { }

  public static int toInt(float number) {
    return (int) number;
  }

  public static int toInt(double number) {
    return toInt((float) number);
  }

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
    return deg / 180 * java.lang.Math.PI;
  }

  /** Converts a rotation in radian to degree */
  public static double radToDeg(double rad) {
    return (rad / (2 * java.lang.Math.PI)) * 360;
  }

  /** Linearly interpolates between two values using a specified rate */
  public static double lerp(double from, double to, double rate) {
    return from + rate * (to - from);
  }
}
