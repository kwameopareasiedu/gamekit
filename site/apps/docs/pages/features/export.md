# Exporting Your Game

So, you've built your awesome game using **GameKit**, now what? Well, you need to export it so that others can play, and
GameKit makes this very easy to do.

GameKit is Java-based, built using the [Maven](https://maven.apache.org/){:target=\_blank} build tool and dependency
manager, and Maven is a [plugin-execution framework at heart](https://maven.apache.org/plugins/index){:target=\_blank}.

As such, GameKit provides a custom Maven plugin, `gamekit-maven-plugin` to handle project build and export.

## Setup

To use the `gamekit-maven-plugin`, we'll configure it in your project's `pom.xml` file like so:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project>
    ...
    <!-- (3) Change the packaging to gamekit-executable -->
    <packaging>gamekit-executable</packaging>

    <pluginRepositories>
        ...
        <!-- (1) Include the plugin repository block -->
        <pluginRepository>
            <id>gamekit-maven</id>
            <name>GameKit GitHub Maven</name>
            <url>https://raw.github.com/kwameopareasiedu/gamekit/master/artifacts</url>
        </pluginRepository>
    </pluginRepositories>

    <build>
        ...
        <plugins>
            ...
            <!-- (2) Include the plugin block -->
            <plugin>
                <groupId>dev.gamekit</groupId>
                <artifactId>gamekit-maven-plugin</artifactId>
                <version>0.1.0</version>
                <extensions>true</extensions> <!-- Enables extensions to include gamekit's custom packaging type -->

                <configuration>
                    <mainClass>mainClassPath</mainClass> <!-- Fully qualified name of your main class (Required) -->
                    <description>Description</description> <!-- The description to include in metadata (Optional) -->
                    <vendor>Vendor Name</vendor> <!-- The vendor name to include in metadata (Optional) -->
                    <jarOnly>true</jarOnly> <!-- Stop build at generated jar file (Optional) -->
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### What we have done

1. We included the `pluginRepository` entry. This tells Maven where to look to find the plugin library.
2. We included the `plugin` block. This registers the plugin to be used in our project:
   [//]: # (@formatter:off)
    - The `<extensions>true</extensions>` line makes the plugin's custom extensions available to Maven.
    - The `<configuration>` block supplies the following arguments to the plugin:
        - `mainClass` is the **required** fully qualified name of the class containing the static main method
        - `description` is the _optional_ description to include in the application metadata
        - `vendor` is the _optional_ vendor name to include in the application metadata
        - `jarOnly` is an _optional_ boolean directing the plugin to stop after generating the jar file
   [//]: # (@formatter:on)
3. We changed the `<packaging>` of our project to `gamekit-executable`. This is a custom packaging type provided by the
   plugin and instructs Maven on how to build and package the final executable file.

## Build

In the previous section, we configured the `gamekit-maven-plugin` in our `pom.xml`. The plugin binds to Maven's
`package` phase, so to build your game, simply run the following command:

```
mvn package
```

After building the project, your executable can be found at the path below, where `<os-name>` is the name of your
operating system (_windows_, _darwin_, _linux_), and `<project-name>` is your project's artifact name in your `pom.xml`:

```
target/outputs/<os-name>/<project-name>.zip
```

> Builds are os-dependent, meaning to create Windows/MacOS/Linux versions, you must run the build on the respective
> operating systems.

The zipped output contains a `README.txt` with instructions on how to launch the game.
