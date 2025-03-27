# ChangeLog

## 0.3.0-SNAPSHOT-3 [UNRELEASED]

### Added

- Added constructor `Application(Config)` which can be used to set the title, resolution and fullscreen mode
- Added `ImageRes` which is a widget that renders a **resource image**
- Added `Stack` which is a `MultiChildParent` which stacks its children on top of each other

### Changed

- Modified `Image` widget to render a given image instead

### Removed

- Removed ~~`static setResolution(Resolution)`~~ from `Window` class
- Removed ~~`static setFullscreen(boolean)`~~ from `Window` class

## 0.3.0-SNAPSHOT-2

### Added

- Added `Flex` which is a `MultiChildParent` which lays its children out along a single axis
- Added `FixedSize` which is a `SingleChildParent` which enforces a fixed size on its child
- Added `gapSize` property to `Row` and `Column` widgets
- Added `mainAxisAlignment` property to `Row` and `Column` widgets
- Added `crossAxisAlignment` property to `Row` and `Column` widgets
- Added `alignment` property to `Text` widget

### Changed

- Modified `IO` to load using input streams
- Modified `Column` to extend `Flex` instead of `MultiChildParent`
- Modified `Row` to extend `Flex` instead of `MultiChildParent`
- Moved all UI related enums to `dev.gamekit.ui.enums` package

## 0.3.0-SNAPSHOT-1

### Added

- Added `static void setResolution(Resolution)` to `Window` class
- Added `static void setFullscreen(boolean)` to `Window` class
- Implemented mouse button detection in `Input` class
- Added `static boolean isButtonPressed(int)` to `Input` class
- Added `static boolean isButtonJustPressed(int)` to `Input` class
- Added `static boolean isButtonJustReleased(int)` to `Input` class
- Added abstract `Widget` class which is the base class of all UI elements
- Added abstract `Parent` which is a `Widget` which contain other widgets
- Added abstract `SingleChildParent` which is a `Parent` that has only one child widget
- Added abstract `MultiChildParent` which is a `Parent` that container multiple child widgets
- Added `Row` which is a `MultiChildParent` that lays its children horizontally
- Added `Column` which is a `MultiChildParent` that lays its children vertically
- Added `Text` which is a `Widget` that renders text
- Added `Image` which is a `Widget` that renders a resource image
- Added `Align` which is a `SingleChildParent` that align its child within itself
- Added `Center` which is an `Align` with center alignment
- Added `Padding` which is a `SingleChildParent` that adds spacing around its child
- Added `Spacing` class which represents space around a widget
- Added `UI` class which manages the user interface for a `Scene`

### Changed

### Deprecated

### Removed

- Removed ~~`void scheduleFrameEndTask(Task)`~~ from `Application` class. To schedule end of frame task, use
  `void scheduleTask(Task)` instead
- Removed deprecated ~~`AnimationCurves`~~ class.
- Removed ~~`void setSize(int, int)`~~ from `Window` class
- Removed ~~`void maximize()`~~ from `Window` class
- Removed deprecated ~~`AnimationCurves`~~ class

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

- Added `static BufferedReader loadBufferedResource(String)` to `IO` class which returns a `BufferedReader` to a
  resource at the specified path
- Added `void setSize(int, int)` to `Window` class which resizes the current instance frame to the new width and height.
- Added `void maximize()` to `Window` class which maximizes the current instance frame.
- Added `void scheduleTimerTask(long, Task)` to `Application` which schedules a task to be run after the
  timeout has elapsed

### Changed

- Renamed ~~`static BufferedImage loadImage(String)`~~ to `static BufferedImage loadImageResource(String)` in `IO`
  class.
- Renamed ~~`static Font loadFont(String)`~~ to `static BufferedImage loadFontResource(String)` in `IO` class.
- Renamed ~~`void runAnimation(Animation)`~~ to `void scheduleAnimation(Animation)` in `Application`
  class.
- Modified `Window` to start in windowed mode instead of maximized
