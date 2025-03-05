package tetris;

import dev.gamekit.Entity;

import java.awt.*;
import java.util.Arrays;

import static dev.gamekit.Utils.toInt;

public class Board extends Entity {
  private static final int COLS = 10;
  private static final int ROWS = 20;
  private static final int SLOT_SIZE = 40;
  private static final int BOARD_OFFSET = 100;
  private static final int BOARD_WIDTH = SLOT_SIZE * COLS;
  private static final int BOARD_HEIGHT = SLOT_SIZE * ROWS;
  private static final int BOARD_X = -toInt(0.5 * BOARD_WIDTH) - BOARD_OFFSET;
  private static final int BOARD_Y = -toInt(0.5 * BOARD_HEIGHT);
  private static final Stroke DEFAULT_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  private static final Stroke OUTLINE_STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

  private final boolean[] slots = new boolean[ROWS * COLS];
  private final Color[] slotColors = new Color[ROWS * COLS];

  public Board() {
    super("Board");
    Arrays.fill(slotColors, new Color(0x00000000));
  }

  @Override
  protected void render(Graphics2D g) {
    super.render(g);

    // Draw slots
    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLS; col++) {
        int idx = row * COLS + col;

        if (slots[idx]) {
          g.setColor(slotColors[idx]);
          g.fillRect(BOARD_X + col * SLOT_SIZE, BOARD_Y + row * SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);
        } else g.clearRect(BOARD_X + col * SLOT_SIZE, BOARD_Y + row * SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);
      }
    }

    // Draw board
    g.setColor(Color.GRAY);
    g.setStroke(DEFAULT_STROKE);

    for (int col = 0; col <= COLS; col++) {
      int x = BOARD_X + col * SLOT_SIZE;
      g.drawLine(x, BOARD_Y, x, BOARD_Y + BOARD_HEIGHT);
    }

    for (int row = 0; row <= ROWS; row++) {
      int y = BOARD_Y + row * SLOT_SIZE;
      g.drawLine(BOARD_X, y, BOARD_X + BOARD_WIDTH, y);
    }

    g.setColor(Color.WHITE);
    g.setStroke(OUTLINE_STROKE);
    g.drawRect(BOARD_X, BOARD_Y, BOARD_WIDTH, BOARD_HEIGHT);
  }
}
