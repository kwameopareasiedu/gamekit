package pong;

import dev.gamekit.Game;

public class Pong extends Game {
  public Pong() {
    super("Pong");
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
