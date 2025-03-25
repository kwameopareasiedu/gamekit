package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Alignment;

/** A container which centers its single child */
public class Center extends Align {
  public Center(Widget child) {
    super(child, Alignment.CENTER);
  }
}
