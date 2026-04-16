package dev.gamekit.signals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

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
   * Registers a {@link Subscriber} to be notified when the signal value changes.
   * <p>
   * If {@code notifyImmediately} is set, the subscriber will immediately be notified of the last value of this
   * {@link Signal}
   *
   * @return The subscription instance which can later be used to unsubscribe
   */
  public Subscription<T> subscribe(Subscriber<T> subscriber, boolean notifyImmediately) {
    return subscribe(subscriber, false, notifyImmediately);
  }

  /**
   * Registers a {@link Subscriber} to be notified when the signal value changes <strong>only once</strong>
   * <p>
   * If {@code notifyImmediately} is set, the subscriber will immediately be notified of the last value of this
   * {@link Signal}
   *
   * @return The subscription instance which can later be used to unsubscribe
   */
  public Subscription<T> subscribeOnce(Subscriber<T> subscriber, boolean notifyImmediately) {
    return subscribe(subscriber, true, notifyImmediately);
  }

  /** Removes the {@link Subscriber} with the associated {@code subscription} from this signal */
  public void unsubscribe(Subscription<T> subscription) {
    subscribers.remove(subscription.id);
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
   */
  private Subscription<T> subscribe(Subscriber<T> subscriber, boolean once, boolean notifyImmediately) {
    if (!open) throw new IllegalStateException("Attempting to subscribe to a closed signal");

    Subscription<T> subscription = new Subscription<>(this, subscriber, once);

    if (notifyImmediately) subscription.notifySubscriber(value);

    subscribers.put(subscription.id, subscription);

    return subscription;
  }

  /** Handler interface for {@link Signal} data changes */
  @FunctionalInterface
  public interface Subscriber<T> {
    /** Called with the updated signal value */
    void onNotified(T value);
  }

  /** Internal wrapper class for {@link Subscriber} interface, with additional metadata */
  public static final class Subscription<T> {
    private final String id;
    private final Signal<T> signal;
    private final Subscriber<T> subscriber;
    private final boolean once;

    private Subscription(Signal<T> signal, Subscriber<T> subscriber, boolean once) {
      this.id = UUID.randomUUID().toString();
      this.signal = signal;
      this.subscriber = subscriber;
      this.once = once;
    }

    void notifySubscriber(T value) {
      subscriber.onNotified(value);
    }

    /** Removes this {@link Subscription} from its associated {@link Signal} */
    public void unsubscribe() {
      signal.unsubscribe(this);
    }
  }
}
