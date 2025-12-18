package dev.gamekit.utils;

import dev.gamekit.annotations.WidgetBuilder;

import java.util.List;

/** {@link WidgetField} holds type information for {@code Widgets} annotated with {@link WidgetBuilder} */
public class WidgetClass {
  public final String name;
  public final String simpleName;
  public final String builderName;
  public final String builderVarName;
  public final String builderSimpleName;
  public final String builderPackageName;
  public final String superBuilderName;
  public final List<WidgetField> allFields;
  public final List<WidgetField> ownFields;

  public WidgetClass(
    String name,
    String superName,
    List<WidgetField> allFields,
    List<WidgetField> ownFields
  ) {
    this.name = name;
    this.allFields = allFields;
    this.ownFields = ownFields;

    simpleName = name.substring(name.lastIndexOf(".") + 1);
    builderName = name + "Config";
    builderPackageName = builderName.substring(0, builderName.lastIndexOf("."));
    builderSimpleName = builderName.substring(builderName.lastIndexOf(".") + 1);
    builderVarName = builderSimpleName.substring(0, 1).toLowerCase() + builderSimpleName.substring(1);
    superBuilderName = superName != null ? superName + "Config" : null;
  }

  @Override
  public String toString() {
    return "WidgetClass{" +
      "\nbuilderName='" + builderName + '\'' +
      "\nbuilderVarName='" + builderVarName + '\'' +
      "\nbuilderSimpleName='" + builderSimpleName + '\'' +
      "\nbuilderPackageName='" + builderPackageName + '\'' +
      "\nsuperBuilderName='" + superBuilderName + '\'' +
      "\nallFields=" + allFields.size() +
      "\nownFields=" + ownFields.size() +
      "\n}\n";
  }
}
