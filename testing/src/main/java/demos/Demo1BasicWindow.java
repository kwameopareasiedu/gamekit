package demos;

import dev.gamekit.core.Application;
import dev.gamekit.core.Scene;

import static dev.gamekit.core.Entity.EmptyState;

/**
 * This demo shows how GameKit is used its simplest form.
 * <p>
 * This demo performs the following actions:
 * <ul>
 *   <li>Creates a <b>windowed</b> {@link Application application} with a 800x600 resolution</li>
 *   <li>Creates a simple {@link Scene scene}</li>
 *   <li>Launches the application</li>
 * </ul>
 */
public class Demo1BasicWindow extends Scene<EmptyState> {
  public Demo1BasicWindow() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application("Demo 1 - Basic Window") { };
    game.loadScene(new Demo1BasicWindow());
    game.run();
  }

  @Override
  protected EmptyState createState() {
    return new EmptyState();
  }
}
