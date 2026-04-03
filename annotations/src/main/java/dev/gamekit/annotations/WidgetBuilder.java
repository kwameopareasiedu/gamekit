package dev.gamekit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link WidgetBuilder} marks an internal {@code Widget} class as needing a generated config builder class
 * <p>
 * PS: For custom widgets, use the {@link CustomWidgetBuilder} annotation instead
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface WidgetBuilder { }
