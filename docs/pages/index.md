# GameKit

GameKit is a 2D Java game engine for creating simple games fast. GameKit is based on
Java [Swing](https://docs.oracle.com/javase/tutorial/uiswing/index.html) and doesn't use OpenGL hence rendering is CPU
based and not GPU based.

GameKit is in no way a AAA engine and has limitations due to it not using OpenGL, but performance is decent enough for
small to medium-sized games.

## Installation

GameKit is distributed as a Maven dependency. Include it in your project add its dependency to your `pom.xml`

```xml

<project>
    <repositories>
        <repository>
            <id>github-maven</id>
            <name>GameKit Github Maven</name>
            <url>https://raw.github.com/kwameopareasiedu/gamekit-maven/master</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>dev.gamekit</groupId>
            <artifactId>engine</artifactId>
            <version>{VERSION}</version>
        </dependency>
    </dependencies>
</project>
```

## Hello GameKit

It's very easy to get started with GameKit. The sample below launches a 1280x720 window with a red square drawn:

```java
import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;

import java.awt.Color;

public class HelloGameKit extends Scene {
  public HelloGameKit() {
    super("Hello GameKit");
  }

  public static void main(String[] args) {
    // Create a new application
    Application game = new Application("Hello GameKit") { };
    // Load an instance of our scene
    game.loadScene(new HelloGameKit());
    // Run the game application
    game.run();
  }

  @Override
  public void render() {
    // Clear the screen with black
    Renderer.setColor(Color.BLACK);
    Renderer.clear();
    // Draw a red square
    Renderer.setColor(Color.RED);
    Renderer.fillRect(0, 0, 200, 200);
  }
}
```

## Next Steps

Now that you've got your feet wet, we'll dive more into the engine modules and features. 
