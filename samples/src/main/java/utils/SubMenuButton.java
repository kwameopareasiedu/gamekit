package utils;

import dev.gamekit.ui.widgets.Button;
import dev.gamekit.ui.widgets.Compose;
import dev.gamekit.ui.widgets.Padding;
import dev.gamekit.ui.widgets.Text;
import dev.gamekit.utils.Spacing;

public class SubMenuButton extends Compose {
  public SubMenuButton(String text) {
    super(
      Button.create(
        props -> {
          props.padding = new Spacing(6, 6, 8, 6);
        },
        Padding.create(
          12, 12, 16, 12,
          Text.create(
            props -> {
              props.text = text;
            }
          )
        )
      )
    );
  }

  public static SubMenuButton create(String text) {
    return new SubMenuButton(text);
  }
}
