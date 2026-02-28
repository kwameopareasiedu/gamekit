# File IO

_[Back To Features](./overview.md)_

While building your game, chances are, you'd need to load images, audio, fonts or other external resources. GameKit
allows you to do this via the static `IO` class.

> GameKit is built with Java and hence you need to ensure that your external directory is added to the classpath.
> For Maven projects, this is the `src/main/java/resources/` directory which should already be added to the classpath,
> however, be sure to check your IDE settings.

`IO` caches resources it loads, making subsequent queries instantaneous, which improves overall performance of your
game.

## Static Methods

| Method              | Description                                                                                                                                                                                       |
|---------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `getResourceStream` | Opens and returns a stream to a resource file                                                                                                                                                     |
| `getFileStream`     | Opens and returns a stream to a file which may or may not be a resource file                                                                                                                      |
| `getResourceImage`  | Loads and caches an image resource file                                                                                                                                                           |
| `getResourceFont`   | Loads and caches a font resource file                                                                                                                                                             |
| `writeFile`         | Writes data to the file at the specified path                                                                                                                                                     |
| `saveString`        | Saves a string value in the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node                                              |
| `saveInteger`       | Saves an integer value in the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node                                            |
| `saveBoolean`       | Saves a boolean value in the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node                                             |
| `saveDouble`        | Saves a double value in the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node                                              |
| `saveFloat`         | Saves a float value in the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node                                               |
| `saveLong`          | Saves a long value in the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node                                                |
| `saveBytes`         | Saves a byte array value in the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node                                          |
| `getSavedString`    | Returns a byte array value from the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node else the default value provided      |
| `getSavedInteger`   | Returns a byte array value from the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node else the default value provided      |
| `getSavedBoolean`   | Returns a byte array value from the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node else the default value provided      |
| `getSavedDouble`    | Returns a byte array value from the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node else the default value provided      |
| `getSavedFloat`     | Returns a byte array value from the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node else the default value provided      |
| `getSavedLong`      | Returns a byte array value from the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node else the default value provided      |
| `getSavedBytes`     | Returns a byte array value from the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node else the default value provided      |
| `clearPreference`   | Clears the saved value of a key from the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node else the default value provided |
| `clearPreferences`  | Clears all saved values in the application's [preferences](https://docs.oracle.com/en/java/javase/17/core/preferences-api1.html){:target="_blank"} node else the default value provided           |
