package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;

public class ScaledParam extends SingleChildParentParam {
  public static Param<ScaledParam> scale(double value) {
    return new Param<>("scale", value);
  }
}
