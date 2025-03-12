# Engine Docs

This sections contains the full in-depth documentation of the classes and interactions of classes that make GameKit
function.

## Application

This is the heart of a GameKit program. A game or application must extend this class to do anything with the engine.

`Application` runs a **60fps** fixed-step game loop which processes input, updates and renders the current scene and
runs end-of-frame tasks.

You can override `Application.onDispose()` to perform any cleanup before the application exits.

| Method                                                               | Description                                                                |
|----------------------------------------------------------------------|----------------------------------------------------------------------------|
| `public Application(String name, int screenWidth, int screenHeight)` | Get the current instance of the application                                |
| `public static Application getInstance()`                            | Get the current instance of the application                                |
| `public void run()`                                                  | Starts up the application's  game loop                                     |
| `public void loadScene(Scene scene)`                                 | Queues a scene object which is loaded at the end of the current frame      |
| `public void runOnFrameEnd(FrameEndTask task)`                       | Schedule a task to be executed at the end of the current frame             |
| `public void quit()`                                                 | Signal to the application to end the game loop, run `onDispose()` and exit |

## Window

This is the [JFrame](https://docs.oracle.com/javase/8/docs/api/javax/swing/JFrame.html) window which displays the
application. It's width and height are initialized to the `screenWidth` and `screenHeight` passed to the `Application`
instance.

The `Window` uses a double buffering approach for rendering. Scene draw calls affect the draw buffer after which the
draw buffer is copied to the window's render buffer. This double buffer approach eliminates screen tearing issues.

Application handles the `Window` object internally and exposes
its [Graphics2D](https://docs.oracle.com/javase/8/docs/api/java/awt/Graphics2D.html) object to the current scene to draw
onto.

## Input

Static class responsible for capturing keyboard and mouse inputs for use in the game.

Input exports static constants which map to
Java's [KeyEvent](https://docs.oracle.com/javase/8/docs/api/java/awt/event/KeyEvent.html) constants so they can be used
interchangeably. An example is shown below

```java
import java.awt.event.KeyEvent;

import dev.gamekit.scene.Scene;
import dev.gamekit.core.Input;

/* Omitted code */

public class MyAwesomeGame extends Scene {
  @Override
  public void onUpdate() {
    // Instead of using KeyEvent.VK_SPACE
    Input.isKeyJustPressed(KeyEvent.VK_SPACE);
    
    // You can use Input.KEY_SPACE
    Input.isKeyJustPressed(Input.KEY_SPACE);
  }
}
```

| Method                                                 | Description                                            |
|--------------------------------------------------------|--------------------------------------------------------|
| `public static boolean isKeyPressed(int keyCode)`      | Check if a key is being held down in the current frame |
| `public static boolean isKeyJustPressed(int keyCode)`  | Check if a key just pressed in the current frame       |
| `public static boolean isKeyJustReleased(int keyCode)` | Check if a key just released in the current frame      |
