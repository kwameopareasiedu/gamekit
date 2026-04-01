# Change Log

## Road Map

- Text widget spanning to allow different text attributes for different segments of text
- New widgets: Flow, Divider, Scroll
- Dynamic lighting
- Networked multiplayer
- Control debugging level via Application

## UNRELEASED

### Added

### Changed

### Deprecated

### Removed

## 0.7.0

### Added

- Added `gamekit-maven-plugin` for building and packaging platform executables
- Added `Grid` which is A `MultiChildParent` which arranges its children in a grid
- Added `color` field to `Panel` widget
- Added `cornerRadius` field to `Panel` widget
- Added `clip` field to `Panel` widget
- Added
  `static EngineImage getImageWithInsets(String resPath, int topInset, int rightInset, int bottomInset, int leftInset)
  ` to `IO` class
- Added
  `static EngineImage getImageSliceWithInsets(String resPath, int sliceX, int sliceY, int sliceWidth,
  int sliceHeight, int topInset, int rightInset, int bottomInset)
  ` to `IO` class
- Added `Spinner` widget which is a `Leaf` widget which displays a loading indicator
- Added `Measure` widget which is a `SingleChildParent` widget which overlays a ruler on its child (for layout 
  debugging)
- Added `Builder` widget which is a `Compose` widget which delegates is widget tree construction to the provided 
  delegate object
- Added `void addListener(AudioClip.Event.Handler handler)` to `AudioClip` class
- Added `void removeListener(AudioClip.Event.Handler handler)` to `AudioClip` class
- Added `Stateful` widget which is `SingleChildParent` which is an abstract base for creating custom widgets which 
  maintain an internal state
- Added `ValueGetter<T>` utility interface which exposes a single abstract no-argument method `get` which returns a
  value of type `T`
- Added `void triggerUpdate()` method to `Widget.Host` interface

### Changed

- Replaced `BufferedImage` return type of get image methods in `IO` class to new `EngineImage`
- Changed signature of `static BufferedImage getResourceImage(String resPath)` in `IO` class to
  `static Engine getImage(String resPath)`
- Changed signature of `static BufferedImage getResourceImage(String resPath, int x, int y, int w, int h)` in `IO`
  class to `static EngineImage getImageSlice(String resPath, int sliceX, int sliceY, int sliceWidth)`
- Renamed `void run()` in `VoidCallback` interface to `void invoke()`
- Renamed `void update(T value)` in `ValueCallback` interface to `void invoke(T value)`
- Updated `WidgetUpdater` class to always update `Stateful` widgets during reconciliation

### Deprecated

### Removed

- Removed `Colored` widget whose functionality is now handled by the `Panel` widget
- Removed `static InputStream getFileStream(String path)` method from `IO` class
- Removed `edgeInsets` field from `Field` widget
- Removed `padding` field from `Panel` widget
- Removed `padding` field from `Button` widget
- Removed `thumbEdgeInsets` field from `Slider` widget
- Removed `trackEdgeInsets` field from `Progress` widget
- Removed `fillEdgeInsets` field from `Progress` widget

## 0.6.0

### Added

- Added custom annotation processing to generate config classes for widgets
- Added custom annotation processing to generate `Theme` widget, which automatically includes all themable widget
  fields
- Added `double getAngle()` method to `Vector` class which returns the angle of a vector in radian
- Added `double getSquaredMagnitude()` method to `Vector` class which returns the squared magnitude of a vector
- Added `void lerpAngle(Vector target, double rate)` method to `Vector` class which interpolates the angle of this
  vector to the target vector angle, maintaining its magnitude
- Added `boolean containsPoint(Vector point)` to `RigidBody` class which checks if a rigid body contains a specified
  point
- Added `boolean containsPoint(Vector point, int categories)` to `RigidBody` class which checks if any collider
  matching the specified category mask value contains the specified point
- Added `void setScale(double x, double y)` to `Sprite` component which sets both x and y scales of the sprite
- Added `Rotated` widget which is a `SingleChildParent` which rotates its child about an angle in radian
- Added `static double distance(Vector v1, Vector v2)` method to `Vector` class which computes the distance between
  two vectors
