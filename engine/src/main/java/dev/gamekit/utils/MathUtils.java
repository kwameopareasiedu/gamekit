package dev.gamekit.utils;

public final class MathUtils {
  private MathUtils() { }

  public static int toInt(float number) {
    return (int) number;
  }

  public static int toInt(double number) {
    return toInt((float) number);
  }

  public static double clamp(double val, double min, double max) {
    return Math.min(max, Math.max(min, val));
  }

  public static int clamp(int val, int min, int max) {
    return Math.min(max, Math.max(min, val));
  }

  public static int cycle(int val, int min, int max) {
    int diff = max - min + 1;
    while (val < min) val += diff;
    return min + val % (max + 1);
  }
}
