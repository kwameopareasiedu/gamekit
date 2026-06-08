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
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("dev.gamekit.annotations.WidgetBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_18)
public class WidgetBuilderProcessor extends AbstractProcessor {
  private static final String WIDGET_TYPE_NAME = "dev.gamekit.ui.widgets.Widget";
  private static final String THEME_TYPE_NAME = "dev.gamekit.ui.widgets.Theme";
  private static final String WIDGETS_TYPE_NAME = "dev.gamekit.ui.widgets.Widgets";

  private final List<WidgetClass> widgetClasses = new ArrayList<>();
  private boolean themeWidgetGenerated = false;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      Set<? extends Element> annotatedClasses = roundEnv.getElementsAnnotatedWith(annotation);

      for (Element annotatedClassElement : annotatedClasses) {
        ensureElementIsWidget(annotatedClassElement);

        WidgetClass widgetClass = extractWidgetClassData(annotatedClassElement);

        if (!widgetClasses.contains(widgetClass) && widgetClass != null) {
          generateWidgetConfigSource(widgetClass);
          widgetClasses.add(widgetClass);
        }
      }

      if (!themeWidgetGenerated) {
        generateThemeWidgetSource(widgetClasses);
        themeWidgetGenerated = true;
      } else {
        generateWidgetsUtilitySource(widgetClasses);
      }
    }

    return false;
  }

  private void generateWidgetsUtilitySource(List<WidgetClass> widgetClasses) {
    try {
      JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(WIDGETS_TYPE_NAME);

      try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
        // Package declaration
        out.printf("package dev.gamekit.ui.widgets;\n\n");

        // Class and private constructor declaration
        out.printf("""
          class Widgets {
            private Widgets() { }
          """);

        out.println();

        // Static configure method declarations
        for (WidgetClass widgetClass : widgetClasses) {
          out.printf("\tstatic %s configure%s(%s.Updater updater) {\n",
            widgetClass.configTypeName, widgetClass.simpleTypeName, widgetClass.configTypeName);
          out.printf("\t\t%s config = new %s();\n", widgetClass.configTypeName, widgetClass.configTypeName);
          out.printf("\t\tupdater.update(config);\n");
          out.printf("\t\treturn config;\n");
          out.printf("\t}\n");

          out.println();
        }

        // Theme widget class end brace
        out.printf("}");
      }
    } catch (FilerException e) {
      processingEnv.getMessager().printWarning(e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void generateThemeWidgetSource(List<WidgetClass> widgetClasses) {
    try {
      JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(THEME_TYPE_NAME);

      try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
        // Package declaration
        out.printf("package dev.gamekit.ui.widgets;\n\n");

        // Class declaration
        out.printf("@dev.gamekit.annotations.WidgetBuilder\n");
        out.printf("public class Theme extends SingleChildParent {\n");

        // Static field declarations
        out.println("\tpublic static final Theme DEFAULT = new Theme(null, new ThemeConfig(), Empty.create());\n");

        // Instance field declarations
        for (WidgetClass widgetClass : widgetClasses) {
          WidgetClass.traverse(widgetClass, clazz -> {
            List<WidgetField> widgetClassThemableFields =
              clazz.fields.stream().filter(field -> field.themable).toList();

            for (WidgetField field : widgetClassThemableFields) {
              out.printf("\t@dev.gamekit.annotations.WidgetBuilderField\n");
              out.printf("\tpublic %s %s%s = %s;\n",
                field.typeName, widgetClass.varName, field.varNameAsSuffix, field.fallbackValue);
            }

            if (!widgetClassThemableFields.isEmpty()) out.println();
          });
        }

        // Constructor, static creator method and performLayout method override declarations
        out.println("""
            public Theme(String key, ThemeConfig config, Widget child) {
              super(key, config, child);
            }
          
            public static Theme create(String key, ThemeConfig.Updater updater, Widget child) {
              return new Theme(key, Widgets.configureTheme(updater), child);
            }
          
            public static Theme create(ThemeConfig.Updater updater, Widget child) {
              return new Theme(null, Widgets.configureTheme(updater), child);
            }
          
            @Override
            protected void performLayout(dev.gamekit.utils.Constraints constraints) {
              child.layout(constraints);
          
              intrinsicSize.set(child.computedBounds.width, child.computedBounds.height);
          
              computedBounds.setSize(
                constraints.constrainWidth(intrinsicSize.width),
                constraints.constrainHeight(intrinsicSize.height)
              );
            }
          """);

        // Class end brace
        out.printf("}");
      }
    } catch (FilerException e) {
      processingEnv.getMessager().printWarning(e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void generateWidgetConfigSource(WidgetClass widgetClass) {
    try {
      JavaFileObject sourceFileObject = processingEnv.getFiler().createSourceFile(widgetClass.configTypeName);

      try (PrintWriter out = new PrintWriter(sourceFileObject.openOutputStream())) {
        // Package declaration
        out.printf("package %s;\n\n", widgetClass.configPackageName);

        // Class declaration
        if (widgetClass.base != null) {
          out.printf(
            "public class %s extends %s implements dev.gamekit.ui.widgets.Widget.Config {\n",
            widgetClass.configSimpleTypeName, widgetClass.base.configTypeName
          );
        } else {
          out.printf(
            "public class %s implements dev.gamekit.ui.widgets.Widget.Config {\n",
            widgetClass.configSimpleTypeName
          );
        }

        // Instance fields declaration
        WidgetClass.traverse(widgetClass, (clazz) -> {
          for (WidgetField field : clazz.fields) {
            out.printf("\tpublic %s %s;\n", field.typeName, field.varName);
          }
        });

        out.println();

        // Equals method override
        out.printf("\t@Override\n");
        out.printf("\tpublic boolean equals(Object obj) {\n");
        out.printf("\t\treturn obj instanceof %s %s\n", widgetClass.configSimpleTypeName, widgetClass.configVarName);

        WidgetClass.traverse(widgetClass, clazz -> {
          List<WidgetField> comparableFields = clazz.fields.stream().filter(field -> field.comparable).toList();

          for (WidgetField field : comparableFields) {
            out.printf(
              "\t\t\t&& java.util.Objects.equals(%s, %s.%s)\n",
              field.varName, widgetClass.configVarName, field.varName
            );
          }
        });

        out.printf("\t\t;\n");
        out.printf("\t}\n\n");

        // UpdateWidget method override @formatter:off
        out.printf("\t@Override\n");
        out.printf("\tpublic void updateWidget(dev.gamekit.ui.widgets.Widget widget) {\n");
        out.printf("\t\t%s %sWidget = (%s) widget;\n", widgetClass.typeName, widgetClass.varName, widgetClass.typeName);
        out.printf("\t\tdev.gamekit.ui.widgets.Theme nearestTheme = dev.gamekit.utils.Misc.coalesce(widget.getAncestorOfType(dev.gamekit.ui.widgets.Theme.class), dev.gamekit.ui.widgets.Theme.DEFAULT);\n");
        // @formatter:on

        out.println();

        WidgetClass.traverse(widgetClass, clazz -> {
          for (WidgetField field : clazz.fields) {
            if (field.themable) {
              String nearestThemeFieldVarName =
                !clazz.typeName.equals(THEME_TYPE_NAME) ? widgetClass.varName + field.varNameAsSuffix : field.varName;

              out.printf(
                "\t\t%sWidget.%s = dev.gamekit.utils.Misc.coalesce(%s, nearestTheme.%s);\n",
                widgetClass.varName, field.varName, field.varName, nearestThemeFieldVarName
              );
            } else {
              out.printf("\t\t%sWidget.%s = %s;\n", widgetClass.varName, field.varName, field.varName);
            }
          }
        });

        out.printf("\t}\n\n");

        // Updater interface declaration
        out.printf("\t@FunctionalInterface\n");
        out.printf("\tpublic interface Updater extends dev.gamekit.ui.widgets.Widget.ConfigUpdater<%s> { }\n",
          widgetClass.configSimpleTypeName);

        // Class end brace
        out.printf("}");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private WidgetClass extractWidgetClassData(Element element) {
    TypeElement typeElement = (TypeElement) element;

    if (typeElement.getAnnotation(WidgetBuilder.class) == null)
      return null;

    String classTypeName = typeElement.getQualifiedName().toString();

    List<? extends Element> elementMembers = typeElement.getEnclosedElements().stream().filter(
      member -> member.getKind() == ElementKind.FIELD && member.getAnnotation(WidgetBuilderField.class) != null
    ).toList();

    List<WidgetField> widgetFields = elementMembers.stream().map(
      fieldElement -> {
        WidgetBuilderField annotation = fieldElement.getAnnotation(WidgetBuilderField.class);
        String fieldTypeName = fieldElement.asType().toString();
        String fieldVarName = fieldElement.getSimpleName().toString();
        String fieldFallback = annotation.fallback();
        String fieldFallbackValue = !fieldFallback.isEmpty() ? fieldFallback : "null";
        boolean comparable = annotation.comparable();
        boolean themable = annotation.themable();

        return new WidgetField(fieldTypeName, fieldVarName, fieldFallbackValue, comparable, themable);
      }
    ).toList();

    TypeElement superTypeElement = (TypeElement) ((DeclaredType) typeElement.getSuperclass()).asElement();

    WidgetClass base = extractWidgetClassData(superTypeElement);

    return new WidgetClass(true, classTypeName, widgetFields, base);
  }

  private void ensureElementIsWidget(Element element) {
    if (element.getKind() != ElementKind.CLASS || element instanceof NoType || !(element instanceof TypeElement typeElement))
      throw new IllegalArgumentException("WidgetBuilder annotation can only be used on Widget classes");

    if (typeElement.getQualifiedName().toString().equals(WIDGET_TYPE_NAME)) return;

    ensureElementIsWidget(((DeclaredType) typeElement.getSuperclass()).asElement());
  }
}
