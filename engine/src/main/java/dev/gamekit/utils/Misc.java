package dev.gamekit.utils;

/** Provides miscellaneous utility methods */
public class Misc {
  private Misc() { }

  /** Returns the first non-null argument from the provided list or {@code null} */
  @SafeVarargs
  public static <T> T coalesce(T... values) {
    for (T val : values) {
      if (val != null)
        return val;
    }

    return null;
  }
}
