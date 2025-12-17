package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;

import java.awt.*;

/** A parent which contains only one child {@link Widget} */
@WidgetBuilder
public abstract class SingleChildParent extends Parent {
  @WidgetBuilderField
  protected Widget child;

  public SingleChildParent(SingleChildParentConfig... config) {
    super(config);
  }

  @Override
  protected void performInit() {
    super.performInit();

    if (((SingleChildParentConfig) config).child == null)
      throw new IllegalArgumentException("Child of SingleChildParent cannot be null");

    this.child = ((SingleChildParentConfig) config).child;

    child.parent = this;
    child.init(host);
  }

  @Override
  protected void performPostLayout() {
    child.postLayout();
  }

  @Override
  protected void performRender(Graphics2D g) {
    renderAppearance(g);
    child.render(g);
  }

  @Override
  protected void performUnmount() {
    child.unmount();
  }

  public Widget getChild() {
    return child;
  }

  public final void updateChild(Widget newChild) {
    child.parent = null;
    child = newChild;
    newChild.parent = this;
  }

  //  public abstract static class SingleChildParentConfig extends ParentConfig { }
}
