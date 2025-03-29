package dev.gamekit.ui.events;

import dev.gamekit.ui.widgets.Widget;

/** Base for mouse related events */
public class MouseEvent extends Event {
  public final int x;
  public final int y;

  public MouseEvent(Widget target, int x, int y) {
    super(target);
    this.x = x;
    this.y = y;
  }
}
