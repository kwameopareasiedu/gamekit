package dev.gamekit.utils;

import java.util.List;
import java.util.function.Predicate;

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

  /**
   * Returns the first item from the given {@code items} which passes the given {@code predicate} object or
   * {@code null} if no item satisfies the {@code} predicate
   */
  public static <T> T getFirstMatch(T[] items, Predicate<T> predicate) {
    for (T item : items)
      if (predicate.test(item))
        return item;

    return null;
  }

  /**
   * Returns the first item from the given {@code items} which passes the given {@code predicate} object or
   * the value returned by {@code orElse} if no item satisfies the {@code} predicate
   */
  public static <T> T getFirstMatch(T[] items, Predicate<T> predicate, ValueGetter<T> orElse) {
    for (T item : items)
      if (predicate.test(item))
        return item;

    return orElse.get();
  }
}
