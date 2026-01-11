package utils;

import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Spacing;

public class SubMenuButton extends Compose {
  private final String text;

  public SubMenuButton(String text) {
    this.text = text;
  }

  public static SubMenuButton create(String text) {
    return new SubMenuButton(text);
  }

  @Override
  protected Widget build() {
    return Button.create(
      props -> props.padding = new Spacing(6, 6, 8, 6),
      Padding.create(
        12, 12, 16, 12,
        Text.create(text)
      )
    );
  }
}
