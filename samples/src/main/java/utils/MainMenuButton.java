package utils;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.ui.events.MouseEvent;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Spacing;

@WidgetBuilder
public class MainMenuButton extends Compose {
  @WidgetBuilderField
  protected String text;

  public MainMenuButton(String text, MouseEvent.Handler mouseListener) {
    super(
      ComposeConfig.child(
        Button.create(
          ButtonConfig.edgeInsets(new Spacing(12, 12, 16, 12)),
          ButtonConfig.mouseListener(mouseListener),
          ButtonConfig.child(
            Padding.create(
              PaddingConfig.padding(new Spacing(12, 12, 16, 12)),
              PaddingConfig.child(
                Text.create(
                  TextConfig.fontSize(20),
                  TextConfig.fontStyle(Text.BOLD),
                  TextConfig.text(text)
                )
              )
            )
          )
        )
      )
    );

    this.text = text;
  }

  public static MainMenuButton create(String text, MouseEvent.Handler mouseListener) {
    return new MainMenuButton(text, mouseListener);
  }

  //    @Override
  //    public boolean stateEquals(Widget widget) {
  //      if (widget instanceof MainMenuButton mainMenuButton)
  //        return Objects.equals(text, mainMenuButton.text);
  //
  //      return false;
  //    }

  @Override
  protected void performUpdate(Widget widget) {
    this.text = ((MainMenuButton) widget).text;
  }
}
