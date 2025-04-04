package dev.gamekit.ui.widgets;

import dev.gamekit.ui.enums.Alignment;

/** An {@link Align} which centers its single child */
public class Center extends Align {
  protected Center(Widget child) {
    super(Alignment.CENTER, child);
  }

  public static Center create(Widget child) {
    return new Center(child);
  }
}
