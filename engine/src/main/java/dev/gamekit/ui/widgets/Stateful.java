package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.utils.Constraints;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link SingleChildParent} which is an abstract base for creating custom widgets by composing other widgets
 * <p>
 * Unlike {@link Compose}, this widget allows the custom widgets to maintain their own internal state which
 * is preserved between re-renders.
 * <p>
 * Subclasses must override the {@link #createState()} method and return a {@link State} object which manages said
 * internal state
 */
@WidgetBuilder
public abstract class Stateful extends SingleChildParent {
  private static final Map<String, State> STATES = new HashMap<>();

  private final String stateKey;
  private State state;

  protected Stateful(String stateKey) {
    super(new StatefulConfig(), Empty.create());
    this.stateKey = stateKey;
  }

  protected Stateful(StatefulConfig config, String stateKey) {
    super(config, Empty.create());
    this.stateKey = stateKey;
  }

  /** Returns the custom {@link State} object representing {@link Stateful} */
  protected abstract State createState();

  @Override
  protected final void performInit() {
    if (state == null) {
      // State is created only once
      if (STATES.containsKey(stateKey)) {
        state = STATES.get(stateKey);
      } else {
        state = createState();
        STATES.put(stateKey, state);
      }
    }

    updateChild(state.build());
    super.performInit();
  }

  @Override
  protected final void performLayout(Constraints constraints) {
    child.layout(constraints);

    computedBounds.setSize(
      child.computedBounds.width,
      child.computedBounds.height
    );

    child.computedBounds.setPosition(0, 0);
  }

  @Override
  protected void performUnmount() {
    super.performUnmount();
//    STATES.remove(stateKey);
  }

  /** Triggers an update of the widget tree in response to some state change */
  protected void updateUI() {
    host.triggerUpdate();
  }

  /** {@link State} represents the mutable part of a {@link Stateful} widget */
  protected abstract static class State implements Updater, Traveller {
    /** Abstract {@link Widget} builder method which constructs the widget tree represented by this {@link State} */
    protected abstract Widget build();
  }
}
