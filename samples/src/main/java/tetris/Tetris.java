package tetris;

import dev.gamekit.Application;
import dev.gamekit.Config;
import dev.gamekit.Scene;
import dev.gamekit.Window;

import java.awt.*;

public class Tetris extends Scene {
  static final int PADDING_X = 50;
  static final int PADDING_Y = 85;
  static final int COLS = 10;
  static final int ROWS = 20;
  static final int CELL_SIZE = 40;
  static final int BOARD_W = CELL_SIZE * COLS;
  static final int BOARD_H = CELL_SIZE * ROWS;
  static final Color CLEAR_COLOR = new Color(0xff333333);
  static final Color TRANSPARENT = new Color(0x00000000);
  static final Stroke STROKE_DEFAULT = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  static final Stroke STROKE_OUTLINE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  final CellState[][] cellStates = new CellState[ROWS][COLS];
  final Color[][] cellColors = new Color[ROWS][COLS];

  public Tetris() {
    super("Tetris");

    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLS; col++) {
        cellStates[row][col] = CellState.FREE;
        cellColors[row][col] = TRANSPARENT;
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Application game = new Application(
      new Config.Builder()
        .setTitle("GameKit Tetris")
        .setWindowWidth(720)
        .setWindowHeight(960)
        .build()
    ) { };
    game.loadScene(new Tetris());
    game.run();
  }

  @Override
  protected void onRender(Graphics2D g) {
    super.onRender(g);

    g.setColor(CLEAR_COLOR);
    g.fillRect(0, 0, Window.getInstance().getWidth(), Window.getInstance().getHeight());
    renderBoard(g);
  }

  private void renderBoard(Graphics2D g) {
    g.translate(PADDING_X, PADDING_Y);

    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLS; col++) {
        CellState state = cellStates[row][col];

        g.setColor(state == CellState.FIXED ? cellColors[row][col] : TRANSPARENT);
        g.fillRect(col * Cell.SIZE, row * Cell.SIZE, Cell.SIZE, Cell.SIZE);
      }
    }

    g.setColor(Color.GRAY);
    g.setStroke(STROKE_DEFAULT);

    for (int col = 0; col <= COLS; col++) {
      int x = col * Cell.SIZE;
      g.drawLine(x, 0, x, BOARD_H);
    }

    for (int row = 0; row <= ROWS; row++) {
      int y = row * Cell.SIZE;
      g.drawLine(0, y, BOARD_W, y);
    }

    g.setColor(Color.WHITE);
    g.setStroke(STROKE_OUTLINE);
    g.drawRect(0, 0, BOARD_W, BOARD_H);

    g.translate(-PADDING_X, -PADDING_Y);
  }

  enum CellState {
    FREE, // Indicates an unoccupied cell
    FIXED, // Indicates a cell occupied by a block
  }
}
