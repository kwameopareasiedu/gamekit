# Compose Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Compose` is an abstract widget for building custom widgets. Subclasses must implement the abstract `build` method which
returns the custom widget tree.

> Custom widgets can sometimes have internal state on which the UI depends on. If a state changes should cause a rebuild
> of the widget, you can call the `updateUI` method to do so.

## Usage

```java
public class MainMenuButton extends Compose {
  private static String[] LABELS = new String[] {  };
  
  private final String text;
  private final MouseEvent.Handler mouseListener;
  private int labelIndex = 0;

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
        
        labelIndex = (labelIndex + 1) % LABELS.length;
        updateUI();
      },
      Padding.create(
        12, 12, 16, 12,
        Text.create(
          props -> {
            props.text = text + LABELS[labelIndex];
            props.fontSize = 20;
            props.fontStyle = Text.BOLD;
          }
        )
      )
    );
  }
}
```
