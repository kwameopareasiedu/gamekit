package dev.gamekit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link WidgetBuilder} marks a {@code Widget} class as needing a generated config builder class
 * <p>
 * PS: Compilation will fail if this annotation is used on non-Widget classes
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface WidgetBuilder { }
