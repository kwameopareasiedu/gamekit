package dev.gamekit.settings;

import java.awt.*;

import static java.awt.RenderingHints.*;

public enum AlphaInterpolation {
  QUALITY, SPEED, DEFAULT;

  static AlphaInterpolation from(Graphics2D g) {
    Object hint = g.getRenderingHint(KEY_ALPHA_INTERPOLATION);
    if (hint == VALUE_ALPHA_INTERPOLATION_QUALITY) return QUALITY;
    if (hint == VALUE_ALPHA_INTERPOLATION_SPEED) return SPEED;
    return DEFAULT;
  }

  public void apply(Graphics2D g) {
    g.setRenderingHint(KEY_ALPHA_INTERPOLATION, switch (this) {
      case QUALITY -> VALUE_ALPHA_INTERPOLATION_QUALITY;
      case SPEED -> VALUE_ALPHA_INTERPOLATION_SPEED;
      default -> VALUE_ALPHA_INTERPOLATION_DEFAULT;
    });
  }
}
