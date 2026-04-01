package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.utils.Constraints;

/**
 * A {@link SingleChildParent} which is an abstract base for creating custom widgets by composing other widgets
 * <p>
 * Subclasses must override the {@link #build()} method and return a custom widget tree
 */
@WidgetBuilder
public abstract class StatelessCompose extends SingleChildParent {
  protected StatelessCompose() {
    super(new StatelessComposeConfig(), Empty.create());
  }

  protected StatelessCompose(StatelessComposeConfig config) {
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

  /** Returns the custom {@link Widget} tree to use as a child of this {@link StatelessCompose} */
  protected abstract Widget build();
}
