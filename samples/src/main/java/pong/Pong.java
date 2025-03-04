package pong;

import dev.gamekit.Config;
import dev.gamekit.Game;

public class Pong extends Game {
  public Pong() {
    super(
      new Config.Builder()
        .setTitle("New Age Pong")
        .setWindowWidth(1280)
        .setWindowHeight(720)
        .build()
    );
  }

  @Override
  protected void start() {
    super.start();
  }

  public static void main(String[] args) throws InterruptedException {
    Pong game = new Pong();
    game.run();


//    if (Input.isKeyPressed(KeyEvent.VK_UP)) {
//      window.screen.setColor(Color.RED);
//    } else if (Input.isKeyPressed(KeyEvent.VK_RIGHT)) {
//      window.screen.setColor(Color.BLUE);
//    } else if (Input.isKeyPressed(KeyEvent.VK_DOWN)) {
//      window.screen.setColor(Color.YELLOW);
//    } else if (Input.isKeyPressed(KeyEvent.VK_LEFT)) {
//      window.screen.setColor(Color.GREEN);
//    } else {
//      window.screen.setColor(Color.WHITE);
//    }
//
//    window.screen.fillRoundRect(-100, -75, 200, 150, 5, 5);

  }
}
