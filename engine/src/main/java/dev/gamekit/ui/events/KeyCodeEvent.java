package dev.gamekit.ui.events;

/** Event class for keyboard action key code events (E.g. Arrow keys) */
public class KeyCodeEvent extends InputEvent {
  public final int keyCode;

  public KeyCodeEvent(int keyCode) {
    this.keyCode = keyCode;
  }

  /** Handler interface for {@link KeyCodeEvent key code events} */
  public interface Handler extends InputEvent.Handler {
    /** Called to handle a {@link KeyCodeEvent} */
    void handleEvent(KeyCodeEvent ev);
  }
}
