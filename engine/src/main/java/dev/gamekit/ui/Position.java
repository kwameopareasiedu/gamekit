package dev.gamekit.ui;

import dev.gamekit.ui.widgets.Widget;

/** Represents the position of a {@link Widget} in screen space */
public class Position {
  public int x;
  public int y;

  /** Creates a new position, copying values from another position object */
  public Position(Position position) { this(position.x, position.y); }

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /** Copies the x-coordinate and y-coordinate from another position object */
  public void set(Position position) { set(position.x, position.y); }

  public void set(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return String.format(getClass().getName() + "[x-coordinate=%d,y-coordinate=%d]", x, y);
  }
}
