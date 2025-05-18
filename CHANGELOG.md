# ChangeLog

## TODO

- Implement spanning for text widget which is the ability to use different text attributes for
  the same text widget
- Research into using triple state buffers and atomic flags to decouple rendering into a
  separate thread

## 0.4.0-SNAPSHOT-2

### Added

### Changed

### Deprecated

### Removed

## 0.4.0-SNAPSHOT-1

### Added

- Added `Opacity` which is a `SingleChildParent` which renders its child
  with transparency
- Added `Scaled` which is a `SingleChildParent` which scales the computed
  size of its child
- Added `Audio` class which manages loaded audio clips
- Added abstract `AudioClip` which loads and manages the clip from a resource
- Added `AudioClip2D` which loads and plays non-spatial audio clips
- Added `AudioClip3D` which loads and plays spatial clips and whose volume
  and pan depend on its position and the position of the `AudioListener`
- Added `AudioListener` which is a reference for `AudioClip3D` to compute
  its volume and pan
- Added `AudioGroup` which manages the volume and mute status of
  `AudioClip`s added to it
- Added `AudioAttenuation` interface which defines a distance attenuation
  function for `AudioClip3D`
- Added abstract `AudioShape` which defines the shape, minimum and maximum
  attenuation distances of the field of `AudioClip3D`
- Added `AudioShapeCircle` which defines a circular audio field for  
  `AudioClip3D`
- Added `static boolean isButtonClicked(int buttonCode)` to `Input` class
- Added event handling code to `Widget` class
- Added `InputEventHandler` interface to be implemented by `Widgets` which
  would like to receive input events
- Added `Compose` which is a `SingleChildParent` which delegates layout and
  rendering to the provided widget tree, essentially a base for custom widgets
- Added `static void drawString(String content, int x, int y, int width, int 
height)` method to `Renderer` class
- Added `static void withRotation(int x, int y, double deg, RenderActions 
renderGroup)` method to `Renderer` class
- Added `static double lerp(double from, double to, double rate)` to `Math` class for linear
  interpolation
- Added `static void resetOptions()` to `Renderer` class
- Added `static BufferedImage getResourceImage(String resPath, int x, int y, int w, int h)` to
  `IO` class for reading a slice of a resource image
- Added `Color` which is a `SingleChildParent` which renders a solid color background
- Implemented safe multithreading in `Application` class

### Changed

- Modified `Button` widget to center child
- Modified `Padding` widget to center child
- Modified `NinePatch` widget to center child
- Renamed `FixedSize` widget to `Sized`
- Renamed `static Font loadFontResource(String path)` to `static Font 
getResourceFong(String path)` in `IO` class
- Renamed `static Font loadFontResource(String path)` to `static Font 
getResourceFong(String path)` in `IO` class
- Renamed `static BufferedImage loadImageResource(String path)` to `static 
BufferedImage getResourceImage(String path)` in `IO` class
- Renamed `NinePatch` widget to `Panel`
- Renamed `static Input.isButtonJustPressed(int buttonCode)` to `static 
  Input.isButtonDown(int buttonId)`
- Renamed `static Input.isButtonJustReleased(int buttonCode)` to `static 
  Input.isButtonReleased(int buttonId)`
- Renamed `static Camera.screenToWorldPoint(int x, int y)` to `static 
  Camera.screenPointToWorld(int x, int y)`
- Renamed `void onStart()` to `void start()` in `Scene` and `Prop` class
- Renamed `void onUpdate()` to `void update()` in `Scene` and `Prop` class
- Renamed `void onRender()` to `void render()` in `Scene` and `Prop` class
- Renamed `void onDispose()` to `void dispose()` in `Scene` and `Prop` class
- Modified `Animation` to be restartable
- Modified `Sized` widget to enforce a fixed size, intrinsic size or fractional size on its child

### Deprecated

### Removed

- Removed `IntrinsicWidth` widget. Use `IntrinsicSize` with `Axis.
  HORIZONTAL` instead
- Removed `IntrinsicHeight` widget. Use `IntrinsicSize` with `Axis.VERTICAL`
  instead
- Removed `static BufferedReader loadBufferedResource(String path)` from `IO`
  class
- Removed `MouseMotionEvent` class and merged function into `MouseEvent` class
- Removed `MouseClickEvent` class and merged function into `MouseEvent` class
- Removed `MouseEnterEvent` class and merged function into `MouseEvent` class
- Removed `MouseExitEvent` class and merged function into `MouseEvent` class
- Removed `TextAlignment` enum in favour or `Alignment` enum
- Removed `Decorated` widget
- Removed `Intrinsic Size` widget
- Removed `Axis` widget
- Removed `static void beginGroup()` from `Renderer` class
- Removed `static void endGroup()` from `Renderer` class

## 0.3.0-SNAPSHOT-3

### Added

