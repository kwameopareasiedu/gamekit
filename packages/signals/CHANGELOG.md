# Change Log

## UNRELEASED

### Added

### Changed

### Deprecated

### Removed

## 1.1.0

### Added

- Added `void unsubscibe()` to `Signal.Subscription` class
- Added `T getValue()` to `Signal` class
- Added `Signal(T initialValue)` constructor to `Signal` class

### Changed

- Changed signature of `void subscribe(String key, Subscriber<T> subscriber, boolean notifyImmediately)` in `Signal`
  class to `Subscription<T> subscribe(Subscriber<T> subscriber, boolean notifyImmediately)`
- Changed signature of `void subscribeOnce(String key, Subscriber<T> subscriber, boolean notifyImmediately)` in `Signal`
  class to `Subscription<T> subscribeOnce(Subscriber<T> subscriber, boolean notifyImmediately)`
- Changed signature of `void unsubscribe(String key)` in `Signal` class to `void unsubscribe(Subscription<T> key)`

## 1.0.0

### Added

- Added `Signal` class