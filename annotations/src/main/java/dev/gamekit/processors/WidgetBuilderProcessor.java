package dev.gamekit.processors;

import dev.gamekit.annotations.WidgetBuilder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("dev.gamekit.annotations.WidgetBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_18)
public class WidgetBuilderProcessor extends AbstractProcessor {
  private static final String WIDGET_CLASS_NAME = "dev.gamekit.ui.widgets.Widget";

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      Set<? extends Element> annotatedClasses = roundEnv.getElementsAnnotatedWith(annotation);
      //      Map<String, List<WidgetField>> widgetFieldMap = new HashMap<>();
      List<WidgetClass> widgetClasses = new ArrayList<>();

      for (Element annotatedClassElement : annotatedClasses) {
        // Check if annotated element is a class element
        if (annotatedClassElement.getKind() != ElementKind.CLASS) {
          throw new IllegalStateException(
            "WidgetBuilder annotation can only be used on class elements"
          );
        }

        // Check if annotated class is a Widget class
        TypeElement hierarchyElement = (TypeElement) annotatedClassElement;
        boolean isWidget = false;

        while (!(hierarchyElement.getSuperclass() instanceof NoType)) {
          if (hierarchyElement.getQualifiedName().toString().equals(WIDGET_CLASS_NAME)) {
            isWidget = true;
            break;
          }

          hierarchyElement =
            (TypeElement) ((DeclaredType) hierarchyElement.getSuperclass()).asElement();
        }

        if (!isWidget) {
          throw new IllegalStateException(
            "WidgetField annotations can only be used inside a Widget class"
          );
        }

        // Traverse the annotated class hierarchy, gathering fields annotated with
        // WidgetBuilderField
        hierarchyElement = (TypeElement) annotatedClassElement;

        // Map the widget name to the annotated elements
        String widgetClassName = ((TypeElement) annotatedClassElement).getQualifiedName().toString();
        List<WidgetField> allWidgetFields = new ArrayList<>();
        List<WidgetField> ownWidgetFields = new ArrayList<>();
        boolean resolvedSuperData = false;
        String widgetSuperName = null;

        do {
          List<? extends Element> fieldElements = hierarchyElement.getEnclosedElements();
          List<? extends Element> annotatedFieldElements = fieldElements.stream().filter(
            element -> element.getAnnotation(WidgetBuilderField.class) != null
          ).toList();

          for (var fieldElement : annotatedFieldElements) {
            String fieldType = fieldElement.asType().toString();
            String fieldCustomSetterTypeName =
              fieldElement.getAnnotation(WidgetBuilderField.class).setterArgType();
            String fieldSetterType =
              !fieldCustomSetterTypeName.isEmpty() ? fieldCustomSetterTypeName : fieldType;
            String fieldName = fieldElement.getSimpleName().toString();
            boolean fieldIncludedInStateMatch =
              fieldElement.getAnnotation(WidgetBuilderField.class).includeInStateMatch();

            allWidgetFields.add(
              new WidgetField(
                fieldType,
                fieldSetterType,
                fieldName,
                fieldIncludedInStateMatch
              )
            );
          }

          hierarchyElement =
            (TypeElement) ((DeclaredType) hierarchyElement.getSuperclass()).asElement();

          if (!resolvedSuperData) {
            if (hierarchyElement.getAnnotation(WidgetBuilder.class) != null)
              widgetSuperName = hierarchyElement.getQualifiedName().toString();

            ownWidgetFields.addAll(allWidgetFields);
            resolvedSuperData = true;
          }
        } while (!hierarchyElement.getQualifiedName().toString().equals(WIDGET_CLASS_NAME));

        widgetClasses.add(
          new WidgetClass(
            widgetClassName, widgetSuperName,
            allWidgetFields, ownWidgetFields
          )
        );
      }

      // Create widget config source files
      for (WidgetClass widgetClass : widgetClasses) {
        try {
          JavaFileObject fileObject =
            processingEnv.getFiler().createSourceFile(widgetClass.builderName);

          try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
            // Builder package declaration
            out.printf("package %s;\n\n", widgetClass.builderPackageName);

            // Builder class declaration
            if (widgetClass.superBuilderName != null) {
              out.printf(
                "public class %s extends %s implements dev.gamekit.ui.widgets.Widget.Config {\n",
                widgetClass.builderSimpleName,
                widgetClass.superBuilderName
              );
            } else {
              out.printf(
                "public class %s implements dev.gamekit.ui.widgets.Widget.Config {\n",
                widgetClass.builderSimpleName
              );
            }

            // Builder instance fields declaration
            for (WidgetField field : widgetClass.ownFields) {
              out.printf("\tpublic %s %s = null;\n", field.type(), field.name());
            }

            out.println();

            // Builder static setters declaration
            for (WidgetField field : widgetClass.allFields) {
              out.printf(
                "\tpublic static %s %s(%s %s) {\n",
                widgetClass.builderName, field.name(), field.setterType(), field.name()
              );
              out.printf("\t\t%s config = new %s();\n", widgetClass.builderName, widgetClass.builderName);
              out.printf("\t\tconfig.%s = %s;\n", field.name(), field.name());
              out.printf("\t\treturn config;\n");
              out.printf("\t}\n\n");
            }

            // Builder mergeWith method override
            out.printf("\t@Override\n");
            out.printf("\tpublic dev.gamekit.ui.widgets.Widget.Config mergeWith(dev.gamekit.ui.widgets.Widget.Config[] configs) {\n");
            out.printf(
              "\t\t%s buffer = new %s();\n\n",
              widgetClass.builderName, widgetClass.builderName
            );

            out.printf("\t\tfor (var config : configs) {\n");
            out.printf(
              "\t\t\t%s %s = (%s) config;\n",
              widgetClass.builderSimpleName, widgetClass.builderVarName, widgetClass.builderName
            );

            for (WidgetField field : widgetClass.allFields) {
              out.printf(
                "\t\t\tbuffer.%s = %s.%s != null ? %s.%s : buffer.%s;\n",
                field.name(), widgetClass.builderVarName, field.name(),
                widgetClass.builderVarName, field.name(), field.name()
              );
            }

            out.printf("\t\t}\n\n");

            out.printf("\t\treturn buffer;\n");
            out.printf("\t}\n\n");

            // Builder equals method override
            out.printf("\t@Override\n");
            out.printf("\tpublic boolean equals(Object obj) {\n");
            out.printf(
              "\t\treturn obj instanceof %s %s\n",
              widgetClass.builderSimpleName,
              widgetClass.builderVarName
            );

            List<WidgetField> stateMatchableFields = widgetClass.allFields.stream().filter(
              WidgetField::includeInStateMatch
            ).toList();

            for (WidgetField field : stateMatchableFields) {
              out.printf("\t\t\t&& java.util.Objects.equals(%s, %s.%s)\n",
                field.name(), widgetClass.builderVarName, field.name()
              );
            }

            out.printf(";\n");
            out.printf("\t}\n");

            // Builder class end brace
            out.printf("}");
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    return false;
  }
}
