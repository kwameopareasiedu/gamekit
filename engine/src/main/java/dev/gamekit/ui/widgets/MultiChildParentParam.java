package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;

public class MultiChildParentParam extends ParentParam {
  public static Param<MultiChildParentParam> children(Widget... value) {
    return new Param<>("children", value);
  }
}
