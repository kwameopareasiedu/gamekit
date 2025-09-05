package dev.gamekit.tools.state.machine;

/**
 * A state represents a behaviour in a state machine. Subclasses should extend to represent
 * concrete behaviours
 */
public abstract class FiniteState<K extends Enum<K>> {
  public final K key;

  protected FiniteState(K key) {
    this.key = key;
  }

  /** Called to (re)initialize the state when it becomes the current state */
  protected void enter() { /* No-op */ }

  /** Called to run the update logic of the state */
  protected void update() { /* No-op */ }

  /**
   * Called to compute the key for the next state
   * <p>
   * If the returned key is {@code null} or the same as this state, no transition occurs
   */
  public abstract K getNextStateKey();

  /**
   * Called to run cleanup logic for the state when the state machine is transitioning to
   * another state
   */
  protected void exit() { /* No-op */ }
}
