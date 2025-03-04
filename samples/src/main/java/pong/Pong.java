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
  }
}
