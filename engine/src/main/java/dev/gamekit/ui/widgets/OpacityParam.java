package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;

public class OpacityParam extends SingleChildParentParam {
  public static Param<OpacityParam> opacity(double value) {
    return new Param<>("opacity", value);
  }
}
