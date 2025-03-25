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
    boolean notify = value != this.value;
    this.value = value;
    if (notify) notifyObservers();
  }

  /**
   * Sets the value by passing the current value through an updater
   * interface method. This is useful for updating object values
   * @param setter The updater object
   * @param notify Whether to notify listeners
   */
  public void set(ObjectValueUpdater<T> setter, boolean notify) {
    setter.setValue(value);
    if (notify) notifyObservers();
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

  /**
   * Interface for an observer which is notified
   * when an observable is updated
   */
  public interface Observer<T> {
    void onChange(Observable<T> observable);
  }

  public interface ObjectValueUpdater<T> {
    void setValue(T current);
  }
}
