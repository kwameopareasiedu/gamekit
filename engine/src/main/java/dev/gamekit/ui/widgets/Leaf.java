package dev.gamekit.ui.widgets;

/**
 * A leaf is a {@link Widget} which has no children.
 * <p>
 * Common functionality for widgets with no children will be implemented here instead of individually in subclasses
 */
public abstract class Leaf extends Widget {
  public Leaf(Config config) {
    super(config);
  }
}
