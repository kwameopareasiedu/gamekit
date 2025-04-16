package dev.gamekit.ui.events;

import dev.gamekit.core.UI;
import dev.gamekit.ui.widgets.Widget;

/**
 * Interface for {@link Widget Widgets} which can process
 * {@link InputEvent InputEvents}
 */
public interface InputEventHandler {
  MouseEvent.Listener getMouseListener();

  void setMouseEntered(boolean mouseEntered);

  void setMousePressed(boolean mouseEntered);

  /** Called when the implementor receives an {@link InputEvent} */
  default void handleEvent(InputEvent event) {
    MouseEvent.Listener mouseListener = getMouseListener();

    if (event instanceof MouseEvent mouseEvent && mouseListener != null) {
      switch (mouseEvent.type) {
        case ENTER -> {
          setMouseEntered(true);
          UI.getInstance().triggerRender();
        }
        case DOWN -> {
          setMousePressed(true);
          UI.getInstance().triggerRender();
        }
        case RELEASE -> {
          setMousePressed(false);
          UI.getInstance().triggerRender();
        }
        case EXIT -> {
          setMouseEntered(false);
          setMousePressed(false);
          UI.getInstance().triggerRender();
        }
      }

      mouseListener.handleEvent(mouseEvent);
    }
  }
}
