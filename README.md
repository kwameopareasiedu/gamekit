# GameKit

A 2D Java game engine for creating simple games fast. GameKit is based on
Java [Swing](https://docs.oracle.com/javase/tutorial/uiswing/index.html) and doesn't use OpenGL hence rendering is CPU
based and not GPU based.

GameKit is in no way a AAA engine and has limitations due to it not using OpenGL, but performance is decent enough for
small games.

## Full Documentation

Read the full engine documentation [here](./ENGINE.md).

## Installation

Gamekit is distributed as a Maven dependency on Github packages. To include in your project add its dependency to your `pom.xml`

```xml

<project>
  <!-- Project config -->

  <repositories>
    <!-- Other repositories -->
    <repository>
      <id>github</id>
      <name>Github Packages</name>
      <url>
        https://kwameopareasiedu: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@maven.pkg.github.com/kwameopareasiedu/gamekit
      </url>
    </repository>
  </repositories>

  <dependencies>
    <!-- Other dependencies -->
    <dependency>
      <groupId>dev.gamekit</groupId>
      <artifactId>engine</artifactId>

      <!--Replace VERSION with the desired version number -->
      <version>VERSION</version>
    </dependency>
  </dependencies>
</project>
```

Then sync your project in your IDE or run `mvn install` in the terminal.

## Getting Started

Here's a small snippet of how to use GameKit for a basic game.

This is a simple GameKit scene which renders a red or blue box based on whether the space bar is pressed. The GameKit
application is created in the `main()` method and loads an instance of our scene.

```java
import dev.gamekit.core.Application;
import dev.gamekit.core.Input;
import dev.gamekit.core.Renderer;
import dev.gamekit.scene.Scene;

import java.awt.*;

public class BasicGame extends Scene {
  private boolean isPressed = false;

  public BasicGame() {
    super("Basic Game");
  }

  public static void main(String[] args) {
    // Create a new game application
    Application game = new Application("Simple Game") { };

    // Load an instance of our Scene class
    game.loadScene(new BasicGame());

    // Run the game application
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
```

After running the main method, you should see your window similar to this:

<img src="docs/assets/simple-game.png" width="404">

[//]: # (## Samples)

[//]: # ()

[//]: # (The project uses a multi-module maven architecture. There's an included `samples` module containing small games built)

[//]: # (with the engine.)

[//]: # ()

[//]: # (These include:)

[//]: # ()

[//]: # (1. Basic game)

[//]: # (2. Tetris &#40;No audio&#41;)

## Contributors

1. [Kwame Opare Asiedu](https://github.com/kwameopareasiedu)