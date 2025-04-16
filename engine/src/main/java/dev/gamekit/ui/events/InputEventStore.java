package dev.gamekit.ui.events;

import java.util.ArrayList;
import java.util.List;

/** Convenience class which stores structures of {@link InputEvent} */
public class InputEventStore {
  public final List<MouseEvent> mouseDownEvents;
  public final List<MouseEvent> mousePressEvents;
  public final List<MouseEvent> mouseReleaseEvents;
  public final List<MouseEvent> mouseClickEvents;
  public MouseEvent mouseMotionEvent;
  public MouseEvent mouseEnterEvent;
  public MouseEvent mouseExitEvent;

  public InputEventStore() {
    mouseDownEvents = new ArrayList<>();
    mousePressEvents = new ArrayList<>();
    mouseReleaseEvents = new ArrayList<>();
    mouseClickEvents = new ArrayList<>();
  }

  public void clear() {
    mouseMotionEvent = null;
    mouseEnterEvent = null;
    mouseDownEvents.clear();
    mousePressEvents.clear();
    mouseReleaseEvents.clear();
    mouseClickEvents.clear();
    mouseExitEvent = null;
  }
}
