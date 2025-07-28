package dev.gamekit.ui.events;

import dev.gamekit.ui.widgets.Widget;

/** Event class for value changes in {@link Widget widgets} */
public class ChangeEvent extends InputEvent {
  public final String value;

  public ChangeEvent(String value) {
    this.value = value;
  }

  /** Handler interface for {@link ChangeEvent mouse events} */
  public interface Handler extends InputEvent.Handler {
    void handleEvent(ChangeEvent ev);
  }
}
