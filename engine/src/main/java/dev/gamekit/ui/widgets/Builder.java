package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;

/**
 * {@link Builder} is a {@link Compose} which delegates its {@link #build()} method to the provided
 * {@link BuilderDelegate} object
 */
@WidgetBuilder
public class Builder extends Compose {
  @WidgetBuilderField(themable = false, comparable = false)
  public BuilderDelegate delegate;

  public Builder(BuilderConfig config) {
    super(config);
  }

  public static Builder create(BuilderDelegate delegate) {
    return new Builder(
      Widgets.configureBuilder(
        config -> config.delegate = delegate
      )
    );
  }

  @Override
  protected Widget build() {
    return delegate.build();
  }

  /** Contract for an object which builds a {@link Widget} tree */
  public interface BuilderDelegate {
    /** Called to return the custom {@link Widget} tree */
    Widget build();
  }
}
