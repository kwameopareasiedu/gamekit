package dev.gamekit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link WidgetBuilderField} marks fields of a {@code Widget} as needing to be part of the enclosing widget's
 * generated builder class.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface WidgetBuilderField {
  /** A fully qualified declaration of the fallback value to set for the annotated field */
  String fallback() default "";

  /** Indicates that the annotated field is used in state comparison in the generated builder class equals method */
  boolean comparable() default true;

  /** Indicates that the annotated field should be included in the generated Theme widget class */
  boolean themable() default true;
}
