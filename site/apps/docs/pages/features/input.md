# Input

_[Back To Features](./overview.md)_

Detecting player input in GameKit is done using the static `Input` class. `Input` detects and gathers **keyboard** and
**mouse** inputs to be used during the update phase of the game loop.

`Input` allows you to check the following states:

- Keyboard key has just been pressed down
- Keyboard key is being held
- Keyboard key has just been released
- Mouse button has just been pressed down
- Mouse button is being held
- Mouse button has just been released

## Static Methods

| Method                | Description                                                                        |
|-----------------------|------------------------------------------------------------------------------------|
| `isKeyDown`           | Checks if a key has just been pressed in the current frame                         |
| `isKeyPressed`        | Checks if a key is being held down                                                 |
| `isKeyReleased`       | Checks if a key has just been released in the current frame                        |
| `isButtonDown`        | Checks if a button has just been pressed in the current frame                      |
| `isButtonPressed`     | Checks if a button is being held down                                              |
| `isButtonReleased`    | Checks if a button has just been released in the current frame                     |
| `getPressedCharacter` | Returns the character of the key which was just pressed                            |
| `getPressedKeyCode`   | Returns the code of the key which was just pressed                                 |
| `getMousePosition`    | Returns the mouse position in screen space (I.e. (0, 0) at the top-left of screen) |

## Input Sample

The sample below showcases how `Input` methods are used. When the left mouse button or space bar is pressed, held and
released, it prints to the console.

```java
import dev.gamekit.core.Application;
import dev.gamekit.core.Input;
import dev.gamekit.core.Scene;

public class InputSample extends Scene {
  public InputSample() {
    super("Main Scene");
  }

  public static void main(String[] args) {
    Application game = new Application("Input Sample") { };
    game.loadScene(new InputSample());
    game.run();
  }

  @Override
  protected void update() {
    if (Input.isButtonDown(Input.BUTTON_LMB)) {
      logger.debug("Left Mouse Button pressed");
    } else if (Input.isButtonPressed(Input.BUTTON_LMB)) {
      logger.debug("Left Mouse Button is being held");
    } else if (Input.isButtonReleased(Input.BUTTON_LMB)) {
      logger.debug("Left Mouse Button released");
    }

    if (Input.isKeyDown(Input.KEY_SPACE)) {
      logger.debug("Space Bar pressed");
    } else if (Input.isKeyPressed(Input.BUTTON_LMB)) {
      logger.debug("Space Bar is being held");
    } else if (Input.isKeyReleased(Input.BUTTON_LMB)) {
      logger.debug("Space Bar released");
    }
  }
}
```
