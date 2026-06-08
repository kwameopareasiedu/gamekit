package dev.gamekit.ui.widgets;

import dev.gamekit.utils.Constraints;

/**
 * A {@link SingleChildParent} which is an abstract base for creating custom widgets by composing other widgets
 * <p>
 * Subclasses must override the {@link #build()} method and return a custom widget tree
 */
public abstract class Compose extends SingleChildParent {
  private boolean updatedChild = false;

  protected Compose(String key) {
    super(key, ignored -> { }, Empty.create());
  }

  protected Compose() {
    super(null, ignored -> { }, Empty.create());
  }

  @Override
  protected void performInit() {
    if (!updatedChild) {
      updateChild(build());
      updatedChild = true;
    }

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

  /** Returns the custom {@link Widget} tree to use as a child of this {@link Compose} */
  protected abstract Widget build();
}
