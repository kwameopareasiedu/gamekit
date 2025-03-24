package dev.gamekit.ui.widgets;

import dev.gamekit.utils.Constants;

import java.awt.*;
import java.util.List;

/**
 * A parent is a special {@link Widget} which can contain children.
 * <p>
 * Since a scene can only have one root widget, parents are responsible
 * for rendering their children unto themselves which in turn are rendered
 * to their parent or the window
 * <p>
 * Parent widgets can be children of other parent widgets with no change
 * to how layout is performed due to the box-constraint model used.
 */
public abstract class Parent extends Widget {
  @Override
  public final void onRender(Graphics2D g) {
    List<Widget> children = getChildren();

    g.setColor(Constants.TRANSPARENT_COLOR);
    g.fillRect(0, 0, computedSize.width, computedSize.height);

    // The children are drawn in the container's image instead of calling their onRender.
    // This allows for clipping if the child's bounds fall outside that of the container.
    children.forEach(child ->
      g.drawImage(
        child.getAppearance().image,
        child.getComputedPosition().x,
        child.getComputedPosition().y,
        null
      )
    );
  }

  /**
   * Abstract method which returns the list of children to iterate over.
   * This method exists because some containers may either have a
   * single child or multiple children.
   */
  protected abstract List<Widget> getChildren();
}
