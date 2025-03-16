package basic;

import dev.gamekit.core.Application;
import dev.gamekit.core.Input;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Window;
import dev.gamekit.scene.Scene;

import java.awt.*;

public class BasicGame extends Scene {
  private boolean isPressed = false;

  public BasicGame() {
    super("Basic Game");
  }

  public static void main(String[] args) {
    Application game = new Application("Simple Game") { };
    Window.getInstance().setSize(1280, 720);
    Window.getInstance().maximize();

    game.loadScene(new BasicGame());
    game.run();
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    isPressed = Input.isKeyPressed(Input.KEY_SPACE);
  }

  @Override
  public void onRender() {
    super.onRender();
    // Clear the screen with black
    Renderer.setColor(Color.BLACK);
    Renderer.clear();

    // Draw a red or blue square based on if the space bar is pressed
    Renderer.setColor(isPressed ? Color.RED : Color.BLUE);
    Renderer.fillRect(0, 0, 200, 200);
  }
}
