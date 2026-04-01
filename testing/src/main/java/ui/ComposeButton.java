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

  public ComposeButton(ComposeButtonConfig config) {
    super(config);
  }

  public static ComposeButton create(ComposeButtonConfig.Updater updater) {
    ComposeButtonConfig config = new ComposeButtonConfig();
    updater.update(config);
    return new ComposeButton(config);
  }

  public static ComposeButton create(String text, MouseEvent.Handler mouseListener) {
    return create(
      props -> {
        props.text = text;
        props.mouseListener = mouseListener;
      }
    );
  }

  @Override
  protected ComposeButtonState createState() {
    return new ComposeButtonState();
  }

  protected static class ComposeButtonState extends State<ComposeButton> {
    private int clickCount = 1;
    
    @Override
    protected Widget build() {
      return Button.create(
        props -> props.mouseListener = (ev) -> {
          if (ev.type == MouseEvent.Type.CLICK) {
            clickCount += clickCount;
            updateState();
          }

          widget.mouseListener.handleEvent(ev);
        },
        Padding.create(
          12, 12, 16, 12,
          Text.create(
            props -> {
              props.text =widget.text + " " + clickCount;
              props.fontSize = 20;
              props.fontStyle = Text.BOLD;
            }
          )
        )
      );
    }
  }
}
