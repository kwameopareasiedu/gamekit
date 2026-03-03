# Compose Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Compose` is an abstract widget for building custom widgets. Subclasses must implement the abstract `build` method which
returns the custom widget tree.

## Usage

```java
public class MainMenuButton extends Compose {
  private final String text;
  private final MouseEvent.Handler mouseListener;

  public MainMenuButton(String text, MouseEvent.Handler mouseListener) {
    this.text = text;
    this.mouseListener = mouseListener;
  }

  @Override
  protected Widget build() {
    return Button.create(
      props -> {
        props.padding = new Spacing(12, 12, 16, 12);
        props.mouseListener = mouseListener;
      },
      Padding.create(
        12, 12, 16, 12,
        Text.create(
          props -> {
            props.text = text;
            props.fontSize = 20;
            props.fontStyle = Text.BOLD;
          }
        )
      )
    );
  }
}
```
