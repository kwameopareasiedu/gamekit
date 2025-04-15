package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;
import dev.gamekit.ui.Spacing;

import java.awt.image.BufferedImage;

public class PanelParam extends SingleChildParentParam {
  public static Param<PanelParam> background(BufferedImage value) {
    return new Param<>("background", value);
  }

  public static Param<PanelParam> spacing(Spacing value) {
    return new Param<>("spacing", value);
  }
}
