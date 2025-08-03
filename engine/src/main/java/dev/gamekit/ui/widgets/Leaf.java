package dev.gamekit.ui.widgets;

/**
 * A leaf is a {@link Widget} which has no children
 * <p>
 * Common functionality for widgets with no children will be implemented here instead of
 * individually in said widgets
 */
public abstract class Leaf extends Widget {
  public Leaf(LeafConfig config) {
    super(config);
  }

  public static abstract class LeafConfig extends WidgetConfig { }
}
