import dev.gamekit.core.*;
import dev.gamekit.utils.Config;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.Resolution;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Playground extends Scene {
  private static final Color CLEAR_COLOR = new Color(0xff1f1f1f);

  int x = 0, y = 0;
  String text = "Kwame";
  BufferedImage bufferedImage = IO.loadImageResource("square-img.jpg");

  public Playground() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Config("Playground", Resolution.SVGA, true)
    ) { };
    game.loadScene(new Playground());
    game.run();
  }

  @Override
  public void onUpdate() {
    super.onUpdate();

    if (Input.isButtonPressed(Input.BUTTON_LMB)) {
      Position mousePos = Input.getMousePosition();
      Position pt = Camera.screenToWorldPoint(mousePos.x, mousePos.y);
      x = pt.x;
      y = pt.y;
    }
  }

  @Override
  public void onRender() {
    super.onRender();
    Renderer.setColor(CLEAR_COLOR);
    Renderer.clear();
    Renderer.setColor(Color.RED);
    Renderer.fillRoundRect(-50 + x, -50 + y, 100, 100, 10, 10);
    Renderer.setColor(Color.YELLOW);
    Renderer.fillRoundRect(50 + x, -50 + y, 100, 100, 10, 10);
    Renderer.setColor(Color.GREEN);
    Renderer.fillRoundRect(50 + x, 50 + y, 100, 100, 10, 10);
    Renderer.setColor(Color.CYAN);
    Renderer.fillRoundRect(-50 + x, 50 + y, 100, 100, 10, 10);
  }
}
