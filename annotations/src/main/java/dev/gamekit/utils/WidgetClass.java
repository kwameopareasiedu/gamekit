package dev.gamekit.utils;

import dev.gamekit.annotations.WidgetBuilder;

import java.util.List;

/** {@link WidgetField} holds type information for {@code Widgets} annotated with {@link WidgetBuilder} */
public class WidgetClass {
  /** E.g: For a widget {@code Field}, this is {@code dev.gamekit.ui.widgets.Field} */
  public final String typeName;
  /** E.g: For a widget {@code Field}, this is {@code Field} */
  public final String simpleTypeName;
  /** E.g: For a widget {@code Field}, this is {@code field} */
  public final String varName;
  /** E.g: For a widget {@code Field}, this is {@code dev.gamekit.ui.widgets.FieldConfig} */
  public final String builderTypeName;
  /** E.g: For a widget {@code Field}, this is {@code FieldConfig} */
  public final String builderSimpleTypeName;
  /** E.g: For a widget {@code Field}, this is {@code fieldConfig} */
  public final String builderVarName;
  /** E.g: For a widget {@code Field}, this is {@code dev.gamekit.ui.widgets} */
  public final String builderPackageName;
  /** E.g: For a widget {@code Field}, this is {@code dev.gamekit.ui.widgets.Text} */
  public final String superClassTypeName;
  /** E.g: For a widget {@code Field}, this is {@code dev.gamekit.ui.widgets.TextConfig} */
  public final String superClassBuilderTypeName;
  public final List<WidgetField> fields;

  public WidgetClass(String typeName, String superClassTypeName, List<WidgetField> fields) {
    this.typeName = typeName;
    this.superClassTypeName = superClassTypeName;
    this.fields = fields;

    simpleTypeName = typeName.substring(typeName.lastIndexOf(".") + 1);
    varName = simpleTypeName.substring(0, 1).toLowerCase() + simpleTypeName.substring(1);
    builderTypeName = typeName + "Config";
    builderSimpleTypeName = builderTypeName.substring(builderTypeName.lastIndexOf(".") + 1);
    builderVarName = builderSimpleTypeName.substring(0, 1).toLowerCase() + builderSimpleTypeName.substring(1);
    builderPackageName = builderTypeName.substring(0, builderTypeName.lastIndexOf("."));

    superClassBuilderTypeName = superClassTypeName != null ? superClassTypeName + "Config" : null;
  }

  @Override
  public String toString() {
    return "WidgetClass{" +
      "\ntypeName='" + typeName + '\'' +
      "\nsimpleTypeName='" + simpleTypeName + '\'' +
      "\nvarName='" + varName + '\'' +
      "\nbuilderTypeName='" + builderTypeName + '\'' +
      "\nbuilderSimpleTypeName='" + builderSimpleTypeName + '\'' +
      "\nbuilderVarName='" + builderVarName + '\'' +
      "\nbuilderPackageName='" + builderPackageName + '\'' +
      "\nsuperClassTypeName='" + superClassTypeName + '\'' +
      "\nsuperClassBuilderTypeName='" + superClassBuilderTypeName + '\'' +
      "\nfields=" + fields.size() +
      "\n}\n";
  }
}
