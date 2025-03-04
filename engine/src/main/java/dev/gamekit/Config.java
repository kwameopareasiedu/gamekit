package dev.gamekit;

import java.util.Locale;

/** GameKit launcher configuration class. Use {@code Config.Builder} to configure options */
public final class Config {
  final String title;
  final int screenWidth;
  final int screenHeight;

  public Config(String title, int screenWidth, int screenHeight) {
    this.title = title;
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
  }

  @Override
  public String toString() {
    return String.format(Locale.getDefault(), "Config(" +
        "title: \"%s\", " +
        "screenWidth: %d, " +
        "screenHeight: %d" +
        ")",
      title, screenWidth, screenHeight
    );
  }

  public static final class Builder {
    private String title = "GameKit Game";
    private int screenWidth = 800;
    private int screenHeight = 600;

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder setWindowWidth(int width) {
      this.screenWidth = width;
      return this;
    }

    public Builder setWindowHeight(int height) {
      this.screenHeight = height;
      return this;
    }

    public Config build() {
      return new Config(title, screenWidth, screenHeight);
    }
  }
}