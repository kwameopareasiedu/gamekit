# Checkbox Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Checkbox` is a widget which renders a checkbox toggle to the left of its child.

## Properties

| Property         | Description                                           |
|------------------|-------------------------------------------------------|
| `defaultIcon`    | The checkbox icon when not toggled                    |
| `toggledIcon`    | The checkbox icon when not toggled                    |
| `iconWidth`      | The checkbox icon width                               |
| `iconHeight`     | The checkbox icon height                              |
| `gapSize`        | The spacing between the checkbox toggle and its child |
| `toggled`        | The toggled state of the widget                       |
| `changeListener` | The change event handler to invoke when toggled       |

## Usage

```java
boolean toggled = false;

Checkbox.create(
  props -> {
    props.defaultIcon = IO.getImage("default-bg.png");
    props.toggledIcon = IO.getImage("hover-bg.png");
    props.iconWidth = 64;
    props.iconHeight = 64;
    props.gapSize = 24;
    props.toggled = toggled;
    props.changeListener = (event) -> {
      if (event.value) {
        System.out.println("Checkbox enabled");
      } else {
        System.out.println("Checkbox disabled");
      }
      
      toggled = event.value
    };
  },
  Text.create("Panel child"),
);
```
