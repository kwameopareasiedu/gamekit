package dev.gamekit.annotationprocessors;

import dev.gamekit.annotations.CustomWidgetBuilder;
import dev.gamekit.annotations.CustomWidgetBuilderField;
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
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("dev.gamekit.annotations.CustomWidgetBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_18)
public class CustomWidgetBuilderProcessor extends AbstractProcessor {
  private static final String WIDGET_TYPE_NAME = "dev.gamekit.ui.widgets.Widget";
  private static final String THEME_TYPE_NAME = "dev.gamekit.ui.widgets.Theme";

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
        processingEnv.getMessager().printNote(widgetClass.toString());
        generateWidgetConfigSource(widgetClass);
      }
    }

    return false;
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
                !clazz.typeName.equals(THEME_TYPE_NAME) ? clazz.varName + field.varNameAsSuffix : field.varName;

              out.printf(
                "\t\t%sWidget.%s = dev.gamekit.utils.Misc.coalesce(%s, nearestTheme.%s);\n",
                widgetClass.varName, field.varName, field.varName, nearestThemeFieldVarName
              );
            } else {
              String assignedValue = (widgetClass.internal || clazz == widgetClass)
                ? field.varName : clazz.varName + field.varNameAsSuffix;

              out.printf("\t\t%sWidget.%s = %s;\n", clazz.varName, field.varName, assignedValue);
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

    if (typeElement.getQualifiedName().toString().equals(WIDGET_TYPE_NAME)
      || (typeElement.getAnnotation(CustomWidgetBuilder.class) == null && typeElement.getAnnotation(WidgetBuilder.class) == null))
      return null;

    String classTypeName = typeElement.getQualifiedName().toString();
    boolean elementIsInternalWidget = typeElement.getAnnotation(WidgetBuilder.class) != null;

    List<? extends Element> elementMembers = typeElement.getEnclosedElements().stream().filter(
      member -> member.getKind() == ElementKind.FIELD &&
        (member.getAnnotation(CustomWidgetBuilderField.class) != null || member.getAnnotation(WidgetBuilderField.class) != null)
    ).toList();

    List<WidgetField> widgetFields = elementMembers.stream().map(
      fieldElement -> {
        CustomWidgetBuilderField customAnnotation = fieldElement.getAnnotation(CustomWidgetBuilderField.class);
        WidgetBuilderField internalAnnotation = fieldElement.getAnnotation(WidgetBuilderField.class);
        String fieldTypeName = fieldElement.asType().toString();
        String fieldVarName = fieldElement.getSimpleName().toString();
        String fieldFallback = customAnnotation != null ? customAnnotation.fallback() : internalAnnotation.fallback();
        String fieldFallbackValue = !fieldFallback.isEmpty() ? fieldFallback : "null";
        boolean comparable = customAnnotation != null ? customAnnotation.comparable() : internalAnnotation.comparable();
        boolean themable = customAnnotation == null && internalAnnotation.themable();

        return new WidgetField(fieldTypeName, fieldVarName, fieldFallbackValue, comparable, themable);
      }
    ).toList();

    TypeElement superTypeElement = (TypeElement) ((DeclaredType) typeElement.getSuperclass()).asElement();

    WidgetClass base = extractWidgetClassData(superTypeElement);

    return new WidgetClass(elementIsInternalWidget, classTypeName, widgetFields, base);
  }

  private void ensureElementIsWidget(Element el) {
    if (el.getKind() != ElementKind.CLASS || el instanceof NoType || !(el instanceof TypeElement typeElement))
      throw new IllegalStateException("CustomWidgetBuilder annotation can only be used on Widget classes");

    if (typeElement.getQualifiedName().toString().equals(WIDGET_TYPE_NAME)) return;

    ensureElementIsWidget(((DeclaredType) typeElement.getSuperclass()).asElement());
  }
}
