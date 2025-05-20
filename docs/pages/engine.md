# Engine Components

In this section, we'll take a look at the core components of the GameKit engine.

## Application

`Application` is the heart of a GameKit program. Your game must extend this class to do anything with the engine.

`Application` runs a fixed-step game loop which processes input, updates and renders the current scene and runs
end-of-frame tasks. This fixed time step approach makes sure during lag spikes don't overshoot logic and physics
updates.

### Startup

When your game is run, it sets up the engine internals which include:

- Creating the window object
- Attaching keyboard and mouse listeners to the window
- Making the window visible

### Game Loop

After setup, the main thread starts which runs the following until the application exits:

- Capture keyboard and mouse input
- Update the current scene
- Render the current scene to the window
- Perform end of frame tasks

In a worker thread, the following updates also occur in another game loop:

- Update running animations
- Update active timeouts
- Update audio system

The game loops continues running until the close event is received on the window. When this happens, the loop is halted
and the application runs its dispose logic before exiting.

You can override the `dispose()` to custom cleanup before the application exits

```java
@Override
protected void dispose() {
  super.dispose();
  /** Custom dispose logic */
}
```

To access the application from anywhere in the application, use `Application.getInstance()`.
