package dev.gamekit.ui.widgets;

import dev.gamekit.utils.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** A parent which contains multiple child {@link Widget}s */
public abstract class MultiChildParent extends Parent {
  protected final List<Widget> children;

  public MultiChildParent(Widget... children) {
    if (children == null)
      throw new IllegalArgumentException("Children of MultiChildParent cannot be null");
    this.children = new ArrayList<>(List.of(children));
    this.children.forEach(c -> c.setParent(this));
  }

  @Override
  public final void performRender(Graphics2D g) {
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

  public List<Widget> getChildren() { return children; }

  public final void updateChild(Widget newChild, int index) {
    if (index >= children.size())
      throw new ArrayIndexOutOfBoundsException(
        String.format(
          "Children length: %d, Index: %d",
          children.size(), index
        )
      );
    children.get(index).setParent(null);
    children.set(index, newChild);
    newChild.setParent(this);
  }
}
