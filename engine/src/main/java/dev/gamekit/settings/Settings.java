package dev.gamekit.settings;

import dev.gamekit.core.Application;

/** {@link Settings} contains startup configuration object for an {@link Application} instance */
public class Settings {
  public final String title;
  public final Resolution resolution;
  public final boolean fullscreen;
  public final boolean undecorated;
  public final Antialiasing antialiasing;
  public final TextAntialiasing textAntialiasing;
  public final AlphaInterpolation alphaInterpolation;
  public final ImageInterpolation imageInterpolation;
  public final RenderingStrategy renderingStrategy;
  public final Dithering dithering;

  public Settings(String title, Setting... settings) {
    this.title = title;
    this.resolution = get(Resolution.class, settings, Resolution.SVGA);
    this.fullscreen = get(WindowMode.class, settings, WindowMode.WINDOWED) == WindowMode.FULLSCREEN;
    this.undecorated = get(WindowMode.class, settings, WindowMode.WINDOWED) == WindowMode.BORDERLESS;
    this.antialiasing = get(Antialiasing.class, settings, Antialiasing.DEFAULT);
    this.textAntialiasing = get(TextAntialiasing.class, settings, TextAntialiasing.ON);
    this.alphaInterpolation = get(AlphaInterpolation.class, settings, AlphaInterpolation.DEFAULT);
    this.imageInterpolation = get(ImageInterpolation.class, settings, ImageInterpolation.NEAREST);
    this.renderingStrategy = get(RenderingStrategy.class, settings, RenderingStrategy.DEFAULT);
    this.dithering = get(Dithering.class, settings, Dithering.DEFAULT);
  }

  @Override
  public String toString() {
    return String.format(
      "%s[title:%s,resolution:%s,fullscreen:%b,antialiasing:%s,text antialiasing:%s," +
        "alpha interpolation:%s,image interpolation:%s,rendering:%s,dithering:%s]",
      getClass().getName(),
      title,
      resolution,
      fullscreen,
      antialiasing,
      textAntialiasing,
      alphaInterpolation,
      imageInterpolation,
      renderingStrategy,
      dithering
    );
  }

  private <T> T get(Class<T> clazz, Setting[] values, T fallbackValue) {
    for (var val : values) {
      try {
        if (clazz.isInstance(val))
          //noinspection unchecked
          return (T) val;
      } catch (Exception ignored) { }
    }

    return fallbackValue;
  }
}
