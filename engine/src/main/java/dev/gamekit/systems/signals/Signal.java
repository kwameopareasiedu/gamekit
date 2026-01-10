package dev.gamekit.systems.signals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * {@link Signal} is an event dispatcher for the specified type {@code T}.
 * <p>
 * Rather than implementing specific subscriber patterns for classes, {@link Signal} provides a useful abstraction,
 * allowing event listeners to subscribe/unsubscribe directly to it.
 */
public class Signal<T> {
  private final Map<String, Subscription<T>> subscribers;
  private boolean open;
  private T value;

  public Signal() {
    subscribers = new HashMap<>();
    value = null;
    open = true;
  }

  /** Returns {@code true} if this {@link Signal} is open, or {@code false} otherwise */
  public boolean isOpen() {
    return open;
  }

  /**
   * Registers a {@link Subscriber} to be notified when the signal value changes
   * <p>
   * If {@code notifyImmediately} is set, the subscriber will immediately be notified of the last value of this
   * {@link Signal}
   */
  public void subscribe(String key, Subscriber<T> subscriber, boolean notifyImmediately) {
    subscribe(key, subscriber, false, notifyImmediately);
  }

  /**
   * Registers a {@link Subscriber} to be notified when the signal value changes and subsequently removed
   * <p>
   * If {@code notifyImmediately} is set, the subscriber will immediately be notified of the last value of this
   * {@link Signal}
   */
  public void subscribeOnce(String key, Subscriber<T> subscriber, boolean notifyImmediately) {
    subscribe(key, subscriber, true, notifyImmediately);
  }

  /** Dispatches the provided value to all subscribers */
  public void emit(T value) {
    if (!open) throw new IllegalStateException("Attempting to emit from a closed signal");

    Iterator<Subscription<T>> iterator = subscribers.values().iterator();

    while (iterator.hasNext()) {
      Subscription<T> subscription = iterator.next();

      subscription.notifySubscriber(value);

      if (subscription.once) iterator.remove();
    }

    this.value = value;
  }

  /**
   * Removes all subscribers and closes this signal
   * <p>
   * After this method is called, further calls to {@link #emit} will throw an {@link IllegalStateException}
   */
  public void dispose() {
    Iterator<Subscription<T>> iterator = subscribers.values().iterator();

    while (iterator.hasNext())
      iterator.remove();

    open = false;
  }

  /**
   * Registers a {@link Subscriber} to be notified when the signal value changes.
   * <p>
   * If {@code notifyImmediately} is set, the subscriber will immediately be notified of the last value of this
   * {@link Signal}
   * <p>
   * NB: <i>The provided {@code subscriber} will overwrite an existing subscriber with the same {@code key}</i>
   */
  private void subscribe(String key, Subscriber<T> subscriber, boolean once, boolean notifyImmediately) {
    if (!open) throw new IllegalStateException("Attempting to subscribe to a closed signal");

    Subscription<T> subscription = new Subscription<>(subscriber, once);

    if (notifyImmediately) subscription.notifySubscriber(value);

    subscribers.put(key, subscription);
  }

  /** Handler interface for {@link Signal} data changes */
  @FunctionalInterface
  public interface Subscriber<T> {
    /** Called with the updated signal value */
    void onNotified(T value);
  }

  /** Internal wrapper class for {@link Subscriber} interface, with additional metadata */
  record Subscription<T>(Subscriber<T> subscriber, boolean once) {
    void notifySubscriber(T value) {
      subscriber.onNotified(value);
    }
  }
}
