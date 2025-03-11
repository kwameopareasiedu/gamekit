# GameKit

A 2D Java game engine for creating simple games fast. GameKit is based on
Java [Swing](https://docs.oracle.com/javase/tutorial/uiswing/index.html) and doesn't use OpenGL hence rendering is CPU
based and not GPU based.

GameKit is in no way a AAA engine and has limitations due to it not using OpenGL, but performance is decent enough for
small indie games.

## Installation

To use GameKit, include it as a dependency:

[//]: # (Include dependency syntax after publishing)

## Getting Started

Here's a small snippet of how to use GameKit for a basic game.

This is a simple GameKit scene which renders a red or blue box based on whether the space bar is pressed. The GameKit
application is created in the `main()` method and loads an instance of our scene.

```java
import dev.gamekit.core.*;
import dev.gamekit.scene.Scene;

import java.awt.*;
import java.awt.event.KeyEvent;

public class GameScene extends Scene {
  public GameScene() {
    super("Game Scene");
  }

  public static void main(String[] args) throws InterruptedException {
    // Create a new game application
    Application game = new Application(
      new Config.Builder()
        .setTitle("Simple Game")
        .setWindowWidth(800)
        .setWindowHeight(600)
        .build()
    ) { };

    // Load an instance of our Scene class
    game.loadScene(new GameScene());

    // Run the game application
    game.run();
  }

  @Override
  public void onRender(Graphics2D g) {
    // Clear the screen with black
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, 800, 600);

    // Draw a red or blue square based on if the space bar is pressed
    g.setColor(Input.isKeyPressed(KeyEvent.VK_SPACE) ? Color.RED : Color.BLUE);
    g.fillRect(300, 200, 200, 200);
  }
}
```

After running the main method, you should see your window similar to this:

<img src="docs/assets/simple-game.png" width="404">

## Samples

The project uses a multi-module maven architecture. There's an included `samples` module containing small games built with the engine. 

These include:

1. Basic game
2. Tetris (No audio)

## Contributors
1. [Kwame Opare Asiedu](https://github.com/kwameopareasiedu)