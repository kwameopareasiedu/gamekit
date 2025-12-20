package dev.gamekit.annotationprocessors;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.WidgetClass;
import dev.gamekit.utils.WidgetField;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("dev.gamekit.annotations.WidgetBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_18)
public class WidgetBuilderProcessor extends AbstractProcessor {
  private static final String WIDGET_CLASS_NAME = "dev.gamekit.ui.widgets.Widget";
  private static final String THEME_CLASS_NAME = "dev.gamekit.ui.widgets.Theme";
  Types TypeUtils;
  Elements ElementUtils;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    TypeUtils = processingEnv.getTypeUtils();
    ElementUtils = processingEnv.getElementUtils();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      Set<? extends Element> annotatedClasses = roundEnv.getElementsAnnotatedWith(annotation);
      List<WidgetClass> widgetClasses = new ArrayList<>();


      for (Element annotatedClassElement : annotatedClasses) {
        if (!elementIsWidget(annotatedClassElement)) {
          throw new IllegalStateException("WidgetBuilder annotation can only be used on Widget classes");
        }

        WidgetClass widgetClass = extractWidgetClassData(annotatedClassElement);
        widgetClasses.add(widgetClass);

        generateWidgetConfigSource(widgetClass);
      }

      // Create widget config source files
//      for (WidgetClass clazz : widgetClasses) {
//        try {
//          JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(clazz.builderTypeName);
//
//          try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
//            // Builder package declaration
//            out.printf("package %s;\n\n", clazz.builderPackageName);
//
//            // Builder class declaration
//            if (clazz.superClassBuilderTypeName != null) {
//              out.printf("public class %s extends %s implements Widget.Config {\n",
//                clazz.builderSimpleTypeName, clazz.superClassBuilderTypeName);
//            } else {
//              out.printf("public class %s implements Widget.Config {\n",
//                clazz.builderSimpleTypeName);
//            }
//
//            // Builder instance fields declaration
//            for (WidgetField field : clazz.fields) {
//              out.printf("\tpublic %s %s;\n", field.typeName, field.varName);
//            }
//
//            out.println();
//
//            // Builder static setters declaration (widget own fields)
//            for (WidgetField field : clazz.fields) {
//              out.printf("\tpublic static %s %s(%s %s) {\n",
//                clazz.builderTypeName, field.varName, field.setterTypeName, field.varName);
//              out.printf("\t\t%s config = new %s();\n", clazz.builderTypeName, clazz.builderTypeName);
//              out.printf("\t\tconfig.%s = %s;\n", field.varName, field.varName);
//              out.printf("\t\treturn config;\n");
//              out.printf("\t}\n\n");
//            }
//
//            // Builder static setters declaration (widget superclass fields)
//            for (WidgetClass superClass : clazz.ancestry) {
//              for (WidgetField superField : superClass.fields) {
//                out.printf("\tpublic static %s %s(%s %s) {\n",
//                  clazz.builderTypeName, superField.varName, superField.setterTypeName, superField.varName);
//                out.printf("\t\t%s config = new %s();\n", clazz.builderTypeName, clazz.builderTypeName);
//                out.printf("\t\tconfig.%s = %s;\n", superField.varName, superField.varName);
//                out.printf("\t\treturn config;\n");
//                out.printf("\t}\n\n");
//              }
//            }
//
//            // Builder mergeWith method override
//            // @formatter:off
//            out.printf("\t@Override\n");
//            out.printf("\tpublic Widget.Config mergeWith(Widget.Config[] configs) {\n");
//            out.printf("\t\t%s resolved = new %s();\n\n", clazz.builderTypeName, clazz.builderTypeName);
//            // @formatter:on
//
//            out.printf("\t\tfor (Widget.Config config : configs) {\n");
//            out.printf("\t\t\t%s %s = (%s) config;\n",
//              clazz.builderSimpleTypeName, clazz.builderVarName, clazz.builderTypeName);
//            for (WidgetField field : clazz.fields) {
//              out.printf("\t\t\tif (%s.%s != null) resolved.%s = %s.%s;\n",
//                clazz.builderVarName, field.varName, field.varName, clazz.builderVarName, field.varName);
//            }
//            for (WidgetClass superClass : clazz.ancestry) {
//              for (WidgetField superField : superClass.fields) {
//                out.printf("\t\t\tif (%s.%s != null) resolved.%s = %s.%s;\n",
//                  clazz.builderVarName, superField.varName, superField.varName, clazz.builderVarName,
//                  superField.varName);
//              }
//            }
//            out.printf("\t\t}\n\n");
//
//            out.printf("\t\treturn resolved;\n");
//            out.printf("\t}\n\n");
//
//            // Builder updateWidget method override
//            // @formatter:off
//            out.printf("\t@Override\n");
//            out.printf("\tpublic void updateWidget(dev.gamekit.ui.widgets.Widget widget) {\n");
//            out.printf("\t\t%s %sWidget = (%s) widget;\n", clazz.typeName, clazz.varName, clazz.typeName);
//            out.printf("\t\tdev.gamekit.ui.widgets.Theme nearestTheme = dev.gamekit.utils.Misc.coalesce(widget
//            .getAncestorOfType(dev.gamekit.ui.widgets.Theme.class), dev.gamekit.ui.widgets.Theme.DEFAULT);\n");
//            // @formatter:on
//
//            List<WidgetField> updatableFields = clazz.fields.stream().filter(field -> field.updatable).toList();
//
//            for (WidgetField field : updatableFields) {
//              if (field.themable) {
//                String nestingThemeVarName = !clazz.typeName.equals(THEME_CLASS_NAME) ?
//                  clazz.varName + field.nameAsSuffix : field.varName;
//
//                out.printf("\t\t%sWidget.%s = dev.gamekit.utils.Misc.coalesce(%s, nearestTheme.%s);\n",
//                  clazz.varName, field.varName, field.varName, nestingThemeVarName);
//              } else {
//                out.printf("\t\t%sWidget.%s = %s;\n", clazz.simpleTypeName.toLowerCase(), field.varName, field
//                .varName);
//              }
//            }
//
//            for (WidgetClass superClass : clazz.ancestry) {
//              List<WidgetField> updatableSuperFields =
//                superClass.fields.stream().filter(field -> field.updatable).toList();
//
//              for (WidgetField superField : updatableSuperFields) {
//                if (superField.themable) {
//                  String nestingThemeVarName = !superClass.typeName.equals(THEME_CLASS_NAME) ?
//                    superClass.varName + superField.nameAsSuffix : superField.varName;
//
//                  out.printf("\t\t%sWidget.%s = dev.gamekit.utils.Misc.coalesce(%s, nearestTheme.%s);\n",
//                    clazz.varName, superField.varName, superField.varName, nestingThemeVarName);
//                } else {
//                  out.printf("\t\t%sWidget.%s = %s;\n",
//                    clazz.simpleTypeName.toLowerCase(), superField.varName, superField.varName);
//                }
//              }
//            }
//
//            out.printf("\t}\n\n");
//
//            // Builder equals method override
//            out.printf("\t@Override\n");
//            out.printf("\tpublic boolean equals(Object obj) {\n");
//            out.printf("\t\treturn obj instanceof %s %s\n", clazz.builderSimpleTypeName, clazz.builderVarName);
//
//            List<WidgetField> comparableFields = clazz.fields.stream().filter(field -> field.comparable).toList();
//
//            for (WidgetField field : comparableFields) {
//              out.printf("\t\t\t&& java.util.Objects.equals(%s, %s.%s)\n",
//                field.varName, clazz.builderVarName, field.varName);
//            }
//
//            for (WidgetClass superClass : clazz.ancestry) {
//              List<WidgetField> comparableSuperFields =
//                superClass.fields.stream().filter(field -> field.comparable).toList();
//
//              for (WidgetField superField : comparableSuperFields) {
//                out.printf("\t\t\t&& java.util.Objects.equals(%s, %s.%s)\n",
//                  superField.varName, clazz.builderVarName, superField.varName);
//              }
//            }
//
//            out.printf(";\n");
//            out.printf("\t}\n");
//
//            // Builder class end brace
//            out.printf("}");
//          }
//        } catch (IOException e) {
//          throw new RuntimeException(e);
//        }
//      }

      // Generate Theme widget
//      try {
//        JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(THEME_CLASS_NAME);
//
//        try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
//          // Theme widget package declaration
//          out.printf("package dev.gamekit.ui.widgets;\n\n");
//
//          // Theme widget class declaration
//          out.printf("@dev.gamekit.annotations.WidgetBuilder\n");
//          out.printf("public class Theme extends dev.gamekit.ui.widgets.SingleChildParent {\n");
//
//          // Theme widget static field declarations
//          // @formatter:off
//          out.println("\tpublic static final dev.gamekit.ui.widgets.Theme DEFAULT = new dev.gamekit.ui.widgets.Theme(dev.gamekit.ui.widgets.ThemeConfig.child(dev.gamekit.ui.widgets.Empty.create()));\n");
//          // @formatter:on
//
//          // Theme widget instance field declarations
////          List<WidgetClass> widgetsWithOwnThemableFields = widgetClasses.stream().filter(
////            clazz -> !clazz.fields.isEmpty() && !clazz.fields.stream().filter(
////              field -> field.themable
////            ).toList().isEmpty()
////          ).toList();
//
//          for (WidgetClass clazz : widgetClasses) {
//            List<WidgetField> themableFields = clazz.fields.stream().filter(field -> field.themable).toList();
//
//            for (WidgetField field : themableFields) {
//              out.printf("\t@dev.gamekit.annotations.WidgetBuilderField\n");
//              out.printf("\tpublic %s %s%s = %s;\n",
//                field.typeName, clazz.varName, field.nameAsSuffix, field.fallbackValue);
//            }
//
//            if (!themableFields.isEmpty()) out.println();
//
//            for (WidgetClass superClass : clazz.ancestry) {
//              List<WidgetField> themableSuperFields =
//                superClass.fields.stream().filter(field -> field.themable).toList();
//
//              for (WidgetField superField : themableSuperFields) {
//                out.printf("\t@dev.gamekit.annotations.WidgetBuilderField\n");
//                out.printf("\tpublic %s %s%s = %s;\n",
//                  superField.typeName, superClass.varName, superField.nameAsSuffix, superField.fallbackValue);
//              }
//
//              if (!themableSuperFields.isEmpty()) out.println();
//            }
//          }
//
//          // Theme widget constructor and static creator method declarations
//          out.println("""
//              public Theme(ThemeConfig... config) {
//                super(config);
//              }
//
//              public static Theme create(ThemeConfig... config) {
//                return new Theme(config);
//              }
//            """);
//
//          // Theme widget performInit method override
//          // @formatter:off
//          out.printf("\t@Override\n");
//          out.printf("\tprotected void performInit() {\n");
//          out.printf("\t\tdev.gamekit.ui.widgets.ThemeConfig config = (dev.gamekit.ui.widgets.ThemeConfig) super.config;\n");
//          out.println("\t\tdev.gamekit.ui.widgets.Theme theme = dev.gamekit.utils.Misc.coalesce(getAncestorOfType(dev.gamekit.ui.widgets.Theme.class), Theme.DEFAULT);\n");
//          // @formatter:on
//
//          for (WidgetClass clazz : widgetClasses) {
//            List<WidgetField> themableFields = clazz.fields.stream().filter(field -> field.themable).toList();
//
//            for (WidgetField field : themableFields) {
//              String fieldUsageName = clazz.varName + field.nameAsSuffix;
//
//              out.printf("\t\t%s = dev.gamekit.utils.Misc.coalesce(config.%s, theme.%s);\n",
//                fieldUsageName, fieldUsageName, fieldUsageName);
//            }
//
//            if (!themableFields.isEmpty()) out.println();
//
//            for (WidgetClass superClass : clazz.ancestry) {
//              List<WidgetField> themableSuperFields =
//                superClass.fields.stream().filter(field -> field.themable).toList();
//
//              for (WidgetField superField : themableSuperFields) {
//                String fieldUsageName = superClass.varName + superField.nameAsSuffix;
//
//                out.printf("\t\t%s = dev.gamekit.utils.Misc.coalesce(config.%s, theme.%s);\n",
//                  fieldUsageName, fieldUsageName, fieldUsageName);
//              }
//
//              if (!themableSuperFields.isEmpty()) out.println();
//            }
//          }
//
//          out.printf("\t\tsuper.performInit();\n");
//          out.printf("\t}\n\n");
//
//          // Theme widget performLayout method override
//          out.println("""
//              @Override
//              protected void performLayout(dev.gamekit.utils.Constraints constraints) {
//                child.layout(constraints);
//
//                intrinsicSize.set(child.computedBounds.width, child.computedBounds.height);
//
//                computedBounds.setSize(
//                  constraints.constrainWidth(intrinsicSize.width),
//                  constraints.constrainHeight(intrinsicSize.height)
//                );
//              }
//            """);
//
//          // Theme widget class end brace
//          out.printf("}");
//        }
//      } catch (FilerException e) {
//        processingEnv.getMessager().printWarning(e.getMessage());
//      } catch (IOException e) {
//        throw new RuntimeException(e);
//      }
    }

    return false;
  }

  private void generateWidgetConfigSource(WidgetClass widgetClass) {
    try {
      JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(widgetClass.builderTypeName);

      try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
        // Builder package declaration
        out.printf("package %s;\n", widgetClass.builderPackageName);

        // Builder import declarations
        out.printf("import dev.gamekit.ui.widgets.Widget;\n");
        out.printf("import dev.gamekit.ui.widgets.Theme;\n");
        out.printf("import dev.gamekit.utils.Misc;\n\n");

        // Builder class declaration
        if (widgetClass.superClassBuilderTypeName != null) {
          out.printf("public class %s extends %s implements Widget.Config {\n",
            widgetClass.builderSimpleTypeName, widgetClass.superClassBuilderTypeName);
        } else {
          out.printf("public class %s implements Widget.Config {\n",
            widgetClass.builderSimpleTypeName);
        }

        // Builder instance fields declaration
        List<WidgetField> widgetClassOwnFields =
          widgetClass.fields.stream().filter(field -> field.classTypeName.equals(widgetClass.typeName)).toList();

        for (int i = 0; i < widgetClassOwnFields.size(); i++) {
          WidgetField field = widgetClassOwnFields.get(i);
          out.printf("\tpublic %s %s;\n", field.typeName, field.varName);

          if (i == widgetClassOwnFields.size() - 1) out.println();
        }

        // Builder static setters declaration
        for (WidgetField field : widgetClass.fields) {
          out.printf("\tpublic static %s %s(%s %s) {\n",
            widgetClass.builderTypeName, field.varName, field.setterTypeName, field.varName);
          out.printf("\t\t%s config = new %s();\n", widgetClass.builderTypeName, widgetClass.builderTypeName);
          out.printf("\t\tconfig.%s = %s;\n", field.varName, field.varName);
          out.printf("\t\treturn config;\n");
          out.printf("\t}\n\n");
        }

        // Builder equals method override
        out.printf("\t@Override\n");
        out.printf("\tpublic boolean equals(Object obj) {\n");
        out.printf("\t\treturn obj instanceof %s %s\n", widgetClass.builderSimpleTypeName, widgetClass.builderVarName);

        List<WidgetField> comparableFields = widgetClass.fields.stream().filter(field -> field.comparable).toList();

        for (int i = 0; i < comparableFields.size(); i++) {
          WidgetField field = comparableFields.get(i);
          out.printf("\t\t\t&& java.util.Objects.equals(%s, %s.%s)%s",
            field.varName, widgetClass.builderVarName, field.varName, i < comparableFields.size() - 1 ? "\n" : ";\n");
        }

        out.printf("\t}\n\n");


        // Builder updateWidget method override
        out.printf("\t@Override\n");
        out.printf("\tpublic void updateWidget(Widget widget) {\n");
        out.printf("\t\t%s %sWidget = (%s) widget;\n", widgetClass.typeName, widgetClass.varName, widgetClass.typeName);
        out.printf("\t\tTheme nearestTheme = Misc.coalesce(widget.getAncestorOfType(Theme.class), Theme.DEFAULT);\n");

        List<WidgetField> updatableFields = widgetClass.fields.stream().filter(field -> field.updatable).toList();

        for (WidgetField field : updatableFields) {
          if (field.themable) {
//            String nestingThemeVarName = !clazz.typeName.equals(THEME_CLASS_NAME) ?
//              clazz.varName + field.nameAsSuffix : field.varName;
//
            out.printf("\t\t%sWidget.%s = Misc.coalesce(%s, nearestTheme.%s);\n",
              widgetClass.varName, field.varName, field.varName, field.classVarName + field.varNameAsSuffix);
          } else {
            out.printf("\t\t%sWidget.%s = %s;\n", widgetClass.varName, field.varName, field.varName);
          }
        }

        out.printf("\t}\n\n");

        // Builder mergeWith method override
        out.printf("\t@Override\n");
        out.printf("\tpublic Widget.Config mergeWith(Widget.Config[] configs) {\n");
        out.printf("\t\t%s resolved = new %s();\n\n",
          widgetClass.builderSimpleTypeName, widgetClass.builderSimpleTypeName);

        out.printf("\t\tfor (Widget.Config config : configs) {\n");
        out.printf("\t\t\t%s incoming = (%s) config;\n",
          widgetClass.builderSimpleTypeName, widgetClass.builderSimpleTypeName);

        for (WidgetField field : widgetClass.fields) {
          out.printf("\t\t\tif (incoming.%s != null) resolved.%s = incoming.%s;\n",
            field.varName, field.varName, field.varName);
        }

        out.printf("\t\t}\n\n");
        out.printf("\t\treturn resolved;\n");
        out.printf("\t}\n\n");

        // Builder class end brace
        out.printf("}");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private WidgetClass extractWidgetClassData(Element element) {
    TypeElement typeElement = (TypeElement) element;
    String classTypeName = typeElement.getQualifiedName().toString();

    TypeElement typeSuperClassElement = (TypeElement) ((DeclaredType) typeElement.getSuperclass()).asElement();
    String superClassTypeName = typeSuperClassElement.getAnnotation(WidgetBuilder.class) != null
      ? typeSuperClassElement.getQualifiedName().toString() : null;

    TypeElement iteratorElement = (TypeElement) element;
    List<WidgetField> widgetFields = new ArrayList<>();

    while (!iteratorElement.getQualifiedName().toString().equals(WIDGET_CLASS_NAME)
      && iteratorElement.getAnnotation(WidgetBuilder.class) != null) {
      String iterClassTypeName = iteratorElement.getQualifiedName().toString();
      String iterClassSimpleName = iterClassTypeName.substring(iterClassTypeName.lastIndexOf(".") + 1);
      String iterClassVarName = iterClassSimpleName.substring(0, 1).toLowerCase() + iterClassSimpleName.substring(1);

      List<? extends Element> elementMembers = iteratorElement.getEnclosedElements().stream().filter(
        member -> member.getKind() == ElementKind.FIELD && member.getAnnotation(WidgetBuilderField.class) != null
      ).toList();

      for (Element fieldElement : elementMembers) {
        String fieldTypeName = fieldElement.asType().toString();
        String fieldVarName = fieldElement.getSimpleName().toString();
        String fieldCustomSetterType = fieldElement.getAnnotation(WidgetBuilderField.class).customSetterType();
        String fieldSetterTypeName = !fieldCustomSetterType.isEmpty() ? fieldCustomSetterType : fieldTypeName;
        String fieldFallbackDeclaration = fieldElement.getAnnotation(WidgetBuilderField.class).fallback();
        String fieldFallbackValue = !fieldFallbackDeclaration.isEmpty() ? fieldFallbackDeclaration : "null";
        boolean comparable = fieldElement.getAnnotation(WidgetBuilderField.class).comparable();
        boolean updatable = fieldElement.getAnnotation(WidgetBuilderField.class).updatable();
        boolean themable = fieldElement.getAnnotation(WidgetBuilderField.class).themable();

        widgetFields.add(
          new WidgetField(
            fieldTypeName, fieldVarName, fieldSetterTypeName, fieldFallbackValue,
            iterClassTypeName, iterClassSimpleName, iterClassVarName,
            comparable, updatable, themable
          )
        );
      }

      iteratorElement = (TypeElement) ((DeclaredType) iteratorElement.getSuperclass()).asElement();
    }

    return new WidgetClass(classTypeName, superClassTypeName, widgetFields);
  }

  private boolean elementIsWidget(Element element) {
    if (element.getKind() != ElementKind.CLASS || element instanceof NoType
      || !(element instanceof TypeElement typeElement))
      return false;

    if (typeElement.getQualifiedName().toString().equals(WIDGET_CLASS_NAME)) return true;

    return elementIsWidget(((DeclaredType) typeElement.getSuperclass()).asElement());
  }
}
