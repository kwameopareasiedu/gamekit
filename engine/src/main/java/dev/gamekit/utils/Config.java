package dev.gamekit.utils;

public record Config(String title, Resolution resolution, boolean isFullScreen) {
  @Override
  public String toString() {
    return String.format("%s[resolution:%s,fullscreen:%b]", getClass().getName(), resolution, isFullScreen);
  }
}
