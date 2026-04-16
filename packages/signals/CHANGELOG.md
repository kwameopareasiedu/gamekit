# Change Log

## UNRELEASED

### Added

### Changed

### Deprecated

### Removed

## 1.1.0

### Changed

- Changed signature of `void subscribe(String key, Subscriber<T> subscriber, boolean notifyImmediately)` in `Signal`
  class to `int subscribe(Subscriber<T> subscriber, boolean notifyImmediately)`
- Changed signature of `void subscribeOnce(String key, Subscriber<T> subscriber, boolean notifyImmediately)` in `Signal`
  class to `int subscribeOnce(Subscriber<T> subscriber, boolean notifyImmediately)`

## 1.0.0

### Added

- Added `Signal` class