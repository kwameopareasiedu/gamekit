package pong;

import dev.gamekit.Input;
import dev.gamekit.Scene;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PlayScene extends Scene {
  @Override
  protected void render(Graphics2D g) {
    super.render(g);

    if (Input.isKeyPressed(KeyEvent.VK_UP)) {
      g.setColor(Color.RED);
    } else if (Input.isKeyPressed(KeyEvent.VK_RIGHT)) {
      g.setColor(Color.BLUE);
    } else if (Input.isKeyPressed(KeyEvent.VK_DOWN)) {
      g.setColor(Color.YELLOW);
    } else if (Input.isKeyPressed(KeyEvent.VK_LEFT)) {
      g.setColor(Color.GREEN);
    } else {
      g.setColor(Color.WHITE);
    }

    g.fillRoundRect(-100, -75, 200, 150, 5, 5);
  }
}
