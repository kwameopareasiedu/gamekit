package dev.gamekit.ui.widgets;

import dev.gamekit.ui.Param;
import dev.gamekit.ui.enums.Alignment;

import java.awt.*;

public class TextParam extends WidgetParam {
  public static Param<TextParam> text(String value) {
    return new Param<>("text", value);
  }

  public static Param<TextParam> fontFamily(String value) {
    return new Param<>("fontFamily", value);
  }

  public static Param<TextParam> fontStyle(int value) {
    return new Param<>("fontStyle", value);
  }

  public static Param<TextParam> fontSize(int value) {
    return new Param<>("fontSize", value);
  }

  public static Param<TextParam> color(Color value) {
    return new Param<>("color", value);
  }

  public static Param<TextParam> backgroundColor(Color value) {
    return new Param<>("backgroundColor", value);
  }

  public static Param<TextParam> font(Font value) {
    return new Param<>("font", value);
  }

  public static Param<TextParam> alignment(Alignment value) {
    return new Param<>("alignment", value);
  }

  public static Param<TextParam> vAlignment(Alignment value) {
    return new Param<>("verticalAlignment", value);
  }

  public static Param<TextParam> shadowEnabled(boolean value) {
    return new Param<>("shadowEnabled", value);
  }

  public static Param<TextParam> shadowOffsetX(int value) {
    return new Param<>("shadowOffsetX", value);
  }

  public static Param<TextParam> shadowOffsetY(int value) {
    return new Param<>("shadowOffsetY", value);
  }

  public static Param<TextParam> shadowColor(Color value) {
    return new Param<>("shadowColor", value);
  }
}
