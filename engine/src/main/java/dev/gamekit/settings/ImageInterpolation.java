package dev.gamekit.settings;

import java.awt.*;

import static java.awt.RenderingHints.*;

public enum ImageInterpolation implements Setting {
  BICUBIC, BILINEAR, NEAREST;

  static ImageInterpolation from(Graphics2D g) {
    Object hint = g.getRenderingHint(KEY_INTERPOLATION);
    if (hint == VALUE_INTERPOLATION_BICUBIC) return BICUBIC;
    if (hint == VALUE_INTERPOLATION_BILINEAR) return BILINEAR;
    return NEAREST;
  }

  public void apply(Graphics2D g) {
    g.setRenderingHint(KEY_INTERPOLATION, switch (this) {
      case BICUBIC -> VALUE_INTERPOLATION_BICUBIC;
      case BILINEAR -> VALUE_INTERPOLATION_BILINEAR;
      default -> VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
    });
  }
}
