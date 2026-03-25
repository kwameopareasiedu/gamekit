package dev.gamekit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** {@link CustomWidgetBuilder} is a {@link WidgetBuilder} for custom widgets in consumer applications */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface CustomWidgetBuilder { }
