package dev.gamekit.utils;

import java.awt.*;

/** Represents an (x,y) position */
public class Position {
  public int x;
  public int y;

  public Position() {
    this(0, 0);
  }

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /** Copy constructor for this class */
  public Position(Position position) {
    this(position.x, position.y);
  }

  /** Creates a position from a {@link Point} */
  public Position(Point pt) {
    this(pt.x, pt.y);
  }

  public void set(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /** Copies values from another position object */
  public void set(Position position) {
    set(position.x, position.y);
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  @Override
  public String toString() {
    return String.format("%s[x=%d, y=%d]", getClass().getName(), x, y);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Position posObject
      && x == posObject.x
      && y == posObject.y;
  }
}
