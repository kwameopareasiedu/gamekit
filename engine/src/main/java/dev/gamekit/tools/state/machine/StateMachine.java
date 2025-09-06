package dev.gamekit.tools.state.machine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link StateMachine} manages a finite set of {@link FiniteState states} and the transitions
 * between them
 */
public class StateMachine<K extends Enum<K>> {
  public static boolean DEBUG = false;

  protected final Logger logger = LogManager.getLogger(getClass());
  protected final Map<K, FiniteState<K>> stateMap = new HashMap<>();
  protected FiniteState<K> currentState;

  @SafeVarargs
  public StateMachine(FiniteState<K>... states) {
    if (states.length == 0)
      throw new IllegalArgumentException("At least one state is required");

    for (FiniteState<K> state : states)
      if (state == null)
        throw new IllegalArgumentException("State cannot be null");

    for (FiniteState<K> state : states)
      stateMap.put(state.key, state);

    currentState = states[0];
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
      if (DEBUG)
        logger.debug("Exiting {}", currentState.getClass().getName());

      currentState.exit();
      currentState = stateMap.get(nextStateKey);
      currentState.enter();

      if (DEBUG)
        logger.debug("Entering {}", currentState.getClass().getName());
    }

    currentState.update();
  }

  public void dispose() {
    currentState.exit();
  }
}
