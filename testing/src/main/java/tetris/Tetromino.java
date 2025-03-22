package tetris;

import java.awt.*;

import static dev.gamekit.utils.Math.cycle;
import static tetris.Utils.getIndex;

public class Tetromino {
  public static final Tetromino O_PIECE = new Tetromino(
    2, Color.YELLOW,
    new int[][]{
      new int[]{
        1, 1,
        1, 1 },
      new int[]{
        1, 1,
        1, 1 },
      new int[]{
        1, 1,
        1, 1 },
      new int[]{
        1, 1,
        1, 1 },
    },
    new int[][]{
      new int[]{ 0, 0, 1, 1 },
      new int[]{ 0, 0, 1, 1 },
      new int[]{ 0, 0, 1, 1 },
      new int[]{ 0, 0, 1, 1 },
    }
  );

  public static final Tetromino I_PIECE = new Tetromino(
    4, Color.CYAN,
    new int[][]{
      new int[]{
        0, 0, 0, 0,
        1, 1, 1, 1,
        0, 0, 0, 0,
        0, 0, 0, 0 },
      new int[]{
        0, 0, 1, 0,
        0, 0, 1, 0,
        0, 0, 1, 0,
        0, 0, 1, 0 },
      new int[]{
        0, 0, 0, 0,
        0, 0, 0, 0,
        1, 1, 1, 1,
        0, 0, 0, 0 },
      new int[]{
        0, 1, 0, 0,
        0, 1, 0, 0,
        0, 1, 0, 0,
        0, 1, 0, 0 },
    },
    new int[][]{
      new int[]{ 1, 0, 1, 3 },
      new int[]{ 0, 2, 3, 2 },
      new int[]{ 2, 0, 2, 3 },
      new int[]{ 0, 1, 3, 1 },
    }
  );

  public static final Tetromino L_PIECE = new Tetromino(
    3, Color.ORANGE,
    new int[][]{
      new int[]{
        0, 0, 1,
        1, 1, 1,
        0, 0, 0 },
      new int[]{
        0, 1, 0,
        0, 1, 0,
        0, 1, 1 },
      new int[]{
        0, 0, 0,
        1, 1, 1,
        1, 0, 0 },
      new int[]{
        1, 1, 0,
        0, 1, 0,
        0, 1, 0 },
    },
    new int[][]{
      new int[]{ 0, 0, 1, 2 },
      new int[]{ 0, 1, 2, 2 },
      new int[]{ 1, 0, 2, 2 },
      new int[]{ 0, 0, 2, 1 },
    }
  );

  public static final Tetromino J_PIECE = new Tetromino(
    3, Color.BLUE,
    new int[][]{
      new int[]{
        1, 0, 0,
        1, 1, 1,
        0, 0, 0 },
      new int[]{
        0, 1, 1,
        0, 1, 0,
        0, 1, 0 },
      new int[]{
        0, 0, 0,
        1, 1, 1,
        0, 0, 1 },
      new int[]{
        0, 1, 0,
        0, 1, 0,
        1, 1, 0 },
    },
    new int[][]{
      new int[]{ 0, 0, 1, 2 },
      new int[]{ 0, 1, 2, 2 },
      new int[]{ 1, 0, 2, 2 },
      new int[]{ 0, 0, 2, 1 },
    }
  );

  public static final Tetromino S_PIECE = new Tetromino(
    3, Color.GREEN,
    new int[][]{
      new int[]{
        0, 1, 1,
        1, 1, 0,
        0, 0, 0 },
      new int[]{
        0, 1, 0,
        0, 1, 1,
        0, 0, 1 },
      new int[]{
        0, 0, 0,
        0, 1, 1,
        1, 1, 0 },
      new int[]{
        1, 0, 0,
        1, 1, 0,
        0, 1, 0 },
    },
    new int[][]{
      new int[]{ 0, 0, 1, 2 },
      new int[]{ 0, 1, 2, 2 },
      new int[]{ 1, 0, 2, 2 },
      new int[]{ 0, 0, 2, 1 },
    }
  );

  public static final Tetromino Z_PIECE = new Tetromino(
    3, Color.RED,
    new int[][]{
      new int[]{
        1, 1, 0,
        0, 1, 1,
        0, 0, 0 },
      new int[]{
        0, 0, 1,
        0, 1, 1,
        0, 1, 0 },
      new int[]{
        0, 0, 0,
        1, 1, 0,
        0, 1, 1 },
      new int[]{
        0, 1, 0,
        1, 1, 0,
        1, 0, 0 },
    },
    new int[][]{
      new int[]{ 0, 0, 1, 2 },
      new int[]{ 0, 1, 2, 2 },
      new int[]{ 1, 0, 2, 2 },
      new int[]{ 0, 0, 2, 1 },
    }
  );

