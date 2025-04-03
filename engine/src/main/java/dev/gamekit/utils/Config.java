package dev.gamekit.utils;

/**
 * Configuration object for the application containing the title,
 * {@link Resolution resolution} and fullscreen flag
 */
public record Config(String title, Resolution resolution,
                     boolean isFullScreen) {
  public static final boolean DEBUG_DRAW = false;

  @Override
  public String toString() {
    return String.format("%s[resolution:%s,fullscreen:%b]", getClass().getName(), resolution, isFullScreen);
  }
}
