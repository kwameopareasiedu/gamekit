package dev.gamekit.ui;

/** Represents the position of a {@link Node} in screen space */
public class Position {
  public int x;
  public int y;

  /**
   * Creates a new position, copying values from another position object
   * @param position The position object to copy
   */
  public Position(Position position) {
    this(position.x, position.y);
  }

  /**
   * Creates a new position with explicit values for x-coordinate and y-coordinate
   * @param x X-Coordinate of this position
   * @param y Y-Coordinate of this position
   */
  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Copies the x-coordinate and y-coordinate from another position object
   * @param position The position object
   */
  public void set(Position position) {
    set(position.x, position.y);
  }

  /**
   * Sets the x-coordinate and y-coordinate
   * @param x-coordinate New x-coordinate
   * @param y-coordinate New y-coordinate
   */
  public void set(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return String.format(getClass().getName() + "[x-coordinate=%d,y-coordinate=%d]", x, y);
  }
}
