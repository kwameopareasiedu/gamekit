package dev.gamekit.ui;

import dev.gamekit.ui.widgets.Widget;

import java.util.ArrayList;
import java.util.List;

/** Al observer UI state class which forms the basis UI reactivity in GameKit */
public class State<T> {
  protected final List<Observer<T>> observers;
  protected final List<Widget> widgets;
  protected T value;

  public State(T value) {
    this.value = value;
    observers = new ArrayList<>();
    widgets = new ArrayList<>();
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

  /** Binds a {@link Widget} to be repainted when the state value changes */
  public void bindNode(Widget widget) {
    if (widget != null && !widgets.contains(widget)) {
      widgets.add(widget);
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
