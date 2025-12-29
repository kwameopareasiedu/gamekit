package dev.gamekit.utils;

import dev.gamekit.annotations.WidgetBuilderField;

import java.io.Serializable;

/** {@link WidgetField} holds type information for {@code Widget} fields annotated with {@link WidgetBuilderField} */
public final class WidgetField implements Serializable {
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code dev.gamekit.utils.Spacing} */
  public final String typeName;
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code edgeInsets} */
  public final String varName;
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code EdgeInsets} */
  public final String varNameAsSuffix;
  /** E.g: For a field {@code Spacing Field#edgeInsets = new Spacing()}, this is {@code new Spacing()} */
  public final String fallbackValue;
  public final boolean comparable;
  public final boolean themable;

  public WidgetField(String typeName, String varName, String fallbackValue, boolean comparable, boolean themable) {
    this.typeName = typeName;
    this.varName = varName;
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
      "\ncomparable='" + comparable + '\'' +
      "\nthemable='" + themable + '\'' +
      "\n}\n";
  }
}
