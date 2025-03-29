package dev.gamekit.utils;

public record Config(String title, Resolution resolution, boolean isFullScreen) {
  public static final boolean DEBUG_DRAW = true;
  @Override
  public String toString() {
    return String.format("%s[resolution:%s,fullscreen:%b]", getClass().getName(), resolution, isFullScreen);
  }
}
