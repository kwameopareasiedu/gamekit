package dev.gamekit.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a value allows {@link Observer}s to bind to it and be notified of its value.
 * This behaviour forms the basis of reactivity in GameKit
 */
public class Observable<T> {
  protected final List<Observer<T>> observers;
  protected T value;

  public Observable(T value) {
    this.value = value;
    observers = new ArrayList<>();
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

  public void notifyObservers() {
    for (Observer<T> o : observers) {
      o.onChange(this);
    }
  }

  public interface Observer<T> {
    void onChange(Observable<T> state);
  }

  public interface ObjectStateSetter<T> {
    void setValue(T current);
  }
}
