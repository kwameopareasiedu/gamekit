package dev.gamekit.processors;

import dev.gamekit.annotations.WidgetBuilderField;

/**
 * {@link WidgetField} holds type information for {@code Widget} fields annotated with
 * {@link WidgetBuilderField}
 */
public record WidgetField(String type, String name, String value) { }