- Added `static List<RaycastHit> raycast(Vector start, Vector end, int categories)` method to `Physics` class which
  casts a ray into the physics world from the start to the end positions and returns a list of hit results
- Added `static List<RaycastHit> raycast(Vector start, double angle, double distance int categories)` method to
  `Physics` class which casts a ray into the physics world from the start position at a specified angle and returns
  a list of hit results
- Added `signals` systems package to engine module
- Added `AnimationSlice` class which observes a slice of an animation's value, mapping it to a new 0 - 1 range
- Added `fontHeightRatio` property to `Text` widget to control excess font height as reported by `FontMetrics`

### Changed

- Changed behaviour of `Physics` class to allow collision detection between sensor colliders
- Modified `Sprite` component to multiply its opacity with the parent entity sprite component (if any)
- Renamed `void setCenter(double x, double y)` in `Sprite` component to `void setOffset(double x double y)`
- Updated `Compose` widget to support custom internal state via its `void updateUI()` method

## 0.5.0

### Added

- Added `Field` widget which is a `Text` widget extension which accepts text input
- Added `Checkbox` widget which is a `SingleChildParent` input component which toggles between two states
- Added `Progress` widget which is a `Leaf` widget which displays a progress bar
- Added `Slider` widget which is a `Progress` widget extension which adjusts a value by moving a slider
- Added `static void drawPolygon(int[] pointPairs)` and `static void fillPolygon(int[] pointPairs)` methods to
  `Renderer` class
- Added `Sprite` which is a `Component` that renders an image appearance for an entity
- Added `public <T extends DrawImage> T withInterpolation(ImageInterpolation interpolation)` method to `DrawImage`
  draw call
- Added `AnimatedSprite` which is a `Sprite` component extension that renders an animated sprite sheet
- Added abstract `Collider` which is a `Component` which defines the physics shape of an entity for the purposes of
  physics collision detection
- Added abstract `CircleCollider` which is a `Collider` with a circle shape
- Added abstract `BoxCollider` which is a `Collider` with a rectangular shape
- Added `void stop()` lifecycle method on `Entity` class, which is called before it is removed from the parent
  entity or the scene
- Added `void restart()` lifecycle method on `Entity` class, which is called when a previously inactivated entity
  is added to a parent entity or the scene
- Added `void destroy()` method to `Entity` which schedules it for destruction at the end of the current frame
- Added `Entity.State getState()` method to `Entity` which returns the current state of the entity
- Added `static double angle(Vector from, Vector to)` method to `Vector` class
- Added `static Vector from(double manitude, double rotation)` method to `Vector` class
- Added `Animation.State.RESTARTED` and `Animation.State.REVERSED` pseudo states which are
  passed to a registered state listener when an animation restarts or reverses respectively
- Added `ValueCallback<T>` class which is a single abstract method interface whose `run` method accepts a single
  variable of type `T`
- Added `state machine` systems package to engine module
- Added `void setLinearVelocity(double x, double y)` method to `RigidBody` class
- Added `void applyForce(double x, double y)` method to `RigidBody` class
- Added `static Vector sum(Vector... vectors)` method to `Vector` which returns a vector summing all input vectors
- Added `static double lerpAngle(double start, double end, double rate)` method to `Math` class
- Added `static Position worldToScreenPosition(double wx, double wy)` method to `Camera` class
- Added `public <T extends Component> findComponent(Class<T> clazz, Component.Filter<T> filter)` method to `Entity`
  class which finds a component of a class, matching the filter provided
