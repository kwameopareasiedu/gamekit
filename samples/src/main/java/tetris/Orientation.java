package tetris;

public enum Orientation {
  UP, RIGHT, DOWN, LEFT;

  static Orientation valueOf(int val) {
    return switch (val % Orientation.values().length) {
      case 0 -> UP;
      case 1 -> RIGHT;
      case 2 -> DOWN;
      default -> LEFT;
    };
  }
}
