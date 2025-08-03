package dev.gamekit.ui.events;

import dev.gamekit.ui.widgets.Widget;

/** Event class for value changes in {@link Widget widgets} */
public class ChangeEvent<T> extends InputEvent {
  public final T value;

  public ChangeEvent(T value) {
    this.value = value;
  }

  /** Handler interface for {@link ChangeEvent mouse events} */
  public interface Handler<T> extends InputEvent.Handler {
    void handleEvent(ChangeEvent<T> ev);
  }
}
