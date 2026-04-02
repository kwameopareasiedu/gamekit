package ui;

import dev.gamekit.annotations.CustomWidgetBuilder;
import dev.gamekit.annotations.CustomWidgetBuilderField;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.widgets.*;

@CustomWidgetBuilder
public class ComposeButton extends Stateful {
  @CustomWidgetBuilderField
  public String text;
  @CustomWidgetBuilderField(comparable = false)
  public MouseEvent.Handler mouseListener;

  private final Widget child;

  public ComposeButton(ComposeButtonConfig config, Widget child) {
    super(config, "ComposeButton");
    this.child = child;
  }

  public static ComposeButton create(ComposeButtonConfig.Updater updater, Widget child) {
    ComposeButtonConfig config = new ComposeButtonConfig();
    updater.update(config);
    return new ComposeButton(config, child);
  }

  public static ComposeButton create(String text, MouseEvent.Handler mouseListener, Widget child) {
    return create(
      props -> {
        props.text = text;
        props.mouseListener = mouseListener;
      },
      child
    );
  }

  @Override
  protected State createState() {
    return new ComposeButtonState();
  }

  public class ComposeButtonState extends State {
    private int clickCount = 1;

    @Override
    protected Widget build() {
      return Column.create(
        props -> props.gapSize = 32,
        Button.create(
          props -> props.mouseListener = (ev) -> {
            if (ev.type == MouseEvent.Type.CLICK) {
              clickCount += clickCount;
              host.triggerUpdate();
            }

            mouseListener.handleEvent(ev);
          },
          Padding.create(
            12, 12, 16, 12,
            Text.create(
              props -> {
                props.text = text + " " + clickCount;
                props.fontSize = 20;
                props.fontStyle = Text.BOLD;
              }
            )
          )
        ),
        child
      );
    }

  }
}
