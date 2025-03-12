package basic;

import dev.gamekit.core.Application;
import dev.gamekit.core.Input;
import dev.gamekit.scene.Scene;

import java.awt.*;

public class BasicGame extends Scene {
  public BasicGame() {
    super("Basic Game");
  }

  public static void main(String[] args) throws InterruptedException {
    // Create a new game application
    Application game = new Application("Simple Game", 800, 600) { };
    // Load an instance of our Scene class
    game.loadScene(new BasicGame());
    // Run the game application
    game.run();
  }

  @Override
  public void onRender(Graphics2D g) {
    // Clear the screen with black
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, 800, 600);

    // Draw a red or blue square based on if the space bar is pressed
    g.setColor(Input.isKeyPressed(Input.KEY_SPACE) ? Color.RED : Color.BLUE);
    g.fillRect(300, 200, 200, 200);
  }
}
