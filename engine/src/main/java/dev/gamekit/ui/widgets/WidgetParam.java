package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;
import dev.gamekit.ui.events.MouseEvent;

public class WidgetParam {
  public static Param<WidgetParam> mouseListener(MouseEvent.Listener value) {
    return new Param<>("mouseListener", value);
  }
}
