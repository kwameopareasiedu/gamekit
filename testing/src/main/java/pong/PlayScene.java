package pong;

import dev.gamekit.components.RigidBody;
import dev.gamekit.core.IO;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.widgets.Align;
import dev.gamekit.ui.widgets.Padding;
import dev.gamekit.ui.widgets.Text;
import dev.gamekit.ui.widgets.Widget;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayScene extends Scene {
  public static final BufferedImage BOARD_IMG = IO.getResourceImage("pong/board.png");

  public PlayScene() {
    super("Play Scene");
  }

  @Override
  protected void start() {
    super.start();

    RigidBody.DEBUG_DRAW = true;

    addChild(new Ball());

    addChild(new Wall(Wall.Type.TOP));
    addChild(new Wall(Wall.Type.RIGHT));
    addChild(new Wall(Wall.Type.BOTTOM));
    addChild(new Wall(Wall.Type.LEFT));

    addChild(new Paddle(Paddle.Type.LEFT));
    addChild(new Paddle(Paddle.Type.RIGHT));
  }

  @Override
  protected void render() {
    super.render();
    Renderer.clear(Color.BLACK);
    Renderer.drawImage(BOARD_IMG, 0, 0, BOARD_IMG.getWidth(), BOARD_IMG.getHeight());
  }

  @Override
  protected Widget createUI() {
    return Align.create(
      Align.config().horizontalAlignment(Alignment.CENTER),
      Padding.create(
        Padding.config().padding(32, 32, 32, 32),
        Text.create(
          Text.config().fontSize(72).fontStyle(Text.BOLD).horizontalAlignment(Alignment.CENTER),
          "Score"
        )
      )
    );
  }
}
