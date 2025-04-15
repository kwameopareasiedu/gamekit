package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;

public class SizedParam extends SingleChildParentParam {
  public static Param<SizedParam> width(int value) {
    return new Param<>("width", value);
  }

  public static Param<SizedParam> height(int value) {
    return new Param<>("height", value);
  }
}
