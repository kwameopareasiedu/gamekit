package ui;

import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.widgets.*;

public class ComposeButton extends Stateful {
  private final String text;
  private final MouseEvent.Handler mouseListener;
  private final Widget child;

  public ComposeButton(String text, MouseEvent.Handler mouseListener, Widget child) {
    super("ComposeButton");
    this.text = text;
    this.mouseListener = mouseListener;
    this.child = child;
  }

  public static ComposeButton create(String text, MouseEvent.Handler mouseListener, Widget child) {
    return new ComposeButton(text, mouseListener, child);
  }

  @Override
  protected ComposeButtonState createState() {
    return new ComposeButtonState();
  }

  public static class ComposeButtonState extends State<ComposeButton> {
    private int clickCount = 1;

    @Override
    protected Widget build(ComposeButton widget) {
      return Column.create(
        props -> props.gapSize = 32,
        Button.create(
          props -> props.mouseListener = (ev) -> {
            if (ev.type == MouseEvent.Type.CLICK) {
              clickCount += clickCount;
              widget.host.triggerUpdate();
            }

            widget.mouseListener.handleEvent(ev);
          },
          Padding.create(
            12, 12, 16, 12,
            Text.create(
              props -> {
                props.text = widget.text + " " + clickCount;
                props.fontSize = 20;
                props.fontStyle = Text.BOLD;
              }
            )
          )
        ),
        widget.child
      );
    }
  }
}
