package dev.gamekit.settings;

import java.awt.*;

import static java.awt.RenderingHints.*;

public enum TextAntialiasing implements Setting {
  ON, OFF, DEFAULT;

  public static TextAntialiasing from(Graphics2D g) {
    Object hint = g.getRenderingHint(KEY_TEXT_ANTIALIASING);
    if (hint == VALUE_TEXT_ANTIALIAS_ON) return ON;
    if (hint == VALUE_TEXT_ANTIALIAS_OFF) return OFF;
    return DEFAULT;
  }

  public void apply(Graphics2D g) {
    g.setRenderingHint(KEY_TEXT_ANTIALIASING, switch (this) {
      case ON -> VALUE_TEXT_ANTIALIAS_ON;
      case OFF -> VALUE_TEXT_ANTIALIAS_OFF;
      default -> VALUE_TEXT_ANTIALIAS_DEFAULT;
    });
  }
}
