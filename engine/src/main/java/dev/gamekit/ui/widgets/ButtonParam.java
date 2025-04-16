package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;
import dev.gamekit.ui.Spacing;
import dev.gamekit.ui.events.MouseEvent;

import java.awt.image.BufferedImage;

public class ButtonParam extends SingleChildParentParam {
  public static Param<ButtonParam> spacing(Spacing value) {
    return new Param<>("spacing", value);
  }

  public static Param<ButtonParam> defaultBackground(BufferedImage value) {
    return new Param<>("defaultBackground", value);
  }

  public static Param<ButtonParam> hoverBackground(BufferedImage value) {
    return new Param<>("hoverBackground", value);
  }

  public static Param<ButtonParam> pressedBackground(BufferedImage value) {
    return new Param<>("pressedBackground", value);
  }

  public static Param<ButtonParam> mouseListener(MouseEvent.Listener value) {
    return new Param<>("mouseListener", value);
  }
}
