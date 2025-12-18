package dev.gamekit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link WidgetBuilderField} marks fields of a {@code Widget} as needing to be part of the
 * enclosing widget's generated builder class.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface WidgetBuilderField {
  /**
   * A custom fully qualified type to use as the argument type of the setter method generated
   * for the annotated widget field
   */
  String setterArgType() default "";

  /**
   * Indicates that the annotated field should take part in state comparison in the generated
   * config builder class
   */
  boolean includeInStateMatch() default true;
}
