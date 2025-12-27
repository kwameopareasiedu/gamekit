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

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
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

      generateThemeWidgetSource(widgetClasses);

      generateWidgetsUtilitySource(widgetClasses);

      // TODO: Since annotation processing is done incrementally by Maven (I.e. only processing changed symbols),
      //  We should record already processed widget classes in a file every time. This way, when incremental runs are
      //  made, we pull existing class data from said file and combine it with new symbols to fully generate the
      //  Theme widget
    }

    return false;
  }

  private void generateWidgetsUtilitySource(List<WidgetClass> widgetClasses) {
    try {
      JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(WIDGETS_TYPE_NAME);

      try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
        // Widgets package declaration
        out.printf("package dev.gamekit.ui.widgets;\n\n");

        // Widgets class and private constructor declaration
        out.printf("""
          class Widgets {
            private Widgets() { }
          """);

        out.println();

        // Widget configure method declarations
        for (WidgetClass widgetClass : widgetClasses) {
          out.printf("\tstatic %s configure%s(%s.Updater updater) {\n",
            widgetClass.configTypeName, widgetClass.simpleTypeName, widgetClass.configTypeName);
          out.printf("\t\t%s config = new %s();\n", widgetClass.configTypeName, widgetClass.configTypeName);
          out.printf("\t\tupdater.update(config);\n");
          out.printf("\t\treturn config;\n");
          out.printf("\t}\n");

          out.println();
        }

        // Theme configure method declaration
        out.printf("""
            static ThemeConfig configureTheme(ThemeConfig.Updater updater) {
              ThemeConfig config = new ThemeConfig();
              updater.update(config);
              return config;
            }
          """);

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
        // Theme widget package declaration
        out.printf("package dev.gamekit.ui.widgets;\n\n");

        // Theme widget class declaration
        out.printf("@dev.gamekit.annotations.WidgetBuilder\n");
        out.printf("public class Theme extends SingleChildParent {\n");

        // Theme widget static field declarations
        out.println("\tpublic static final Theme DEFAULT = new Theme(new ThemeConfig(), Empty.create());\n");

        // Theme widget instance field declarations
        for (WidgetClass widgetClass : widgetClasses) {
          List<WidgetField> widgetClassThemableFields =
            widgetClass.fields.stream().filter(field -> field.themable).toList();

          for (WidgetField field : widgetClassThemableFields) {
            out.printf("\t@dev.gamekit.annotations.WidgetBuilderField\n");
            out.printf("\tpublic %s %s%s = %s;\n",
              field.typeName, widgetClass.varName, field.varNameAsSuffix, field.fallbackValue);
          }

          if (!widgetClassThemableFields.isEmpty()) out.println();
        }

        // Theme widget constructor, static creator method and performLayout method override declarations
        out.println("""
            public Theme(ThemeConfig config, Widget child) {
              super(config, child);
            }
          
            public static Theme create(ThemeConfig.Updater updater, Widget child) {
              return new Theme(Widgets.configureTheme(updater), child);
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

        // Theme widget class end brace
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
      JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(widgetClass.configTypeName);

      try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
        // Package declaration
        out.printf("package %s;\n\n", widgetClass.configPackageName);

        // Import declarations
        out.printf("import static dev.gamekit.utils.Misc.coalesce;\n\n");

        // Class declaration
        if (widgetClass.superClassConfigTypeName != null) {
          out.printf("public class %s extends %s implements Widget.Config {\n",
            widgetClass.configSimpleTypeName, widgetClass.superClassConfigTypeName);
        } else {
          out.printf("public class %s implements Widget.Config {\n",
            widgetClass.configSimpleTypeName);
        }

        // Instance fields declaration
        for (int i = 0; i < widgetClass.fields.size(); i++) {
          WidgetField field = widgetClass.fields.get(i);
          out.printf("\tpublic %s %s;\n", field.typeName, field.varName);

          if (i == widgetClass.fields.size() - 1) out.println();
        }

        // Instance setter methods declaration
        for (WidgetField field : widgetClass.fields) {
          out.printf("\tpublic void %s(%s %s) {\n", field.varName, field.typeName, field.varName);
          out.printf("\t\t%s config = new %s();\n", widgetClass.configTypeName, widgetClass.configTypeName);
          out.printf("\t\tconfig.%s = %s;\n", field.varName, field.varName);
          out.printf("\t}\n\n");
        }

        // Equals method override
        out.printf("\t@Override\n");
        out.printf("\tpublic boolean equals(Object obj) {\n");

        List<WidgetField> comparableFields = widgetClass.fields.stream().filter(field -> field.comparable).toList();
        out.printf("\t\treturn obj instanceof %s %s%s",
          widgetClass.configSimpleTypeName, widgetClass.configVarName, comparableFields.isEmpty() ? ";\n" : "\n");

        for (int i = 0; i < comparableFields.size(); i++) {
          WidgetField field = comparableFields.get(i);
          out.printf("\t\t\t&& java.util.Objects.equals(%s, %s.%s)%s",
            field.varName, widgetClass.configVarName, field.varName, i < comparableFields.size() - 1 ? "\n" : ";\n");
        }

        out.printf("\t}\n\n");


        // UpdateWidget method override
        out.printf("\t@Override\n");
        out.printf("\tpublic void updateWidget(Widget widget) {\n");
        out.printf("\t\t%s %sWidget = (%s) widget;\n", widgetClass.typeName, widgetClass.varName, widgetClass.typeName);
        out.printf("\t\tTheme nearestTheme = coalesce(widget.getAncestorOfType(Theme.class), Theme.DEFAULT);\n");

        for (WidgetField field : widgetClass.fields) {
          if (field.themable) {
            String nearestThemeFieldVarName = !widgetClass.typeName.equals(THEME_TYPE_NAME) ?
              field.classVarName + field.varNameAsSuffix : field.varName;

            out.printf("\t\t%sWidget.%s = coalesce(%s, nearestTheme.%s);\n",
              widgetClass.varName, field.varName, field.varName, nearestThemeFieldVarName);
          } else {
            out.printf("\t\t%sWidget.%s = %s;\n", widgetClass.varName, field.varName, field.varName);
          }
        }

        out.printf("\t}\n\n");

        // Updater interface declaration
        out.printf("\t@FunctionalInterface\n");
        out.printf("\tpublic interface Updater extends Widget.ConfigUpdater<%s> { }\n",
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
    String classTypeName = typeElement.getQualifiedName().toString();

    TypeElement typeSuperClassElement = (TypeElement) ((DeclaredType) typeElement.getSuperclass()).asElement();
    String superClassTypeName = typeSuperClassElement.getAnnotation(WidgetBuilder.class) != null
      ? typeSuperClassElement.getQualifiedName().toString() : null;

    TypeElement iteratorElement = (TypeElement) element;
    List<WidgetField> widgetFields = new ArrayList<>();

    while (!iteratorElement.getQualifiedName().toString().equals(WIDGET_TYPE_NAME)
      && iteratorElement.getAnnotation(WidgetBuilder.class) != null) {
      String iteratorClassTypeName = iteratorElement.getQualifiedName().toString();
      String iteratorClassSimpleName = iteratorClassTypeName.substring(iteratorClassTypeName.lastIndexOf(".") + 1);
      String iteratorClassVarName =
        iteratorClassSimpleName.substring(0, 1).toLowerCase() + iteratorClassSimpleName.substring(1);

      List<? extends Element> elementMembers = iteratorElement.getEnclosedElements().stream().filter(
        member -> member.getKind() == ElementKind.FIELD && member.getAnnotation(WidgetBuilderField.class) != null
      ).toList();

      for (Element fieldElement : elementMembers) {
        String fieldTypeName = fieldElement.asType().toString();
        String fieldVarName = fieldElement.getSimpleName().toString();
        String fieldFallbackDeclaration = fieldElement.getAnnotation(WidgetBuilderField.class).fallback();
        String fieldFallbackValue = !fieldFallbackDeclaration.isEmpty() ? fieldFallbackDeclaration : "null";
        boolean comparable = fieldElement.getAnnotation(WidgetBuilderField.class).comparable();
        boolean themable = fieldElement.getAnnotation(WidgetBuilderField.class).themable();

        widgetFields.add(
          new WidgetField(
            fieldTypeName,
            fieldVarName,
            fieldFallbackValue,
            iteratorClassTypeName,
            iteratorClassSimpleName,
            iteratorClassVarName,
            comparable,
            themable
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

    if (typeElement.getQualifiedName().toString().equals(WIDGET_TYPE_NAME)) return true;

    return elementIsWidget(((DeclaredType) typeElement.getSuperclass()).asElement());
  }
}
