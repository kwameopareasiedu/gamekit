package dev.gamekit.ui;

import java.util.ArrayList;
import java.util.List;

/** Al observer UI state class which forms the basis UI reactivity in GameKit */
public class State<T> {
  protected final List<Observer<T>> observers;
  protected final List<Node> nodes;
  protected T value;

  public State(T value) {
    this.value = value;
    observers = new ArrayList<>();
    nodes = new ArrayList<>();
  }

  public void set(T value) {
    this.value = value;
    notifyObservers();
  }

  public void set(ObjectStateSetter<T> setter) {
    setter.setValue(value);
    notifyObservers();
  }

  public T get() { return value; }

  /** Binds an {@link Observer} to be notified of changes to the value */
  public void bindObserver(Observer<T> observer) {
    if (observer != null && !observers.contains(observer)) {
      observers.add(observer);
    }
  }

  /** Binds a {@link Node} to be repainted when the state value changes */
  public void bindNode(Node node) {
    if (node != null && !nodes.contains(node)) {
      nodes.add(node);
    }
  }

  public void notifyObservers() {
    observers.forEach(o -> o.onChange(this));
  }

  public interface Observer<T> {
    void onChange(State<T> state);
  }

  public interface ObjectStateSetter<T> {
    void setValue(T current);
  }
}
