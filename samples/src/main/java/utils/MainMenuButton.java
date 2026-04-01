package utils;

import dev.gamekit.annotations.CustomWidgetBuilder;
import dev.gamekit.annotations.CustomWidgetBuilderField;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.widgets.*;

@CustomWidgetBuilder
public class MainMenuButton extends StatelessCompose {
  @CustomWidgetBuilderField
  public String text;
  @CustomWidgetBuilderField(comparable = false)
  public MouseEvent.Handler mouseListener;

  public MainMenuButton(MainMenuButtonConfig config) {
    super(config);
  }

  public static MainMenuButton create(MainMenuButtonConfig.Updater updater) {
    MainMenuButtonConfig config = new MainMenuButtonConfig();
    updater.update(config);
    return new MainMenuButton(config);
  }

  public static MainMenuButton create(String text, MouseEvent.Handler mouseListener) {
    return create(
      props -> {
        props.text = text;
        props.mouseListener = mouseListener;
      }
    );
  }

  @Override
  protected Widget build() {
    return Button.create(
      props -> props.mouseListener = mouseListener,
      Padding.create(
        12, 12, 16, 12,
        Text.create(
          props -> {
            props.text = text;
            props.fontSize = 20;
            props.fontStyle = Text.BOLD;
          }
        )
      )
    );
  }
}
