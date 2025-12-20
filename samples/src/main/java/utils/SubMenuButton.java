package utils;

import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Spacing;

public class SubMenuButton extends Compose {
  public SubMenuButton(String text) {
    super(
      ComposeConfig.child(
        Button.create(
          ButtonConfig.edgeInsets(new Spacing(6, 6, 8, 6)),
          ButtonConfig.child(
            Padding.create(
              PaddingConfig.padding(new Spacing(12, 12, 16, 12)),
              PaddingConfig.child(
                Text.create(
                  TextConfig.text(text)
                )
              )
            )
          )
        )
      )
    );
  }

  public static SubMenuButton create(String text) {
    return new SubMenuButton(text);
  }
}
