package dev.gamekit.ui.widgets;

/**
 * A parent is a {@link Widget} which can contain one or more children.
 * <p>
 * Since a scene can only have one root widget, parents are responsible
 * for rendering their children unto themselves which in turn are rendered
 * to their parent or the window
 * <p>
 * Parents can be children of other parent widgets with no change
 * to how layout is performed due to the box-constraint model used.
 */
public abstract class Parent extends Widget {
}
