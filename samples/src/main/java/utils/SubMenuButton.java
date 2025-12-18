package utils;

import dev.gamekit.annotations.WidgetBuilder;
import dev.gamekit.annotations.WidgetBuilderField;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Spacing;

@WidgetBuilder
public class SubMenuButton extends Compose {
  @WidgetBuilderField
  protected String text;

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

    this.text = text;
  }

  public static SubMenuButton create(String text) {
    return new SubMenuButton(text);
  }

  //    @Override
  //    public boolean stateEquals(Widget widget) {
  //      if (widget instanceof SubMenuButton subMenuButton)
  //        return Objects.equals(text, subMenuButton.text);
  //
  //      return false;
  //    }

  @Override
  protected void performUpdate(Widget widget) {
    this.text = ((SubMenuButton) widget).text;
  }
}
