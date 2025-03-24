package dev.gamekit.ui;

import dev.gamekit.core.Renderer;
import dev.gamekit.core.Window;
import dev.gamekit.utils.Constraints;

/**
 * Manages UI handling within a {@link dev.gamekit.core.Scene Scene}.
 * A {@link dev.gamekit.core.Scene Scene} must only contain 1 instance of this
 */
public class UI {
  private Constraints windowConstraints;
  private Node root;

  public UI() {
    Window win = Window.getInstance();

    windowConstraints = new Constraints(
      win.getRenderWidth(),
      win.getRenderWidth(),
      win.getRenderHeight(),
      win.getRenderHeight()
    );
  }

  public void setRoot(Node root) { this.root = root; }

  public void onUpdate() {
    if (root != null) {
      Window win = Window.getInstance();
      windowConstraints = windowConstraints.update(
        win.getRenderWidth(),
        win.getRenderWidth(),
        win.getRenderHeight(),
        win.getRenderHeight()
      );
      root.onLayout(windowConstraints);
      root.computedPosition.set(0, 0);
    }
  }

  public void onRender() {
    if (root != null) {
      Renderer.drawNode(root);
    }
  }
}
