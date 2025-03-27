package dev.gamekit.ui.widgets;

import dev.gamekit.ui.enums.Alignment;

/** A {@link Parent} which centers its single child */
public class Center extends Align {
  public Center(Widget child) {
    super(child, Alignment.CENTER);
  }
}