- Added `public <T extends DrawCall> T withOpacity(double opacity)` modifier method to abstract `DrawCall` class
- Added `static InputStream getFileStream(String path)` to `IO` class
- Added `static boolean writeFile(String path, String content, boolean overwrite)` to `IO` class
- Added `static void saveString(String key, String value)` to `IO` class
- Added `static void saveInteger(String key, int value)` to `IO` class
- Added `static void saveBoolean(String key, boolean value)` to `IO` class
- Added `static void saveDouble(String key, double value)` to `IO` class
- Added `static void saveFloat(String key, float value)` to `IO` class
- Added `static void saveLong(String key, long value)` to `IO` class
- Added `static void saveBytes(String key, byte[] value)` to `IO` class
- Added `static String getSavedString(String key, String defaultValue)` to `IO` class
- Added `static int getSavedInteger(String key, int defaultValue)` to `IO` class
- Added `static boolean getSavedBoolean(String key, boolean defaultValue)` to `IO` class
- Added `static double getSavedDouble(String key, double defaultValue)` to `IO` class
- Added `static float getSavedFloat(String key, float defaultValue)` to `IO` class
- Added `static long getSavedLong(String key, long defaultValue)` to `IO` class
- Added `static byte[] getSavedBytes(String key, byte[] defaultValue)` to `IO` class
- Added `static boolean clearPreference(String key)` to `IO` class
- Added `static boolean clearPreferences()` to `IO` class
- Added `Gap` widget which is a `Leaf` which leaves a gap spacing

### Changed

- Renamed `public void mounted()` lifecycle method in `Widget` class to `public void init(UI.BridgeObject)`
- Renamed `public void updateState(Widget)` in `Widget` to `public void update(Widget)`
- Renamed `FRAME_TIME_MS` to `FRAME_INTERVAL_MS` in `Constants` class
- Renamed `DRAW_TIME_MS` to `DRAW_INTERVAL_MS` in `Constants` class
- Changed signature of `void scheduleTask(Task task)` to `Timeout scheduleTask(Task task)` in `Application` class
- Renamed `void scheduleAnimation(Animation task)` to `void playAnimation(Animation task)` in `Application` class
- Modified `RigidBody` component to initialize with the entity's `Transform` component's position and rotation
- Changed `Physics.CollisionListener` from an interface to and abstract class
- Modified `Entity` to allow for addition of more than one instances of a component type except the `Transform`
  component
- Changed engine rotations from degree-based to radian-based
- Renamed `Task` class to `VoidCallback` and updated usages
- Changed signature of `static Position screenToWorldPosition(double sx, double sy)` in `Camera` class to `static 
Vector screenToWorldPosition(double, double)`
- Modified `Component` to keep a reference to its `Entity` during disposal

### Removed

- Removed `void addCircleFixture(double radius, FixtureTuner tuner)` from `RigidBody` class
- Removed `void addCircleFixture(double radius)` from `RigidBody` class
- Removed `void addRectFixture(double width, double height, FixtureTuner tuner)` from `RigidBody` class
- Removed `void addRectFixture(double width, double height)` from `RigidBody` class
- Removed `void addCollisionListener(Physics.CollisionListener listener)` from `RigidBody` class
- Removed `static int toInt(double value)` from `Math` class
- Removed `static int toInt(float value)` from `Math` class

## 0.5.0-SNAPSHOT-1

### Added

- Added abstract `Component` which is the base class for all entity component
- Added `Transform` which is a `Component` that contains position and rotation parameters for an entity
- Added `RigidBody` which is a `Component` that enables physics-based motion for an entity
- Added `Physics` class which manages the physics world updates and collision processing
- Added component lifecycle processing to `Entity` class
- Added `public <T extends Component> findComponent(Class<T> clazz)` to `Entity` class which finds a specific
  component on and `Entity`
- Added `public void mounted()` lifecycle method to `Widget` class
- Added `Theme` widget which is a `SingleChildParent` which theme variables to its descendants
- Updated `Text` widget to utilize nearest available `Theme` in ancestry
- Updated `Button` widget to utilize nearest available `Theme` in ancestry
- Changed `Resolution` enum to a record class
- Added `static Resolution create(int width, int height)` to the `Resolution` record class
- Added `public static getInfo()` to `Window` class to return `Window.Info` instance
- Added public constructor to `Resolution` class to allow for custom resolutions
- Added `WindowMode.BORDERLESS` setting value

### Changed

- Updated `public static <WIDGET_NAME>Options options()` method in all widget classes to `public static Config config()`
- Changed `static Window getInstance()` in `Window` class from public to package-private

## 0.4.0-SNAPSHOT-5

### Changed

- Changed signature of `public void render(Renderer renderer)` to `public void render()` in `Entity` class
- Updated `Renderer`, changing all instance methods to static methods

## 0.4.0-SNAPSHOT-4

### Changed

