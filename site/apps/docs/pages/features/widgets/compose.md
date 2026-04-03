# Compose Widget

_[Back To User Interface](../ui.md#widget-categories)_

`Compose` is an abstract widget for building custom widgets. Subclasses must implement the abstract `build` method which
returns the custom widget tree.

## Usage

```java
public class LabelledText extends Compose {
  private final String label;
  private final String content;

  public MainMenuButton(String label, String content) {
    this.label = label;
    this.content = content;
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
          props.text = label;
          props.alignment = Alignment.CENTER;
          props.fontStyle = Text.BOLD;
        }
      ),
      Text.create(
        props -> {
          props.text = content;
          props.alignment = Alignment.CENTER;
        }
      )
    );
  }
}
```
