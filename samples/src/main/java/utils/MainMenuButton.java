package utils;

import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.widgets.Button;
import dev.gamekit.ui.widgets.Compose;
import dev.gamekit.ui.widgets.Padding;
import dev.gamekit.ui.widgets.Text;
import dev.gamekit.utils.Spacing;

public class MainMenuButton extends Compose {
  public MainMenuButton(String text, MouseEvent.Handler mouseListener) {
    super(
      Button.create(
        props -> {
          props.padding = new Spacing(12, 12, 16, 12);
          props.mouseListener = mouseListener;
        },
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
      )
    );
  }

  public static MainMenuButton create(String text, MouseEvent.Handler mouseListener) {
    return new MainMenuButton(text, mouseListener);
  }
}
