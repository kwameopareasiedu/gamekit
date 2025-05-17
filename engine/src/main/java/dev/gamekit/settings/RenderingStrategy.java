package dev.gamekit.settings;

import java.awt.*;

import static java.awt.RenderingHints.*;

public enum RenderingStrategy implements Setting {
  QUALITY, SPEED, DEFAULT;

  public static RenderingStrategy from(Graphics2D g) {
    Object hint = g.getRenderingHint(KEY_RENDERING);
    if (hint == VALUE_RENDER_QUALITY) return QUALITY;
    if (hint == VALUE_RENDER_SPEED) return SPEED;
    return DEFAULT;
  }

  public void apply(Graphics2D g) {
    g.setRenderingHint(KEY_RENDERING, switch (this) {
      case QUALITY -> VALUE_RENDER_QUALITY;
      case SPEED -> VALUE_RENDER_SPEED;
      default -> VALUE_RENDER_DEFAULT;
    });
  }
}
