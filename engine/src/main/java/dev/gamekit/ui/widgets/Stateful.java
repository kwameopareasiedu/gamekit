package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.ui.events.*;
import dev.gamekit.ui.mixins.WidgetUpdater;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;

import java.awt.*;
import java.util.Objects;

/**
 * A {@link SingleChildParent} which is an abstract base for creating custom widgets which maintain an internal state.
 * <p>
 * Subclasses must implement the {@link #createState()} method which returns a mutable state object which is
 * preserved across re-renders.
 */
@WidgetBuilder
public abstract class Stateful extends SingleChildParent
  implements MouseEvent.Handler, FocusEvent.Handler, KeyCharEvent.Handler, KeyCodeEvent.Handler {
  private State<? extends Stateful> state;

  protected Stateful() {
    super(new StatefulConfig(), Empty.create());
  }

  protected Stateful(StatefulConfig config) {
    super(config, Empty.create());
  }

  /** Returns the custom {@link State} object representing {@link Stateful} */
  protected abstract State<? extends Stateful> createState();

  @Override
  protected final void performInit() {
    if (state == null) {
      // State is created only once
      state = createState();
      state.setWidget(this);
      state.init();
    }

    super.performInit();
  }

  @Override
  protected final void performUpdate() {
    super.performUpdate();

    if (state != null)
      state.update();
  }

  @Override
  protected final void performLayout(Constraints constraints) {
    if (state != null) {
      state.layout(constraints);

      if (state.tree != null) {
        computedBounds.setSize(
          state.tree.computedBounds.width,
          state.tree.computedBounds.height
        );

        return;
      }
    }

    computedBounds.setSize(0, 0);
  }

  @Override
  protected final void performPostLayout() {
    super.performPostLayout();

    if (state != null)
      state.postLayout();
  }

  @Override
  protected void renderSelf(Graphics2D g) {
    super.renderSelf(g);

    if (state != null)
      state.render(g);
  }

  @Override
  public void handleEvent(FocusEvent ev) {
    if (state != null)
      state.handleEvent(ev);
  }

  @Override
  public void handleEvent(KeyCharEvent ev) {
    if (state != null)
      state.handleEvent(ev);
  }

  @Override
  public void handleEvent(KeyCodeEvent ev) {
    if (state != null)
      state.handleEvent(ev);
  }

  @Override
  public void handleEvent(MouseEvent ev) {
    if (state != null)
      state.handleEvent(ev);
  }

  @Override
  protected void performUnmount() {
    if (state != null)
      state.unmount();
  }

  /** {@link State} represents the mutable part of a {@link Stateful} widget */
  protected abstract static class State<T extends Stateful> implements WidgetUpdater {
    protected T widget;
    private Widget tree;

    /** @see Widget#init */
    private void init() {
      tree = build();

      if (tree != null) {
        tree.parent = widget;
        tree.init(widget.host);
      }

      performInit();
    }

    /** @see Widget#performInit */
    protected void performInit() { /* No-op */ }

    /** Called by the associated {@link Stateful} to update this {@link State state's} UI */
    private void update() {
      Bounds originalBounds = tree != null ? new Bounds(tree.computedBounds) : null;

      updateTree(
        widget.host, widget.constraints,
        this::getTree, this::build,
        this::setTree, widget.host::triggerRender
      );

      // If the size of the state's UI changes, trigger a tree update
      if (!Objects.equals(tree.computedBounds, originalBounds))
        widget.host.triggerUpdate();
    }

    /** Abstract {@link Widget} builder method which constructs the widget tree represented by this {@link State} */
    protected abstract Widget build();

    /** @see Widget#layout */
    private void layout(Constraints constraints) {
      if (tree != null)
        tree.layout(constraints);
    }

    /** @see Widget#postLayout */
    private void postLayout() {
      if (tree != null)
        tree.postLayout();
    }

    /** @see Widget#render */
    private void render(Graphics2D g) {
      if (tree != null)
        tree.render(g);
    }

    /**
     * @see MouseEvent.Handler#handleEvent
     * @see FocusEvent.Handler#handleEvent
     * @see KeyCharEvent.Handler#handleEvent
     * @see KeyCodeEvent.Handler#handleEvent
     */
    private void handleEvent(InputEvent ev) {
      if (tree != null) {
        if (ev instanceof MouseEvent mouseEvent && tree instanceof MouseEvent.Handler handler) {
          handler.handleEvent(mouseEvent);
        } else if (ev instanceof FocusEvent focusEvent && tree instanceof FocusEvent.Handler handler) {
          handler.handleEvent(focusEvent);
        } else if (ev instanceof KeyCharEvent keyCharEvent && tree instanceof KeyCharEvent.Handler handler) {
          handler.handleEvent(keyCharEvent);
        } else if (ev instanceof KeyCodeEvent keyCodeEvent && tree instanceof KeyCodeEvent.Handler handler) {
          handler.handleEvent(keyCodeEvent);
        }
      }
    }

    /** @see Widget#unmount */
    private void unmount() {
      performUnmount();
      tree.parent = null;
    }

    /** @see Widget#performUnmount */
    protected void performUnmount() { /* No-op */ }

    /** Triggers an update of the state's UI */
    protected final void updateState() {
      update();
    }

    /** Sets a reference to the associated {@link Stateful} widget */
    private void setWidget(Stateful widget) {
      //noinspection unchecked
      this.widget = (T) widget;
    }

    /** Returns a reference to the current tree */
    private Widget getTree() {
      return tree;
    }

    /** Updates the current tree */
    private void setTree(Widget tree) {
      this.tree = tree;
      tree.parent = widget;
    }
  }
}
