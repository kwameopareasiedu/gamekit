package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.utils.ValueGetter;

/**
 * {@link Builder} is a {@link Compose} which delegates its {@link #build()} method to the provided
 * delegate object
 */
@WidgetBuilder
public class Builder extends Compose {
  @WidgetBuilderField(themable = false, comparable = false)
  public ValueGetter<Widget> delegate;

  public Builder(BuilderConfig config) {
    super(config);
  }

  public static Builder create(ValueGetter<Widget> delegate) {
    return new Builder(
      Widgets.configureBuilder(
        config -> config.delegate = delegate
      )
    );
  }

  @Override
  protected Widget build() {
    return delegate.get();
  }
}
