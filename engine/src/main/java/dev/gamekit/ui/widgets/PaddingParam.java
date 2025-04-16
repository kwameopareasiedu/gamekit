package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;
import dev.gamekit.ui.Spacing;

public class PaddingParam extends SingleChildParentParam {
  public static Param<PaddingParam> padding(Spacing value) {
    return new Param<>("padding", value);
  }
}
