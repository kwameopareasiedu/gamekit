package dev.gamekit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link WidgetBuilderField} marks fields of a {@code Widget} as needing to be part of the
 * generated builder class.
 * <p>
 * PS: Compilation will fail if this annotation is used on member fields of non-Widget classes
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface WidgetBuilderField { }
