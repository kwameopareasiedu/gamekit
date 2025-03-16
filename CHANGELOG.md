# ChangeLog

## UNRELEASED

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
