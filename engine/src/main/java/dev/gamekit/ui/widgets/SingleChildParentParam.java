package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;

public class SingleChildParentParam extends ParentParam {
  public static Param<SingleChildParentParam> child(Widget value) {
    return new Param<>("child", value);
  }
}