- Fixed bug causing UI not to render completely when an update is triggered
- Fixed bug causing screen flickering during draw thread invocation

## 0.4.0-SNAPSHOT-3

### Added

- Separated audio processing and rendering into separate threads

### Changed

- Changed signature of `public void render()` to `public void render(Renderer renderer)` in
  `Entity` class

### Removed

- Removed static Renderer class. Calls to static `Renderer` methods should be replaced with calls to renderer object
  now supplied to the `Scene.render` or `Entity.render` methods.

## 0.4.0-SNAPSHOT-2

### Changed

- Renamed `Prop` class to `Entity`

## 0.4.0-SNAPSHOT-1

### Added

- Added `Opacity` which is a `SingleChildParent` which renders its child with transparency
- Added `Scaled` which is a `SingleChildParent` which scales the computed size of its child
- Added `Audio` class which manages loaded audio clips
- Added abstract `AudioClip` which loads and manages the clip from a resource
- Added `AudioClip2D` which loads and plays non-spatial audio clips
- Added `AudioClip3D` which loads and plays spatial clips and whose volume and pan depend on its position and the
  position of the `AudioListener`
- Added `AudioListener` which is a reference for `AudioClip3D` to compute its volume and pan
- Added `AudioGroup` which manages the volume and mute status of `AudioClip`s added to it
- Added `AudioAttenuation` interface which defines a distance attenuation function for `AudioClip3D`
- Added abstract `AudioShape` which defines the shape, minimum and maximum attenuation distances of the field of
  `AudioClip3D`
- Added `AudioShapeCircle` which defines a circular audio field for `AudioClip3D`
- Added `static boolean isButtonClicked(int buttonCode)` to `Input` class
- Added event handling code to `Widget` class
- Added `InputEventHandler` interface to be implemented by `Widgets` which would like to receive input events
- Added `Compose` which is a `SingleChildParent` which delegates layout and rendering to the provided widget tree,
  essentially a base for custom widgets
- Added `static void drawString(String content, int x, int y, int width, int height)` method to `Renderer` class
- Added `static void withRotation(int x, int y, double deg, RenderActions renderGroup)` method to `Renderer` class
- Added `static double lerp(double from, double to, double rate)` to `Math` class for linear interpolation
- Added `static void resetOptions()` to `Renderer` class
- Added `static BufferedImage getResourceImage(String resPath, int x, int y, int w, int h)` to `IO` class for
  reading a slice of a resource image
- Added `Color` which is a `SingleChildParent` which renders a solid color background
- Implemented safe multithreading in `Application` class

### Changed

- Modified `Button` widget to center child
- Modified `Padding` widget to center child
- Modified `NinePatch` widget to center child
- Renamed `FixedSize` widget to `Sized`
- Renamed `static Font loadFontResource(String path)` to `static Font getResourceFont(String resPath)` in `IO` class
- Renamed `static Font loadFontResource(String path)` to `static Font getResourceFong(String resPath)` in `IO` class
- Renamed `static BufferedImage loadImageResource(String path)` to
  `static BufferedImage getResourceImage(String resPath)` in `IO` class
- Renamed `NinePatch` widget to `Panel`
- Renamed `static Input.isButtonJustPressed(int buttonCode)` to `static Input.isButtonDown(int buttonId)`
- Renamed `static Input.isButtonJustReleased(int buttonCode)` to `static Input.isButtonReleased(int buttonId)`
- Renamed `static Camera.screenToWorldPoint(int x, int y)` to `static Camera.screenPointToWorld(int x, int y)`
- Renamed `void onStart()` to `void start()` in `Scene` and `Prop` class
- Renamed `void onUpdate()` to `void update()` in `Scene` and `Prop` class
- Renamed `void onRender()` to `void render()` in `Scene` and `Prop` class
- Renamed `void onDispose()` to `void dispose()` in `Scene` and `Prop` class
- Modified `Animation` to be restartable
- Modified `Sized` widget to enforce a fixed size, intrinsic size or fractional size on its child

### Removed

