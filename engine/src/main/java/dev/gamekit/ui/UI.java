package dev.gamekit.ui;

import dev.gamekit.core.Renderer;
import dev.gamekit.core.Window;
import dev.gamekit.utils.Constraints;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages UI handling within a {@link dev.gamekit.core.Scene Scene}.
 * A {@link dev.gamekit.core.Scene Scene} must only contain 1 instance of this
 */
public class UI implements State.Observer<Object> {
  private static final Logger LOGGER = LogManager.getLogger();

  private Constraints windowConstraints;
  private Node root;
  private boolean invalidated = true;

  public UI() {
    Window win = Window.getInstance();

    windowConstraints = new Constraints(
      win.getRenderWidth(),
      win.getRenderWidth(),
      win.getRenderHeight(),
      win.getRenderHeight()
    );
  }

  @SuppressWarnings("unchecked")
  public <T> State<T> createState(T val) {
    State<T> state = new State<>(val);
    state.bindObserver((State.Observer<T>) this);
    return state;
  }

  public void setRoot(Node root) {
    this.root = root;

    Window win = Window.getInstance();

    windowConstraints = windowConstraints.update(
      win.getRenderWidth(),
      win.getRenderWidth(),
      win.getRenderHeight(),
      win.getRenderHeight()
    );

    this.root.computeLayout(windowConstraints);
    this.root.computedPosition.set(0, 0);
  }

  public void onRender() {
    if (invalidated) {
      LOGGER.debug("Beginning UI render");
      Renderer.clearUI();
      Renderer.drawUI(root);
      invalidated = false;
      LOGGER.debug("Finished UI render");
    }
  }

  @Override
  public void onChange(State<Object> state) {
  }
}
