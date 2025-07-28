package dev.gamekit.ui;

import dev.gamekit.ui.widgets.Field;

import java.awt.*;

/** Represents border properties of {@link Field} widgets */
public record BorderData(
  double size,
  double radius,
  Color color
) {
  public BorderData() {
    this(0, 0, Color.WHITE);
  }

  public BorderData(double width) {
    this(width, 0, Color.WHITE);
  }
}
