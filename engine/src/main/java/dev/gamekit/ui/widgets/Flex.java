package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;

import static dev.gamekit.utils.Misc.coalesce;

/** A {@link MultiChildParent} which arranges its children linearly along one axis */
@WidgetBuilder
public abstract class Flex extends MultiChildParent {
  @WidgetBuilderField
  protected Integer gapSize;
  @WidgetBuilderField
  protected MainAxisAlignment mainAxisAlignment;
  @WidgetBuilderField
  protected CrossAxisAlignment crossAxisAlignment;

  public Flex(FlexConfig... config) {
    super(config);
  }

  @Override
  protected void performInit() {
    FlexConfig config = (FlexConfig) super.config;

    this.gapSize = coalesce(config.gapSize, 12);
    this.mainAxisAlignment = coalesce(config.mainAxisAlignment, MainAxisAlignment.START);
    this.crossAxisAlignment = coalesce(config.crossAxisAlignment, CrossAxisAlignment.START);

    super.performInit();
  }
}
