import dev.gamekit.core.Application;
import dev.gamekit.core.Input;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Window;
import dev.gamekit.scene.Scene;

import java.awt.*;

public class Playground extends Scene {
  private boolean isPressed = false;
  private int x = 0, y = 0;

  public Playground() {
    super("Basic Game");
  }

  public static void main(String[] args) {
    dev.gamekit.core.Window.setResolution(Window.Resolution._1024_768);
    Application game = new Application("Simple Game") { };

    game.loadScene(new Playground());
    game.run();
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    var pos = Input.getMousePosition();
    int centerX = dev.gamekit.core.Window.getInstance().getCenterX();
    int centerY = dev.gamekit.core.Window.getInstance().getCenterY();
    x = pos.x - centerX;
    y = centerY - pos.y;
    isPressed = Input.isKeyPressed(Input.KEY_SPACE);
  }

  @Override
  public void onRender() {
    // Clear the screen with black
    Renderer.setColor(Color.DARK_GRAY);
    Renderer.clear();

    // Draw a red or blue square based on if the space bar is pressed
    Renderer.setColor(isPressed ? Color.RED : Color.BLUE);
    Renderer.fillRect(x, y, 200, 200);
    super.onRender();
  }
}