- Removed `IntrinsicWidth` widget in favour of `IntrinsicSize` with `Axis.HORIZONTAL`
- Removed `IntrinsicHeight` widget in favour of `IntrinsicSize` with `Axis.VERTICAL`
- Removed `static BufferedReader loadBufferedResource(String path)` from `IO` class
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

- Added constructor `Application(Config config)` which can be used to set the title, resolution and fullscreen mode
- Added `Stack` which is a `MultiChildParent` which stacks its children on top of each other
- Added `Animation setValueListener(ValueListener valueListener)` to `Animation` to be notified of value changes
- Added `Animation setStateListener(StateListener stateListener)` to `Animation` to be notified of state changes
- Added `static Position screenToWorldPoint(int sx, int sy)` to `Camera` which transforms a screen-space position
  into a world position
- Added event generation and dispatch system to `UI` class
- Added abstract `Event` class
- Added abstract `MouseEvent` which is the `Event` for mouse events
- Added `MouseMotionEvent` which is a `MouseEvent` dispatched when the mouse moves
- Added `MouseClickEvent` which is a `MouseEvent` dispatched when a mouse button is released
- Added `Button` which is a `SingleChildParent` for detecting UI events
- Added `MouseEnterEvent` which is a `MouseEvent` dispatched when the mouse enters a widget
- Added `MouseExitEvent` which is a `MouseEvent` dispatched when the mouse leaves a widget
- Added `Decorated` which is a `SingleChildParent` paints a shape, background color and border decorations
- Added `NinePatch` which is a `Widget` which uses the 9-patch algorithm to resize parts of the
  image to prevent stretching
- Added `IntrinsicWidth` which is a `SingleChildParent` which limits the computed width of its single child to the
  child's intrinsic width
- Added `IntrinsicHeight` which is a `SingleChildParent` which limits the computed height of its single child to the
  child's intrinsic height
- Added `IntrinsicSize` which is a `SingleChildParent` which limits the computed size of its single child to the
  child's intrinsic size
- Added `Empty` which is a `Widget` with zero size which renders nothing and should be used in places where `null`
  would have been preferable

### Changed

- Modified `Image` widget to render a given image instead
- Changed `Camera` class to a utility class. Instead of `Camera.getInstance().<METHOD>`, use `Camera.<METHOD>`

### Removed

- Removed `static void setResolution(Resolution resolution)` from `Window` class
- Removed `static void setFullscreen(boolean fullScreen)` from `Window` class
- Removed `static Camera getInstance()` from `Camera` class
- Removed `Point transformPoint(int x, int y)` from `Camera` class
- Removed `Image withSize(int width, int height)` from `Image` class

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

- Added `static void setResolution(Resolution resolution)` to `Window` class
- Added `static void setFullscreen(boolean fullScreen)` to `Window` class
- Implemented mouse button detection in `Input` class
- Added `static boolean isButtonPressed(int buttonCode)` to `Input` class
- Added `static boolean isButtonJustPressed(int buttonCode)` to `Input` class
- Added `static boolean isButtonJustReleased(int buttonCode)` to `Input` class
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

- Renamed `void scheduleFrameEndTask(Task task)` to `void scheduleTask(Task task)` in `Application` class.

### Removed

- Removed deprecated `AnimationCurves` class.
- Removed `void setSize(int size, int size)` from `Window` class
- Removed `void maximize()` from `Window` class
- Removed deprecated `AnimationCurves` class

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

- Added `static BufferedReader loadBufferedResource(String resPath)` to `IO` class which returns a `BufferedReader`
  to a resource at the specified path
- Added `void setSize(int width, int size)` to `Window` class which resizes the current instance frame to the new
  width and height.
- Added `void maximize()` to `Window` class which maximizes the current instance frame.
- Added `void scheduleTimerTask(long timeout, Task task)` to `Application` which schedules a task to be run after
  the timeout has elapsed

### Changed

- Renamed `static BufferedImage loadImage(String path)` to `static BufferedImage loadImageResource(String resPath)`
  in `IO` class.
- Renamed `static Font loadFont(String path)` to `static BufferedImage loadFontResource(String resPath)` in `IO` class.
- Renamed `void runAnimation(Animation)` to `void scheduleAnimation(Animation animation)`in `Application` class.
- Modified `Window` to start in windowed mode instead of maximized