- Added constructor `Application(Config)` which can be used to set the title,
  resolution and fullscreen mode
- Added `Stack` which is a `MultiChildParent` which stacks its children on
  top of each other
- Added `Animation setValueListener(ValueListener)` to `Animation` to be
  notified of value changes
- Added `Animation setStateListener(StateListener)` to `Animation` to be
  notified of state changes
- Added `static Position screenToWorldPoint(int, int)` to `Camera` which
  transforms a screen-space position into a world position
- Added event generation and dispatch system to `UI` class
- Added abstract `Event` class
- Added abstract `MouseEvent` which is the `Event` for mouse events
- Added `MouseMotionEvent` which is a `MouseEvent` dispatched when the mouse
  moves
- Added `MouseClickEvent` which is a `MouseEvent` dispatched when a mouse
  button is released
- Added `Button` which is a `SingleChildParent` for detecting UI events
- Added `MouseEnterEvent` which is a `MouseEvent` dispatched when the mouse
  enters a widget
- Added `MouseExitEvent` which is a `MouseEvent` dispatched when the mouse
  leaves a widget
- Added `Decorated` which is a `SingleChildParent` paints a shape,
  background color and border decorations
- Added `NinePatch` which is a `Widget` which uses the 9-patch algorithm to
  resize parts of the image to prevent stretching
- Added `IntrinsicWidth` which is a `SingleChildParent` which limits the
  computed width of its single child to the child's intrinsic width
- Added `IntrinsicHeight` which is a `SingleChildParent` which limits the
  computed height of its single child to the child's intrinsic height
- Added `IntrinsicSize` which is a `SingleChildParent` which limits the
  computed size of its single child to the child's intrinsic size
- Added `Empty` which is a `Widget with zero size which renders 
  nothing and should be used in places where `null` would have been preferable

### Changed

- Modified `Image` widget to render a given image instead
- Changed `Camera` class to a utility class. Instead of
  `Camera.getInstance().<METHOD>`, use `Camera.<METHOD>`

### Removed

- Removed `static void setResolution(Resolution)` from `Window` class
- Removed `static void setFullscreen(boolean)` from `Window` class
- Removed `static Camera getInstance()` from `Camera` class
- Removed `Point transformPoint(int, int)` from `Camera` class
- Removed `Image withSize(int, int)` from `Image` class

## 0.3.0-SNAPSHOT-2

### Added

- Added `Flex` which is a `MultiChildParent` which lays its children out along a
  single axis
- Added `FixedSize` which is a `SingleChildParent` which enforces a fixed size
  on its child
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
- Added abstract `SingleChildParent` which is a `Parent` that has only one child
  widget
- Added abstract `MultiChildParent` which is a `Parent` that container multiple
  child widgets
- Added `Row` which is a `MultiChildParent` that lays its children horizontally
- Added `Column` which is a `MultiChildParent` that lays its children vertically
- Added `Text` which is a `Widget` that renders text
- Added `Image` which is a `Widget` that renders a resource image
- Added `Align` which is a `SingleChildParent` that align its child within
  itself
- Added `Center` which is an `Align` with center alignment
- Added `Padding` which is a `SingleChildParent` that adds spacing around its
  child
- Added `Spacing` class which represents space around a widget
- Added `UI` class which manages the user interface for a `Scene`

### Removed

- Removed `void scheduleFrameEndTask(Task)` from `Application` class. To
  schedule end of frame task, use `void scheduleTask(Task)` instead
- Removed deprecated `AnimationCurves` class.
- Removed `void setSize(int, int)` from `Window` class
- Removed `void maximize()` from `Window` class
- Removed deprecated `AnimationCurves` class

## v0.2.1-SNAPSHOT

### Added

- Added Javadoc comments to engine classes and methods
- Added `void stop()` to `Animation` class

### Changed

- Copied all static constants of `AnimationCurves` to `AnimationCurve`

### Deprecated

- Deprecated `AnimationCurves` and its static constants for removal in next
  minor update

## v0.2.0-SNAPSHOT

### Added

- Added `static BufferedReader loadBufferedResource(String)` to `IO` class which
  returns a `BufferedReader` to a resource at the specified path
- Added `void setSize(int, int)` to `Window` class which resizes the current
  instance frame to the new width and height.
- Added `void maximize()` to `Window` class which maximizes the current instance
  frame.
- Added `void scheduleTimerTask(long, Task)` to `Application` which schedules a
  task to be run after the
  timeout has elapsed

### Changed

- Renamed `static BufferedImage loadImage(String)` to
  `static BufferedImage loadImageResource(String)` in `IO` class.
- Renamed `static Font loadFont(String)` to
  `static BufferedImage loadFontResource(String)` in `IO` class.
- Renamed `void runAnimation(Animation)` to `void scheduleAnimation(Animation)`
  in `Application` class.
- Modified `Window` to start in windowed mode instead of maximized
