package dev.gamekit.utils;

import dev.gamekit.annotations.WidgetBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** {@link WidgetField} holds type information for {@code Widgets} annotated with {@link WidgetBuilder} */
public class WidgetClass implements Serializable {
  /** Indicates an engine widget */
  public final boolean internal;
  /** E.g: For a widget {@code Field}, this is {@code dev.gamekit.ui.widgets.Field} */
  public final String typeName;
  /** E.g: For a widget {@code Field}, this is {@code Field} */
  public final String simpleTypeName;
  /** E.g: For a widget {@code Field}, this is {@code field} */
  public final String varName;
  /** E.g: For a widget {@code Field}, this is {@code dev.gamekit.ui.widgets.FieldConfig} */
  public final String configTypeName;
  /** E.g: For a widget {@code Field}, this is {@code FieldConfig} */
  public final String configSimpleTypeName;
  /** E.g: For a widget {@code Field}, this is {@code fieldConfig} */
  public final String configVarName;
  /** E.g: For a widget {@code Field}, this is {@code dev.gamekit.ui.widgets} */
  public final String configPackageName;
  public final List<WidgetField> fields;
  public final WidgetClass base;

  public WidgetClass(boolean internal, String typeName, List<WidgetField> fields, WidgetClass base) {
    this.internal = internal;
    this.typeName = typeName;
    this.fields = Collections.unmodifiableList(fields);
    this.base = base;

    simpleTypeName = typeName.substring(typeName.lastIndexOf(".") + 1);
    varName = simpleTypeName.substring(0, 1).toLowerCase() + simpleTypeName.substring(1);
    configTypeName = typeName + "Config";
    configSimpleTypeName = configTypeName.substring(configTypeName.lastIndexOf(".") + 1);
    configVarName = configSimpleTypeName.substring(0, 1).toLowerCase() + configSimpleTypeName.substring(1);
    configPackageName = configTypeName.substring(0, configTypeName.lastIndexOf("."));
  }

  public static void traverse(WidgetClass clazz, Visitor visitor) {
    if (clazz == null)
      return;

    visitor.visit(clazz);
    traverse(clazz.base, visitor);
  }

  @Override
  public String toString() {
    return String.format(
      "WidgetClass[internal=%b, typeName=%s, simpleTypeName=%s, varName=%s, configTypeName=%s, " +
        "configSimpleTypeName=%s, configVarName=%s, configPackageName=%s, fields=%d]",
      internal,
      typeName,
      simpleTypeName,
      varName,
      configTypeName,
      configSimpleTypeName,
      configVarName,
      configPackageName,
      fields.size()
    );
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof WidgetClass other
      && Objects.equals(internal, other.internal)
      && Objects.equals(typeName, other.typeName)
      && Objects.equals(base, other.base);
  }

  @FunctionalInterface
  public interface Visitor {
    void visit(WidgetClass clazz);
  }
}
