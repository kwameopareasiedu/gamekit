package dev.gamekit.ui.widgets;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.ui.enums.Alignment;

/** An {@link SingleChildParent} which centers its single child within itself */
@WidgetBuilder
public class Center extends Align {
  public Center(Widget child) {
    super(
      CenterConfig.horizontalAlignment(Alignment.CENTER),
      CenterConfig.verticalAlignment(Alignment.CENTER),
      CenterConfig.child(child)
    );
  }

  public static Center create(Widget child) {
    return new Center(child);
  }

  @Override
  public boolean stateEquals(Widget widget) {
    return widget instanceof Center;
  }
}
