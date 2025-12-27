package dev.gamekit.utils;

import dev.gamekit.annotations.WidgetBuilderField;

/** {@link WidgetField} holds type information for {@code Widget} fields annotated with {@link WidgetBuilderField} */
public final class WidgetField {
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code dev.gamekit.utils.Spacing} */
  public final String typeName;
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code edgeInsets} */
  public final String varName;
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code EdgeInsets} */
  public final String varNameAsSuffix;
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code new Spacing()} */
  public final String fallbackValue;
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code dev.gamekit.ui.widgets.Field} */
  public final String classTypeName;
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code Field} */
  public final String classSimpleTypeName;
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code field} */
  public final String classVarName;
  public final boolean comparable;
  public final boolean themable;

  public WidgetField(
    String typeName,
    String varName,
    String fallbackValue,
    String classTypeName,
    String classSimpleTypeName,
    String classVarName,
    boolean comparable, boolean themable
  ) {
    this.typeName = typeName;
    this.varName = varName;
    this.classTypeName = classTypeName;
    this.classSimpleTypeName = classSimpleTypeName;
    this.classVarName = classVarName;
    this.fallbackValue = fallbackValue;
    this.comparable = comparable;
    this.themable = themable;

    varNameAsSuffix = varName.substring(0, 1).toUpperCase() + varName.substring(1);
  }

  @Override
  public String toString() {
    return "WidgetField{" +
      "\ntypeName='" + typeName + '\'' +
      "\nvarName='" + varName + '\'' +
      "\nnameAsSuffix='" + varNameAsSuffix + '\'' +
      "\nfallbackValue='" + fallbackValue + '\'' +
      "\nclassTypeName='" + classTypeName + '\'' +
      "\nclassSimpleTypeName='" + classSimpleTypeName + '\'' +
      "\nclassVarName='" + classVarName + '\'' +
      "\ncomparable='" + comparable + '\'' +
      "\nthemable='" + themable + '\'' +
      "\n}\n";
  }
}
