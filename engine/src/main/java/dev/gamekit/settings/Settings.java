package dev.gamekit.settings;

import dev.gamekit.core.Application;

/** {@link Settings} contains startup configuration object for an {@link Application} instance */
public record Settings(
  String title,
  Resolution resolution,
  boolean fullScreen,
  Antialiasing antialiasing,
  AlphaInterpolation alphaInterpolation,
  ImageInterpolation imageInterpolation,
  RenderingStrategy renderingStrategy,
  Dithering dithering
) {
  public Settings(String title) {
    this(
      title,
      Resolution.NATIVE,
      true,
      Antialiasing.DEFAULT,
      AlphaInterpolation.DEFAULT,
      ImageInterpolation.NEAREST,
      RenderingStrategy.DEFAULT,
      Dithering.DEFAULT
    );
  }

  public Settings(String title, Resolution resolution, boolean fullScreen) {
    this(
      title,
      resolution,
      fullScreen,
      Antialiasing.DEFAULT,
      AlphaInterpolation.DEFAULT,
      ImageInterpolation.NEAREST,
      RenderingStrategy.DEFAULT,
      Dithering.DEFAULT
    );
  }

  @Override
  public String toString() {
    return String.format("%s[title:%s,resolution:%s,fullscreen:%b,antialiasing:%s,alpha interpolation:%s,image interpolation:%s,rendering:%s,dithering:%s]",
      getClass().getName(),
      title,
      resolution,
      fullScreen,
      antialiasing,
      alphaInterpolation,
      imageInterpolation,
      renderingStrategy,
      dithering
    );
  }
}
