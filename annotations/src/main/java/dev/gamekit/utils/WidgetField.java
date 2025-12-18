package dev.gamekit.utils;

import dev.gamekit.annotations.WidgetBuilderField;

/** {@link WidgetField} holds type information for {@code Widget} fields annotated with {@link WidgetBuilderField} */
public record WidgetField(
  String type, String setterType, String name, boolean comparable, boolean updatable, boolean themable
) { }
