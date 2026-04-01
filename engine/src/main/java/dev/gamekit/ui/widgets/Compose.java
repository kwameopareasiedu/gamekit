package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.ui.mixins.WidgetUpdater;
import dev.gamekit.utils.Constraints;

import java.awt.*;

/**
 * A {@link SingleChildParent} which is an abstract base for creating custom widgets by overriding the
 * {@link #build()} method and returning a custom widget tree.
 * <p>
 * Subclasses can maintain some custom internal state update the widget as this state changes
 */
@WidgetBuilder
public abstract class Compose extends SingleChildParent implements WidgetUpdater {
  protected Compose() {
    super(new ComposeConfig(), Empty.create());
  }

  protected Compose(ComposeConfig config) {
    super(config, Empty.create());
  }

  @Override
  protected void performInit() {
    updateChild(build());
    super.performInit();
  }

  @Override
  protected final void performLayout(Constraints constraints) {
    child.layout(constraints);

    computedBounds.setSize(
      child.computedBounds.width,
      child.computedBounds.height
    );

    child.computedBounds.setPosition(0, 0);
  }

  /**
   * Returns the custom {@link Widget} tree of this {@link Compose}
   * <p>
   * This is called when the {@link Compose} is initialized, and again when the {@link #updateUI} method is
   * called in response to some custom state change.
   */
  protected abstract Widget build();

  /**
   * Triggers a re-layout of this widget's subtree and subsequently a re-render
   * <p>
   * This method should be called after the widget's custom internal state changes
   */
  protected void updateUI() {
    updateTree(host, constraints, this::getChild, this::build, this::updateChild, host::triggerRender);
  }
}
