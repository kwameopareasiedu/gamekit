package dev.gamekit.ui;

import dev.gamekit.ui.widgets.Widget;

import java.util.Objects;

/** Represents a key value property pair for a {@link Widget} */
@SuppressWarnings("unused")
public record Param<T>(String name, Object value) {
  @SuppressWarnings("unchecked")
  public static <T> T getValue(Param<?>[] params, String name, T defaultValue) {
    for (Param<?> param : params)
      if (Objects.equals(param.name, name))
        return (T) param.value;

    return defaultValue;
  }
}
