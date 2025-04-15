package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;

public class FlexParam extends MultiChildParentParam {
  public static Param<FlexParam> gapSize(int value) {
    return new Param<>("gapSize", value);
  }

  public static Param<FlexParam> mainAxisAlignment(MainAxisAlignment value) {
    return new Param<>("mainAxisAlignment", value);
  }

  public static Param<FlexParam> crossAxisAlignment(CrossAxisAlignment value) {
    return new Param<>("crossAxisAlignment", value);
  }
}
