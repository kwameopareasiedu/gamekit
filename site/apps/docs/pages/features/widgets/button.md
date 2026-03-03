# Button Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Button` is a widget which is clicked to trigger an action, and also renders
a [9-patch](https://en.wikipedia.org/wiki/9-slice_scaling){:target="_blank"} background around its child.

## Properties

| Property            | Description                                                                  |
|---------------------|------------------------------------------------------------------------------|
| `defaultBackground` | The default background image                                                 |
| `hoverBackground`   | The background when the mouse pointer is over it                             |
| `pressedBackground` | The background when the mouse pointer is pressed on it, but not yet released |
| `padding`           | The 9-patch padding to be applied to the rendered background                 |
| `mouseListener`     | The mouse event handler to invoke when an action occurs                      |

## Usage

```java
Button.create(
  props -> {
    props.defaultBackground = IO.getResourceImage("default-bg.png");
    props.hoverBackground = IO.getResourceImage("hover-bg.png");
    props.pressedBackground = IO.getResourceImage("pressed-bg.png");
    props.padding = new Spacing(10, 20, 10, 24);
    props.mouseListener = (event) -> {
      switch (event.type) {
        case ENTER -> System.out.println("Mouse entered");
        case MOTION -> System.out.println("Mouse moved");
        case DOWN -> System.out.println("Mouse just pressed");
        case PRESS -> System.out.println("Mouse being held down");
        case RELEASE -> System.out.println("Mouse released");
        case CLICK -> System.out.println("Mouse clicked");
        case EXIT -> System.out.println("Mouse exited");
      }
    }
  },
  Text.create("Panel child"),
);
```
