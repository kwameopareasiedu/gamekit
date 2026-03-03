# Field Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Field` widget is an extension of [Text](text.md) which allows for user input. `Field` widget uses
the [9-patch algorithm](https://en.wikipedia.org/wiki/9-slice_scaling){:target="_blank"} to render its background

## Properties

In addition to all [Text properties](text.md#properties), field also has these properties:

| Property            | Description                                                           |
|---------------------|-----------------------------------------------------------------------|
| `defaultBackground` | The default input background                                          |
| `focusBackground`   | The input background when focused                                     |
| `edgeInsets`        | The 9-patch padding to be applied to the rendered background          |
| `padding`           | The padding for the input box                                         |
| `focusListener`     | The handler which gets invoked when focus changes                     |
| `keyCharListener`   | The handler which gets invoked when keys are pressed inside the field |
| `changeListener`    | The change event handler to invoke when the field text changes        |

## Usage

```java
Field.create(
  props -> {
    props.defaultBackground = IO.getResourceImage("default-bg.png");
    props.focusBackground = IO.getResourceImage("focus-bg.png");
    props.edgeInsets = new Spacing(10, 20, 10, 24);
    props.padding = new Spacing(10, 20, 10, 24);
    props.focusListener = (event) -> {
      switch (event.type) {
        case FocusEvent.Type.FOCUS -> System.out.println("Gained focus");
        case FocusEvent.Type.BLUR -> System.out.println("Lost focus");
      }
    };
    props.keyCharListener = (event) -> {
      System.out.println("Pressed key character: " + event.charPressed);
    };
    props.changeListener = (event) -> {
      System.out.println("Value changed: " + event.value);
    };

    // Inherited from Text
    props.text = "Hello GameKit";
    props.font = IO.getResourceFont("custom-font.ttf");
    props.fontSize = 24;
    props.fontHeightRatio = 0.9;
    props.fontStyle = Text.ITALIC;
    props.color = Color.RED;
    props.backgroundColor = Color.WHITE;
    props.alignment = Alignment.CENTER;
    props.shadowEnabled = true;
    props.shadowOffsetX = 2;
    props.shadowOffsetY = 3;
    props.shadowColor = Color.BLUE;
  }
);
```
