package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;
import dev.gamekit.ui.enums.Alignment;

public class AlignParam extends SingleChildParentParam {
  public static Param<AlignParam> horizontalAlignment(Alignment value) {
    return new Param<>("horizontalAlignment", value);
  }

  public static Param<AlignParam> verticalAlignment(Alignment value) {
    return new Param<>("verticalAlignment", value);
  }
}
