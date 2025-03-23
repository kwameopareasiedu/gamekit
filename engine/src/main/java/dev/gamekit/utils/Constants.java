package dev.gamekit.utils;

import dev.gamekit.core.IO;

import java.awt.*;

/** Holds constants accessible across the entire engine */
public final class Constants {
  public static final Font DEFAULT_FONT = IO.loadFontResource("fffforward.ttf");
  public static final Color TRANSPARENT_COLOR = new Color(0x0000000, true);

  private Constants() {}
}
