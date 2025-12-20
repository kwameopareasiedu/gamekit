package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;

/** A {@link MultiChildParent} which arranges its children linearly along one axis */
@WidgetBuilder
public abstract class Flex extends MultiChildParent {
  @WidgetBuilderField(fallback = "10")
  protected Integer gapSize;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.enums.MainAxisAlignment.START")
  protected MainAxisAlignment mainAxisAlignment;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.enums.CrossAxisAlignment.START")
  protected CrossAxisAlignment crossAxisAlignment;

  public Flex(FlexConfig... config) {
    super(config);
  }
}
