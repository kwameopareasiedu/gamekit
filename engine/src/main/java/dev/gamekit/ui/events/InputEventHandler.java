package dev.gamekit.ui.events;

import dev.gamekit.ui.widgets.Widget;

/** Interface for {@link Widget Widgets} which can process {@link InputEvent InputEvents} */
public interface InputEventHandler {
  /** Called when the implementor receives an {@link InputEvent} */
  void handleEvent(InputEvent event);
}
