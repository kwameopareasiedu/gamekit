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

  @Override
  public String toString() {
    return String.format(
      getClass().getName() + "[size=%.2f,radius=%.2f,color=%s]",
      size, radius, color
    );
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof BorderData borderData &&
      size == borderData.size &&
      radius == borderData.radius &&
      color == borderData.color;
  }
}
