package dev.gamekit.utils;

/** Utility class providing commonly used math utility methods */
public final class Math {
  private Math() { }

  /**
   * Casts a floating to an integer
   * @param number Float to cast
   * @return The cast integer
   */
  public static int toInt(float number) {
    return (int) number;
  }

  /**
   * Casts a double to an integer
   * @param number Double to cast
   * @return The cast integer
   */
  public static int toInt(double number) {
    return toInt((float) number);
  }

  /**
   * Clamps a double to be within the range of {@code min} and {@code max}
   * @param val Double value to clamp
   * @param min The minimum of the specified range
   * @param max The maximum of the specified range
   * @return The clamped value
   */
  public static double clamp(double val, double min, double max) {
    return java.lang.Math.min(max, java.lang.Math.max(min, val));
  }

  /**
   * Clamps an integer to be within the range of {@code min} and {@code max}
   * @param val Integer value to clamp
   * @param min The minimum of the specified range
   * @param max The maximum of the specified range
   * @return The clamped value
   */
  public static int clamp(int val, int min, int max) {
    return java.lang.Math.min(max, java.lang.Math.max(min, val));
  }

  /**
   * Cycles an integer within the range of {@code min} and {@code max}
   * @param val Integer value to cycle
   * @param min The minimum of the specified range
   * @param max The maximum of the specified range
   * @return The cycled value
   */
  public static int cycle(int val, int min, int max) {
    int diff = max - min + 1;
    while (val < min) val += diff;
    return min + val % (max + 1);
  }
}
