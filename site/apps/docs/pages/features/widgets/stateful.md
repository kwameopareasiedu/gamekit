# Stateful Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Stateful` is an abstract widget for building custom widgets which maintain an internal state.

Subclasses must implement the abstract `createState` method which returns the mutable state that persists across
re-renders.

## Usage

```java
import path.to.custom.confirmation.panel; // <- Import for confirmation panel widget

public class ConfirmExitButton extends Stateful {
  public ConfirmExitButton() { }
  
  private static class ConfirmExitState extends State<ConfirmExit> {
    private boolean showConfirmation = false; // <- Internal state variable not held at scene-level

    @Override
    protected Widget build(ConfirmExit widget) {
      if (showConfirmation) {
        return new ConfirmationPanel();
      } else {
        return Button.create(
          props -> {
            props.padding = new Spacing(12, 12, 16, 12);
            props.mouseListener = ev -> {
              if (ev.type == MouseEvent.Type.CLICK) {
                showConfirmation = true;
                widget.host.triggerUpdate();
              }
            };
          },
          Padding.create(
            12, 12, 16, 12,
            Text.create(
              props -> {
                props.text = "Quit";
                props.fontSize = 20;
                props.fontStyle = Text.BOLD;
              }
            )
          )
        );
      }
    }
  }
}
```
