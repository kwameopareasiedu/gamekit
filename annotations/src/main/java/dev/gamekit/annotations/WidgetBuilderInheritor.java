package dev.gamekit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link WidgetBuilderInheritor} annotates widgets which don't have any fields marked with
 * {@link WidgetBuilderField} but inherit from another widget which do, allowing for their config
 * builders to be generated
 * <p>
 * PS: Compilation will fail if this annotation is used on member fields of non-Widget classes
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface WidgetBuilderInheritor { }
