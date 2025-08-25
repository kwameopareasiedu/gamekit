package dev.gamekit.statemachine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link StateMachine} manages a finite set of {@link State states} and the transitions between
 * them
 */
public class StateMachine<K extends Enum<K>> {
  protected final Map<K, State<K>> stateMap = new HashMap<>();

  protected State<K> currentState;

  @SafeVarargs
  public StateMachine(State<K> initialState, State<K>... states) {
    if (initialState == null)
      throw new IllegalArgumentException("Initial state cannot be null");

    if (states.length == 0)
      throw new IllegalArgumentException("At least one state is required");

    if (Arrays.stream(states).noneMatch(state -> state == initialState))
      throw new IllegalArgumentException("Provided states must also contain the initial state");

    for (State<K> state : states)
      if (state == null)
        throw new IllegalArgumentException("State cannot be null");

    for (State<K> state : states)
      stateMap.put(state.key, state);

    currentState = initialState;
  }

  /**
   * Start should be called once to initialize the state machine. Overrides of this method should
   * call {@code super.start()} to initialize the initial state
   */
  public void start() {
    currentState.enter();
  }

  /** Runs the update logic of the current state and transitions to a new state if necessary */
  public void update() {
    K nextStateKey = currentState.getNextStateKey();

    if (nextStateKey != null && nextStateKey != currentState.key) {
      currentState.exit();
      currentState = stateMap.get(nextStateKey);
      currentState.enter();
    }

    currentState.update();
  }

  public void dispose() {
    currentState.exit();
  }
}
