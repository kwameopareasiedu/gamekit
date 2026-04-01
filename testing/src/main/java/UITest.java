import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.Settings;
import dev.gamekit.settings.WindowMode;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.widgets.Column;
import dev.gamekit.ui.widgets.Field;
import dev.gamekit.ui.widgets.FieldConfig;
import dev.gamekit.ui.widgets.Widget;
import dev.gamekit.utils.Spacing;
import ui.ComposeButton;

import java.awt.*;

public class UITest extends Scene {
  private String text = "Hello";

  public UITest() {
    super("UI Test");
    Widget.DEBUG = true;
  }

  public static void main(String[] args) {
    Application app = new Application(
      new Settings(
        "UI Testing",
        WindowMode.BORDERLESS
      )
    ) { };
    app.loadScene(new UITest());
    app.run();
  }

  @Override
  protected void render() {
    Renderer.clear(Color.BLACK);
  }

  @Override
  protected Widget createUI() {
    return Column.create(
      props -> {
        props.mainAxisAlignment = MainAxisAlignment.CENTER;
        props.crossAxisAlignment = CrossAxisAlignment.CENTER;
        props.gapSize = 24;
      },
      Field.create(
        (FieldConfig.Updater) props -> {
          props.text = text;
          props.color = Color.BLACK;
          props.padding = new Spacing(12, 16);
          props.changeListener = (ev) -> {
            this.text = ev.value;
            updateUI();
          };
        }
      ),
      ComposeButton.create("Click Me", ev -> { })
    );
  }
}
