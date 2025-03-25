package dev.gamekit.ui;

import dev.gamekit.ui.widgets.Widget;
import dev.gamekit.utils.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * An extension of {@link Observable} which stores a list of dependent widgets.
 * The dependent widgets are marked for layout and rendering when the value changes.
 * <p>
 * When a WidgetState is created, it binds to the current {@link dev.gamekit.core.Scene Scene},
 * which is notified when its value changes
 */
public class WidgetState<T> extends Observable<T> {
  public static final List<WidgetState<Object>> STATES_TO_BIND_TO_SCENE = new ArrayList<>();

  protected final List<Widget> dependents;
  protected final String debugName;

  public WidgetState(T value) {
    this(value, null);
  }

  @SuppressWarnings("unchecked")
  public WidgetState(T value, String debugName) {
    super(value);
    this.debugName = debugName;
    dependents = new ArrayList<>();

    STATES_TO_BIND_TO_SCENE.add((WidgetState<Object>) this);
  }

  /**
   * Binds a {@link Widget} to this state to be
   * re-rendered when the state value changes
   */
  public void bindWidget(Widget widget) {
    if (widget != null && !dependents.contains(widget)) {
      dependents.add(widget);
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(getClass().getName());
    if (debugName != null) {
      builder.append(String.format("[name=%s,value=%s,observers:%d]", debugName, value, observers.size()));
    } else builder.append(String.format("[value=%s,observers:%d]", value, observers.size()));
    return builder.toString();
  }
}
