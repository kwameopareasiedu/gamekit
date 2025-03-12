# Engine Docs

This sections contains the full in-depth documentation of the classes and interactions of classes that make GameKit
function.

## Application

This is the heart of a GameKit program. A game or application must extend this class to do anything with the engine.

`Application` runs a **60fps** fixed-step game loop which processes input, updates and renders the current scene and
runs end-of-frame tasks.

You can override `Application.onDispose()` to perform any cleanup before the application exits.

| Method                                         | Description                                                                |
|------------------------------------------------|----------------------------------------------------------------------------|
| `public static Application getInstance()`      | Get the current instance of the application                                |
| `public void run()`                            | Starts up the application's  game loop                                     |
| `public void loadScene(Scene scene)`           | Queues a scene object which is loaded at the end of the current frame      |
| `public void runOnFrameEnd(FrameEndTask task)` | Schedule a task to be executed at the end of the current frame             |
| `public void quit()`                           | Signal to the application to end the game loop, run `onDispose()` and exit |
