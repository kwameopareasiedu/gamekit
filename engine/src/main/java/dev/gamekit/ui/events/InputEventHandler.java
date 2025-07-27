package dev.gamekit.ui.events;

import dev.gamekit.core.UI;
import dev.gamekit.ui.widgets.Widget;

/** Interface for {@link Widget Widgets} which can process {@link InputEvent InputEvents} */
public interface InputEventHandler {
  UI.BridgeObject getUiBridge();

  default MouseEvent.Listener getMouseListener() {
    return null;
  }

  default KeyCharEvent.Listener getKeyCharListener() {
    return null;
  }

  default void setMouseEntered(boolean mouseEntered) { /* No-op */ }

  default void setMousePressed(boolean mouseEntered) { /* No-op */ }

  /** Called when the implementor receives an {@link InputEvent} */
  default void handleEvent(InputEvent event) {
    MouseEvent.Listener mouseListener = getMouseListener();
    KeyCharEvent.Listener keyCharListener = getKeyCharListener();
    UI.BridgeObject uiBridge = getUiBridge();

    if (event instanceof MouseEvent mouseEvent && mouseListener != null) {
      switch (mouseEvent.type) {
        case ENTER -> {
          setMouseEntered(true);
          uiBridge.triggerRender();
        }
        case DOWN -> {
          setMousePressed(true);
          uiBridge.triggerRender();
        }
        case RELEASE -> {
          setMousePressed(false);
          uiBridge.triggerRender();
        }
        case EXIT -> {
          setMouseEntered(false);
          setMousePressed(false);
          uiBridge.triggerRender();
        }
      }

      mouseListener.handleEvent(mouseEvent);
    } else if (event instanceof KeyCharEvent keyCharEvent && keyCharListener != null) {
      keyCharListener.handleEvent(keyCharEvent);
    }
  }
}
