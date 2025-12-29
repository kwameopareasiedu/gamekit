package dev.gamekit.utils;

import dev.gamekit.annotations.WidgetBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/** {@link WidgetField} holds type information for {@code Widgets} annotated with {@link WidgetBuilder} */
public class WidgetClass implements Serializable {
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
  /** E.g: For a widget {@code Field}, this is {@code dev.gamekit.ui.widgets.Text} */
  public final String superClassTypeName;
  /** E.g: For a widget {@code Field}, this is {@code dev.gamekit.ui.widgets.TextConfig} */
  public final String superClassConfigTypeName;
  public final List<WidgetField> fields;

  public WidgetClass(String typeName, String superClassTypeName, List<WidgetField> fields) {
    this.typeName = typeName;
    this.superClassTypeName = superClassTypeName;
    this.fields = fields;

    simpleTypeName = typeName.substring(typeName.lastIndexOf(".") + 1);
    varName = simpleTypeName.substring(0, 1).toLowerCase() + simpleTypeName.substring(1);
    configTypeName = typeName + "Config";
    configSimpleTypeName = configTypeName.substring(configTypeName.lastIndexOf(".") + 1);
    configVarName = configSimpleTypeName.substring(0, 1).toLowerCase() + configSimpleTypeName.substring(1);
    configPackageName = configTypeName.substring(0, configTypeName.lastIndexOf("."));

    superClassConfigTypeName = superClassTypeName != null ? superClassTypeName + "Config" : null;
  }

  @Override
  public String toString() {
    return "WidgetClass{" +
      "\ntypeName='" + typeName + '\'' +
      "\nsimpleTypeName='" + simpleTypeName + '\'' +
      "\nvarName='" + varName + '\'' +
      "\nconfigTypeName='" + configTypeName + '\'' +
      "\nconfigSimpleTypeName='" + configSimpleTypeName + '\'' +
      "\nconfigVarName='" + configVarName + '\'' +
      "\nconfigPackageName='" + configPackageName + '\'' +
      "\nsuperClassTypeName='" + superClassTypeName + '\'' +
      "\nsuperClassConfigTypeName='" + superClassConfigTypeName + '\'' +
      "\nfields=" + fields.size() +
      "\n}\n";
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof WidgetClass other
      && Objects.equals(typeName, other.typeName)
      && Objects.equals(simpleTypeName, other.simpleTypeName)
      && Objects.equals(varName, other.varName)
      && Objects.equals(configTypeName, other.configTypeName)
      && Objects.equals(configSimpleTypeName, other.configSimpleTypeName)
      && Objects.equals(configVarName, other.configVarName)
      && Objects.equals(configPackageName, other.configPackageName)
      && Objects.equals(superClassTypeName, other.superClassTypeName)
      && Objects.equals(superClassConfigTypeName, other.superClassConfigTypeName);
  }
}
