# Text Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Text` is a widget which renders text to the screen.

## Properties

| Property          | Description                                                              |
|-------------------|--------------------------------------------------------------------------|
| `text`            | The text to render                                                       |
| `font`            | The font object to apply                                                 |
| `fontSize`        | The font size                                                            |
| `fontHeightRatio` | A ratio for computing the font height due to certain font metrics issues |
| `fontStyle`       | The font style                                                           |
| `color`           | The font foreground color                                                |
| `backgroundColor` | The font background color                                                |
| `alignment`       | The alignment of the text within the widget bounds                       |
| `shadowEnabled`   | Indicates whether to enable shadows                                      |
| `shadowOffsetX`   | The horizontal text shadow offset                                        |
| `shadowOffsetY`   | The vertical text shadow offset                                          |
| `shadowColor`     | The text shadow color                                                    |

## Usage

```java
Text.create("Hello GameKit");

Text.create(
  props -> {
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
