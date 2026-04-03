# Builder Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Builder`, like `Compose`, is also an abstract widget for building custom widgets. Unlike `Compose`, `Builder` doesn't
need to be subclassed. It functions like an inline `Compose` widget.

Subclasses must supply a method to during creation `build` method which  returns the custom widget tree.

## Usage

```java
public class CustomScene extends Scene {
  private boolean initialized = false;

  public CustomScene() {
    super("Widget Builder Scene");
  }

  @Override
  protected Widget build() {
    return Column.create(
      props -> {
        props.crossAxisAlignment = CrossAxisAlignment.CENTER;
        props.gapSize = 24;
      },
      Text.create(
        props -> {
          props.text = "Widget Builder below";
          props.alignment = Alignment.CENTER;
          props.fontStyle = Text.BOLD;
        }
      ),
      Builder.create( // <- Builder widget here
        () -> {
          if (initialized) {
            return Text.create("Initialized");
          }
          
          return Text.create("Not initialized");
        }
      )
    );
  }
}
```
