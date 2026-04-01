package utils;

import dev.gamekit.ui.widgets.*;

public class SubMenuButton extends StatelessCompose {
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
      props -> { },
      Padding.create(
        12, 12, 16, 12,
        Text.create(text)
      )
    );
  }
}
