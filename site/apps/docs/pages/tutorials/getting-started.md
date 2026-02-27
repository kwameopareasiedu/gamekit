# Getting Started

In this document, we'll cover how to install GameKit and build a simple application with it.

## Installation

GameKit is distributed as a **Maven** dependency.

To include it in your project, add its dependency to your `pom.xml`, then reload the Maven project in your IDE or run
`mvn clean compile` to download the dependencies.

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project>
    ...
    <repositories>
        ...
        <!-- Include the repository block -->
        <repository>
            <id>github-maven</id>
            <name>GameKit GitHub Maven</name>
            <url>https://raw.github.com/kwameopareasiedu/gamekit-maven/master</url>
        </repository>
    </repositories>

    <dependencies>
        ...
        <!-- Include the dependency block -->
        <dependency>
            <groupId>dev.gamekit</groupId>
            <artifactId>engine</artifactId>
            <!-- Replace`{VERSION} with the intended version -->
            <version>{VERSION}</version>
        </dependency>
    </dependencies>
</project>
```

> Find all versions on the [releases](https://github.com/kwameopareasiedu/gamekit/releases) page.

## Hello  Game

Let's get started with a very simple application. Think of this as a "Hello World" GameKit sample.

`HelloGame.java`

```java
import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;

import java.awt.Color;

public class HelloGame extends Scene {
  public HelloGame() {
    super("Hello Game");
  }

  public static void main(String[] args) {
    // Create a new application
    Application game = new Application("A Simple Application") { };

    // Load an instance of our scene class
    game.loadScene(new HelloGame());

    // Run the game application
    game.run();
  }

  @Override
  public void render() {
    // Clear the screen with black
    Renderer.clear(Color.BLACK);

    // Draw a red-filled box
    Renderer.fillRect(0, 0, 200, 200).withColor(Color.RED);
  }
}
```

Running this application should give you a window like this:

![](../assets/getting-started.png){:style="max-width:480px;width:100%"}

### What we have done

- We extended the `Scene` class, which represents a logical part of our game (_More on scenes later_)
- In the `render` lifecycle method, we cleared the screen with color
  black <span style="display:inline-block;width:10px;height:10px;background:black"></span> and drew a red 200x200px
  <span style="display:inline-block;width:10px;height:10px;background:red"></span> box
- In the static `main` method, we created an `Application` instance with title, _"A Simple Application"_, loaded an
  instance of our scene subclass and called the `run` method to start the application.
