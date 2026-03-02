# User Interface (UI)

_[Back To Features](./overview.md)_

User interface (UI) refers to the visual elements that convey information to the player and can allow the player
interact with the game world. These components include text, buttons, sliders, input boxes and toggles amongst others.

<div class="caption-image-container" style="width:65%;margin:0 auto 24px;display:flex">
  <img src="/assets/industrio-ui.png" alt="Industrio User Interface">
  <small><a href="https://kwameopareasiedu.itch.io/industrio" target="_blank">Industrio</a> UI (Created with <strong>GameKit</strong>)</small>
</div>

GameKit's provides a simple framework that makes creating and managing user-interfaces a breeze, compared to other game
engines.

## The Widget

A widget is an object that represents a portion of your UI and are the building blocks of UI in GameKit.

A widget can be a piece of text, a button, panel, column or row. Widgets can have visual representation (I.e. something
you can see on screen) (E.g. text, progress) or handle layout of other widgets with no visual representation
themselves (E.g. row, column, stack, padding, align) or sometimes both (E.g. button).

In our UI sample from the chapter on [Scenes](ecs.md#user-interface), we overrode the `createUI` method and returned
this tree of widgets:

```
Align -> Padding -> Text
```

```java
/** Hidden imports for brevity */

public class UIShowcase extends Scene {
  /** Hidden other scene code for brevity */
  
  @Override
  protected Widget createUI() {
    return Align.create(
      props -> {
        props.horizontalAlignment = Alignment.CENTER;
        props.verticalAlignment = Alignment.END;
      },
      Padding.create(
        48, Text.create(
          props -> {
            props.text = "Hello World";
            props.fontSize = 32;
            props.alignment = Alignment.CENTER;
            props.fontStyle = Text.BOLD;
          }
        )
      )
    );
  }
}
```

This is generally how widgets are composed in GameKit. You start from your root widget and branch out to create the
specific elements needed.

Widgets in GameKit are designed to be as atomic as possible, meaning they only do one thing, and they do it really well.
As an example, instead of a `Button` widget having a padding property, it should be wrapped in a `Padding` widget. Also,
instead of `Button` having a text property, a `Text` widget will be used as it's a child.

The atomic nature of widgets prevents duplicated features, keeps their source code simple and very easy to debug and
maintain.

## Widget Categories

GameKit widgets are placed in two (2) categories, which are summarized in the table below:

| Category | Description                                                                                                                 | Widgets                                                                                                                                                                                                                                                                                                                                                                           |
|----------|-----------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Parent   | Widgets which nest one or more widgets within them. They are _generally_ used for layout and don't have a visual appearance | [Row](widgets/row.md), [Column](widgets/column.md), [Stack](widgets/stack.md), [Scaled](widgets/scaled.md), [Padding](widgets/padding.md), [Align](widgets/align.md), [Sized](widgets/sized.md), [Theme](widgets/theme.md), [Panel](widgets/panel.md), [Opacity](widgets/opacity.md), [Button](widgets/button.md), [Checkbox](widgets/checkbox.md), [Compose](widgets/compose.md) |
| Leaf     | Widgets which don't nest other widgets and have visual representations                                                      | [Text](widgets/text.md), [Field](widgets/field.md), [Image](widgets/image.md), [Progress](widgets/progress.md), [Slider](widgets/slider.md), [Colored](widgets/colored.md), [Empty](widgets/empty.md)                                                                                                                                                                             |

## Widget Customization

Widgets can be created in one of two ways:

- Invoking the constructor (E.g. `new Text(configUpdater)`) or
- Calling the static `create` method on the widget class (E.g. `Text.create(configUpdater)`)

In each case you need to provide a lambda function which you can use to customize the widget properties. This function
is called with a new instance of the widget's properties which you can then customize.

```java
Padding.create(
  props -> {
    // Padding of 10px top, 20px right, 30px bottom, 40px left
    props.padding = new Spacing(10, 20, 30, 40);
  },
  /** Child widget here */
)
```

Some widgets provide overloads of the static `create` method for simpler setup. An example is shown below with the
`Padding` widget, which is the same as the code snippet above:

```java
Padding.create(
  10, 20, 30, 40,
  /** Child widget here */
)
```
