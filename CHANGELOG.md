# ChangeLog

## UNRELEASED

### Added

- Added `static void setResolution(Resolution resolution)` to `Window` class
- Added `static void setFullscreen(boolean fullScreen)` to `Window` class
- Implemented mouse button detection in `Input` class
- Added `static boolean isButtonPressed(int buttonCode)` to `Input` class
- Added `static boolean isButtonJustPressed(int buttonCode)` to `Input` class
- Added `static boolean isButtonJustReleased(int buttonCode)` to `Input` class
- Added abstract `Node` class which is the base class of all UI elements 
- Added abstract `Container` class which is the base class of all container UI elements
- Added `HBox` container class which lays its children horizontally
- Added `VBox` container class which lays its children vertically
- Added `Text` UI class which renders text on the `Window` UI target

### Changed

### Deprecated

### Removed

- Removed ~~`void scheduleFrameEndTask(Task task)`~~ from `Application` class. To schedule end of frame task, use
  `void scheduleTask(Task task)` instead
- Removed deprecated ~~`AnimationCurves`~~ class.
- Removed ~~`void setSize(int width, int height)`~~ from `Window` class
- Removed ~~`void maximize()`~~ from `Window` class

## v0.2.1-SNAPSHOT

### Added

- Added Javadoc comments to engine classes and methods
- Added `void stop()` to `Animation` class

### Changed

- Copied all static constants of `AnimationCurves` to `AnimationCurve`

### Deprecated

- Deprecated `AnimationCurves` and its static constants for removal in next minor update

## v0.2.0-SNAPSHOT

### Added

- Added `static BufferedReader loadBufferedResource(String path)` to `IO` class which returns a `BufferedReader` to a
  resource at the specified path
- Added `void setSize(int width, int height)` to `Window` class which resizes the current instance frame to the new
  width and height.
- Added `void maximize()` to `Window` class which maximizes the current instance frame.
- Added `void scheduleTimerTask(long timeout, Task task)` to `Application` which schedules a task to be run after the
  timeout has elapsed

### Changed

- Renamed ~~`static BufferedImage loadImage(String path)`~~ to `static BufferedImage loadImageResource(String path)` in
  `IO` class.
- Renamed ~~`static Font loadFont(String path)`~~ to `static BufferedImage loadFontResource(String path)` in `IO` class.
- Renamed ~~`void runAnimation(Animation animation)`~~ to `void scheduleAnimation(Animation animation)` in `Application`
  class.
- Modified `Window` to start in windowed mode instead of maximized
