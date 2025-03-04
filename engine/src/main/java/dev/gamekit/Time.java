package dev.gamekit;

/** Stores global time related information at runtime. */
public final class Time {
  public static final long FRAME_TIME = 1000 / 60;

  public static long timeSinceLoad = 0;

  private Time() { }
}
