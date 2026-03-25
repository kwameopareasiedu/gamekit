package dev.gamekit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link CustomWidgetBuilderField} is a {@link WidgetBuilderField} for fields of a custom widget annotated with
 * {@link CustomWidgetBuilder}
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface CustomWidgetBuilderField {
  /** A fully qualified declaration of the fallback value to set for the annotated field */
  String fallback() default "";

  /** Indicates that the annotated field is used in state comparison in the generated builder class equals method */
  boolean comparable() default true;
}
