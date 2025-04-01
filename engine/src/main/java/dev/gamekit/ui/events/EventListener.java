package dev.gamekit.ui.events;

/** Callback interface for a {@link dev.gamekit.core.UI UI} event handler */
public interface EventListener<T extends InputEvent> {
  /** Called when an event is dispatched */
  void onEvent(T event);
}
