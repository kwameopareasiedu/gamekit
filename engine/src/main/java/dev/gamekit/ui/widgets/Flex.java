package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;

/** A {@link MultiChildParent} which arranges its children linearly along one axis */
@WidgetBuilder
public abstract class Flex extends MultiChildParent {
  @WidgetBuilderField(fallback = "10")
  public Integer gapSize;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.enums.MainAxisAlignment.START")
  public MainAxisAlignment mainAxisAlignment;
  @WidgetBuilderField(fallback = "dev.gamekit.ui.enums.CrossAxisAlignment.START")
  public CrossAxisAlignment crossAxisAlignment;

  public Flex(String key, FlexConfig config, Widget... children) {
    super(key, config, children);
  }
}
