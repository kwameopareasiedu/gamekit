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
    }

    return false;
  }

  private void generateThemeWidgetSource(List<WidgetClass> widgetClasses) {
    try {
      JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(THEME_TYPE_NAME);

      try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
        // Theme widget package declaration
        out.printf("package dev.gamekit.ui.widgets;\n\n");

        // Theme widget import declarations
        out.printf("import dev.gamekit.utils.Misc;\n\n");

        // Theme widget class declaration
        out.printf("@dev.gamekit.annotations.WidgetBuilder\n");
        out.printf("public class Theme extends SingleChildParent {\n");

        // Theme widget static field declarations
        out.println("\tpublic static final Theme DEFAULT = new Theme(ThemeConfig.child(Empty.create()));\n");

        // Theme widget instance field declarations
        for (WidgetClass widgetClass : widgetClasses) {
          List<WidgetField> widgetClassOwnThemableFields = widgetClass.fields.stream().filter(
            field -> field.themable && field.classTypeName.equals(widgetClass.typeName)
          ).toList();

          for (WidgetField field : widgetClassOwnThemableFields) {
            out.printf("\t@dev.gamekit.annotations.WidgetBuilderField\n");
            out.printf("\tpublic %s %s%s = %s;\n",
              field.typeName, widgetClass.varName, field.varNameAsSuffix, field.fallbackValue);
          }

          if (!widgetClassOwnThemableFields.isEmpty()) out.println();
        }

        // Theme widget constructor and static creator method declarations
        out.println("""
            public Theme(ThemeConfig... config) {
              super(config);
            }
          
            public static Theme create(ThemeConfig... config) {
              return new Theme(config);
            }
          """);

        // Theme widget performLayout method override
        out.println("""
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
      JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(widgetClass.builderTypeName);

      try (PrintWriter out = new PrintWriter(fileObject.openOutputStream())) {
        // Builder package declaration
        out.printf("package %s;\n\n", widgetClass.builderPackageName);

        // Builder import declarations
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

        List<WidgetField> comparableFields = widgetClass.fields.stream().filter(field -> field.comparable).toList();
        out.printf("\t\treturn obj instanceof %s %s%s",
          widgetClass.builderSimpleTypeName, widgetClass.builderVarName, comparableFields.isEmpty() ? ";\n" : "\n");

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
            String nearestThemeFieldVarName = !widgetClass.typeName.equals(THEME_TYPE_NAME) ?
              field.classVarName + field.varNameAsSuffix : field.varName;

            out.printf("\t\t%sWidget.%s = Misc.coalesce(%s, nearestTheme.%s);\n",
              widgetClass.varName, field.varName, field.varName, nearestThemeFieldVarName);
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
            iteratorClassTypeName, iteratorClassSimpleName, iteratorClassVarName,
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

    if (typeElement.getQualifiedName().toString().equals(WIDGET_TYPE_NAME)) return true;

    return elementIsWidget(((DeclaredType) typeElement.getSuperclass()).asElement());
  }
}
