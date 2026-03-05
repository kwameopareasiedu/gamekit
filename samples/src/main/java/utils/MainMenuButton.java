package utils;

import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Spacing;

public class MainMenuButton extends Compose {
  private final String text;
  private final MouseEvent.Handler mouseListener;

  public MainMenuButton(String text, MouseEvent.Handler mouseListener) {
    this.text = text;
    this.mouseListener = mouseListener;
  }

  public static MainMenuButton create(String text, MouseEvent.Handler mouseListener) {
    return new MainMenuButton(text, mouseListener);
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
