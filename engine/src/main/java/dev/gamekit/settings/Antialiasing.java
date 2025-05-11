package dev.gamekit.settings;

import java.awt.*;

import static java.awt.RenderingHints.*;

public enum Antialiasing implements Setting {
  ON, OFF, DEFAULT;

  static Antialiasing from(Graphics2D g) {
    Object hint = g.getRenderingHint(KEY_ANTIALIASING);
    if (hint == VALUE_ANTIALIAS_ON) return ON;
    if (hint == VALUE_ANTIALIAS_OFF) return OFF;
    return DEFAULT;
  }

  public void apply(Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, switch (this) {
      case ON -> VALUE_ANTIALIAS_ON;
      case OFF -> VALUE_ANTIALIAS_OFF;
      default -> VALUE_ANTIALIAS_DEFAULT;
    });
  }
}
