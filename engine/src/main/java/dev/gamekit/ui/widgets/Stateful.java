package dev.gamekit.ui.widgets;

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
public abstract class Stateful extends SingleChildParent {
  private static final Map<String, State<Stateful>> STATES = new HashMap<>();

  private final String stateKey;
  private boolean updatedChild = false;
  private State<Stateful> state;

  protected Stateful(String key, String stateKey) {
    super(key, ignored -> { }, Empty.create());
    this.stateKey = stateKey;
  }

  protected Stateful(String stateKey) {
    super(null, ignored -> { }, Empty.create());
    this.stateKey = stateKey;
  }

  /** Returns the custom {@link State} object representing {@link Stateful} */
  protected abstract State<? extends Stateful> createState();

  @SuppressWarnings("unchecked")
  @Override
  protected final void performInit() {
    if (state == null) {
      // State is created only once
      if (STATES.containsKey(stateKey)) {
        state = STATES.get(stateKey);
      } else {
        state = (State<Stateful>) createState();
        STATES.put(stateKey, state);
      }
    }

    if (!updatedChild) {
      updateChild(state.build(this));
      updatedChild = true;
    }

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
    STATES.remove(stateKey);
  }

  /**
   * {@link State} represents the mutable part of a {@link Stateful} widget.
   * <p>
   * <strong>NB: Subclasses must be either be standalone classes or STATIC inner classes to work properly</strong>
   */
  protected abstract static class State<T extends Stateful> {
    /** Abstract {@link Widget} builder method which constructs the widget tree represented by this {@link State} */
    protected abstract Widget build(T widget);
  }
}
