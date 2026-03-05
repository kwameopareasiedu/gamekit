# Button Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Button` is a widget which is clicked to trigger an action, and also renders a background behind its child.

## Properties

| Property            | Description                                                                  |
|---------------------|------------------------------------------------------------------------------|
| `defaultBackground` | The default background image                                                 |
| `hoverBackground`   | The background when the mouse pointer is over it                             |
| `pressedBackground` | The background when the mouse pointer is pressed on it, but not yet released |
| `mouseListener`     | The mouse event handler to invoke when an action occurs                      |

## Usage

```java
Button.create(
  props -> {
    props.defaultBackground = IO.getImage("default-bg.png");
    props.hoverBackground = IO.getImage("hover-bg.png");
    props.pressedBackground = IO.getImage("pressed-bg.png");
    props.mouseListener = (event) -> {
      switch (event.type) {
        case MouseEvent.Type.ENTER -> System.out.println("Mouse entered");
        case MouseEvent.Type.MOTION -> System.out.println("Mouse moved");
        case MouseEvent.Type.DOWN -> System.out.println("Mouse just pressed");
        case MouseEvent.Type.PRESS -> System.out.println("Mouse being held down");
        case MouseEvent.Type.RELEASE -> System.out.println("Mouse released");
        case MouseEvent.Type.CLICK -> System.out.println("Mouse clicked");
        case MouseEvent.Type.EXIT -> System.out.println("Mouse exited");
      }
    };
  },
  Text.create("Panel child"),
);
```
