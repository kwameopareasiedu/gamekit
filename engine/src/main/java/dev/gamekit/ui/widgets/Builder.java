package dev.gamekit.ui.widgets;

import dev.gamekit.utils.ValueGetter;

/** {@link Builder} is a {@link Compose} which delegates its {@link #build()} method to the provided delegate object */
public class Builder extends Compose {
  public ValueGetter<Widget> delegate;

  public Builder(ValueGetter<Widget> delegate) {
    this.delegate = delegate;
  }

  public static Builder create(ValueGetter<Widget> delegate) {
    return new Builder(delegate);
  }

  @Override
  protected Widget build() {
    return delegate.get();
  }
}
