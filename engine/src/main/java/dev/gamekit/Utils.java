package dev.gamekit;

public final class Utils {
  private Utils() { }

  public static int toInt(float number) {
    return (int) number;
  }

  public static int toInt(double number) {
    return toInt((float) number);
  }

  public static double clamp(double val, double min, double max) {
    return Math.min(max, Math.max(min, val));
  }
}
