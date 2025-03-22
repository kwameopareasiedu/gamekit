package dev.gamekit.utils;

import dev.gamekit.core.IO;

import java.awt.*;

public final class Constants {
  public static final Font DEFAULT_FONT = IO.loadFontResource("fffforward.ttf");
  public static final Color TRANSPARENT = new Color(0x0000000, true);

  private Constants() {}
}
