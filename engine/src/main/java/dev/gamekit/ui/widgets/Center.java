package dev.gamekit.ui.widgets;

import dev.gamekit.ui.enums.Alignment;

/** An {@link SingleChildParent} which centers its single child within itself */
public class Center extends Align {
  public Center(Widget child) {
    super(
      Widgets.configureAlign(props -> {
        props.horizontalAlignment = Alignment.CENTER;
        props.verticalAlignment = Alignment.CENTER;
      }),
      child);
  }

  public static Center create(Widget child) {
    return new Center(child);
  }
}
