package dev.gamekit.processors;

import dev.gamekit.annotations.WidgetBuilderField;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@SupportedAnnotationTypes("dev.gamekit.annotations.WidgetBuilderField")
@SupportedSourceVersion(SourceVersion.RELEASE_18)
public class WidgetBuilderFieldProcessor extends AbstractProcessor {
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
      Map<String, List<WidgetField>> widgetFieldMap = new HashMap<>();

      if (annotation.getQualifiedName().toString().equals(WidgetBuilderField.class.getCanonicalName())) {
        for (Element annotatedElement : annotatedElements) {
          // Check if annotated element is a field
          if (annotatedElement.getKind() != ElementKind.FIELD) {
            throw new IllegalStateException(
              "WidgetField annotations can only be used on field elements"
            );
          }

          // Check if annotated element is in a class
          TypeElement widgetClassElement = (TypeElement) annotatedElement.getEnclosingElement();
          if (widgetClassElement.getKind() != ElementKind.CLASS) {
            throw new IllegalStateException(
              "WidgetField annotations can only be used inside a class"
            );
          }

          // Check if annotated element is in a Widget class
          TypeElement visitedElement = (TypeElement) annotatedElement.getEnclosingElement();
          final String widgetClassName = "dev.gamekit.ui.widgets.Widget";
          boolean enclosingElementIsWidget = false;

          while (!(visitedElement.getSuperclass() instanceof NoType)) {
            if (visitedElement.getQualifiedName().toString().equals(widgetClassName)) {
              enclosingElementIsWidget = true;
              break;
            }

            visitedElement = (TypeElement) ((DeclaredType) visitedElement.getSuperclass()).asElement();
          }

          if (!enclosingElementIsWidget) {
            throw new IllegalStateException(
              "WidgetField annotations can only be used inside a Widget class"
            );
          }

          // Map the widget name to the annotated elements
          List<WidgetField> widgetFields =
            widgetFieldMap.get(widgetClassElement.getQualifiedName().toString());

          if (widgetFields == null)
            widgetFields = new ArrayList<>();

          widgetFields.add(
            new WidgetField(
              annotatedElement.asType().toString(),
              annotatedElement.getSimpleName().toString(),
              null
            )
          );

          widgetFieldMap.put(widgetClassElement.getQualifiedName().toString(), widgetFields);
        }

        // TODO: For each widget class, go up the widget ancestry and include superclass fields
        //  annotated by WidgetField

        // Create widget config source files
        for (String widgetClassName : widgetFieldMap.keySet()) {
          try {
            String packageName = widgetClassName.substring(0, widgetClassName.lastIndexOf("."));
            String widgetName = widgetClassName.substring(widgetClassName.lastIndexOf(".") + 1);
            String widgetConfigName = widgetName + "Config";
            String widgetConfigVarName = widgetName.toLowerCase() + "Config";
            List<WidgetField> widgetFields = widgetFieldMap.get(widgetClassName);
            JavaFileObject fileObject =
              processingEnv.getFiler().createSourceFile(widgetClassName + "Config");

            try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {

              out.printf("package %s;\n\n", packageName);
              out.printf(
                "public class %s implements dev.gamekit.ui.widgets.Widget.Config {\n",
                widgetConfigName
              );

              for (WidgetField field : widgetFields) {
                out.printf("\tpublic %s %s = null;\n", field.type(), field.name());
              }

              out.println();

              for (WidgetField field : widgetFields) {
                out.printf(
                  "\tpublic static %s %s(%s %s) {\n",
                  widgetConfigName, field.name(), field.type(), field.name()
                );
                out.printf("\t\t%s config = new %s();\n", widgetConfigName, widgetConfigName);
                out.printf("\t\tconfig.%s = %s;\n", field.name(), field.name());
                out.printf("\t\treturn config;\n");
                out.printf("\t}\n\n");
              }

              out.printf("\t@Override\n");
              out.printf("\tpublic Widget.Config mergeWith(Widget.Config[] configs) {\n");
              out.printf("\t\t%s buffer = new %s();\n\n", widgetConfigName, widgetConfigName);

              out.printf("\t\tfor (var config : configs) {\n");
              out.printf(
                "\t\t\t%s %s = (%s) config;\n",
                widgetConfigName, widgetConfigVarName, widgetConfigName
              );

              for (WidgetField field : widgetFields) {
                out.printf(
                  "\t\t\tbuffer.%s = %s.%s != null ? %s.%s : buffer.%s;\n",
                  field.name(), widgetConfigVarName, field.name(), widgetConfigVarName,
                  field.name(), field.name()
                );
              }

              out.printf("\t\t}\n\n");

              out.printf("\t\treturn buffer;\n");
              out.printf("\t}\n");

              out.printf("}");
            }
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }

    return false;
  }
}
