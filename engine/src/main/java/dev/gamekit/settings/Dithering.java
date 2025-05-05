package dev.gamekit.settings;

import java.awt.*;

import static java.awt.RenderingHints.*;

public enum Dithering {
  ON, OFF, DEFAULT;

  static Dithering from(Graphics2D g) {
    Object hint = g.getRenderingHint(KEY_DITHERING);
    if (hint == VALUE_DITHER_ENABLE) return ON;
    if (hint == VALUE_DITHER_DISABLE) return OFF;
    return DEFAULT;
  }

  public void apply(Graphics2D g) {
    g.setRenderingHint(KEY_DITHERING, switch (this) {
      case ON -> VALUE_DITHER_ENABLE;
      case OFF -> VALUE_DITHER_DISABLE;
      default -> VALUE_DITHER_DEFAULT;
    });
  }
}
