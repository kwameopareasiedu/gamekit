import dev.gamekit.core.*;
import dev.gamekit.settings.ImageInterpolation;
import dev.gamekit.settings.Resolution;
import dev.gamekit.settings.Settings;
import dev.gamekit.utils.Picture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Playground extends Scene {
  private static final Picture SIMPLE = IO.getImage("keep-it-simple-light.jpg");
  private static final Picture PLANET_FALL = IO.getImage("planetfall-artwork.jpg");
  private static final Picture MASK = IO.getImage("planetfall-logo-mask.png");

  private final BufferedImage target = new BufferedImage(
    PLANET_FALL.getWidth(), PLANET_FALL.getHeight(), BufferedImage.TYPE_INT_ARGB
  );
  private boolean captureImage = false;

  public Playground() {
    super("Playground");
  }

  public static void main(String[] args) {
    Application game = new Application(new Settings("Playground", Resolution.FULL_HD)) { };
    game.loadScene(new Playground());
    game.run();
  }

  @Override
  protected void update() {
    if (Input.isKeyDown(Input.KEY_SPACE))
      captureImage = true;
  }

  @Override
  public void render() {
    Renderer.clear(Color.BLACK);
    Renderer.drawImage(SIMPLE, 0, 0, SIMPLE.getWidth(), SIMPLE.getHeight());
    Renderer.drawImage(PLANET_FALL, 0, 0, PLANET_FALL.getWidth(), PLANET_FALL.getHeight())
      .withInterpolation(ImageInterpolation.BICUBIC).withMask(MASK).withTarget(captureImage ? target : null);


    if (captureImage) {
      Application.getInstance().scheduleTask(500, () -> {
        try {
          File outFile = new File("testing/target/image.png");
          ImageIO.write(target, "png", outFile);
          logger.debug("Captured!");
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

      captureImage = false;
    }
  }
}
