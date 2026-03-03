# Checkbox Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Checkbox` is a widget which renders a checkbox toggle to the left of its child. `Checkbox`
uses the [9-patch algorithm](https://en.wikipedia.org/wiki/9-slice_scaling){:target="_blank"} to render its icon
background.

## Properties

| Property         | Description                                                        |
|------------------|--------------------------------------------------------------------|
| `defaultIcon`    | The checkbox icon when not toggled                                 |
| `toggledIcon`    | The checkbox icon when not toggled                                 |
| `iconPadding`    | The 9-patch padding to be applied to the rendered icons background |
| `iconWidth`      | The checkbox icon width                                            |
| `iconHeight`     | The checkbox icon height                                           |
| `gapSize`        | The spacing between the checkbox toggle and its child              |
| `toggled`        | The toggled state of the widget                                    |
| `changeListener` | The change event handler to invoke when toggled                    |

## Usage

```java
boolean toggled = false;

Checkbox.create(
  props -> {
    props.defaultIcon = IO.getResourceImage("default-bg.png");
    props.toggledIcon = IO.getResourceImage("hover-bg.png");
    props.iconPadding = new Spacing(10, 20, 10, 24);
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