  public static final Tetromino T_PIECE = new Tetromino(
    3, Color.MAGENTA,
    new int[][]{
      new int[]{
        0, 1, 0,
        1, 1, 1,
        0, 0, 0 },
      new int[]{
        0, 1, 0,
        0, 1, 1,
        0, 1, 0 },
      new int[]{
        0, 0, 0,
        1, 1, 1,
        0, 1, 0 },
      new int[]{
        0, 1, 0,
        1, 1, 0,
        0, 1, 0 },
    },
    new int[][]{
      new int[]{ 0, 0, 1, 2 },
      new int[]{ 0, 1, 2, 2 },
      new int[]{ 1, 0, 2, 2 },
      new int[]{ 0, 0, 2, 1 },
    }
  );

  public static final Tetromino[] PIECES = new Tetromino[]{
    O_PIECE, I_PIECE, L_PIECE, J_PIECE, S_PIECE, Z_PIECE, T_PIECE,
  };

  public final int size;
  public final Color color;

  private int row;
  private int col;
  private Orientation orientation;
  private final int[][] states;
  private final int[][] offsets;
  public int[] state;

  private Tetromino(int size, Color color, int[][] states, int[][] offsets) {
    this.size = size;
    this.color = color;
    this.states = states;
    this.offsets = offsets;
    orientation = Orientation.UP;
    state = states[0];
    row = 0;
    col = 4;
  }

  public Tetromino(Tetromino tetromino) {
    this.size = tetromino.size;
    this.color = tetromino.color;
    this.states = tetromino.states;
    this.offsets = tetromino.offsets;
    orientation = Orientation.UP;
    state = states[0];
    row = 0;
    col = 4;
  }

  public int getRow() { return row; }

  public int getCol() { return col; }

  public int[] getOffset() { return offsets[orientation.ordinal()]; }

  public boolean move(CellState[] grid, int gridCols, Direction dir) {
    if (canMove(grid, gridCols, dir)) {
      switch (dir) {
        case RIGHT -> col += 1;
        case DOWN -> row += 1;
        case LEFT -> col -= 1;
      }
      return true;
    } else return false;
  }

  private boolean canMove(CellState[] grid, int gridCols, Direction dir) {
    int destRow = row + (dir == Direction.DOWN ? 1 : 0);
    int destCol = col + (dir == Direction.LEFT ? -1 : dir == Direction.RIGHT ? 1 : 0);
    int[] offset = offsets[orientation.ordinal()];

    for (int row = offset[0]; row <= offset[2]; row++) {
      for (int col = offset[1]; col <= offset[3]; col++) {
        int gridRow = destRow + row;
        int gridCol = destCol + col;
        int stateIdx = getIndex(row, col, size);

        if (gridCol < 0 || gridCol >= gridCols)
          return false;

        int gridIdx = getIndex(gridRow, gridCol, gridCols);
        if (gridIdx >= grid.length || (grid[gridIdx] == CellState.OCCUPIED && state[stateIdx] == 1))
          return false;
      }
    }

    return true;
  }

  public void placeOnGrid(CellState[] grid, Color[] gridColors, int gridCols) {
    int[] offset = offsets[orientation.ordinal()];

    for (int row = offset[0]; row <= offset[2]; row++) {
      for (int col = offset[1]; col <= offset[3]; col++) {
        int gridRow = this.row + row;
        int gridCol = this.col + col;
        int gridIdx = getIndex(gridRow, gridCol, gridCols);
        int stateIdx = getIndex(row, col, size);

        if (state[stateIdx] == 1) {
          grid[gridIdx] = CellState.OCCUPIED;
          gridColors[gridIdx] = color;
        }
      }
    }
  }

  public void rotateCW() { rotate(1); }

  public void rotateCCW() { rotate(-1); }

  private void rotate(int offset) {
    int ord = orientation.ordinal();
    int newOrd = cycle(ord + offset, 0, 3);

    int[] newState = states[newOrd];
    int[] newOffset = offsets[newOrd];
    Orientation newOrientation = Orientation.valueOf(newOrd);

    int width = newOffset[3] - newOffset[1];
    int leftColAfterRotation = col - newOffset[1];
    int rightColAfterRotation = leftColAfterRotation + width;

    if (leftColAfterRotation < 0) {
      col += Math.abs(leftColAfterRotation);
    } else if (rightColAfterRotation >= 10) {
      col -= Math.abs(rightColAfterRotation - 10) + 1;
    }

    orientation = newOrientation;
    state = newState;
  }
}
