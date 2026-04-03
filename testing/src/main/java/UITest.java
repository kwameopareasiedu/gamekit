import dev.gamekit.animation.Animation;
import dev.gamekit.core.Application;
import dev.gamekit.core.Renderer;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.Settings;
import dev.gamekit.settings.WindowMode;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.utils.Spacing;
import ui.ComposeButton;

import java.awt.*;

public class UITest extends Scene {
  private final Animation timerAnimation;
  private String text = "Hello";
  private int timer = 0;

  public UITest() {
    super("UI Test");
    timerAnimation = new Animation(1000, Animation.RepeatMode.RESTART);
    timerAnimation.setStateListener(state -> {
      if (state == Animation.State.RESTARTED) {
        timer++;
        updateUI();
      }
    });
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
  protected void start() {
    super.start();
    timerAnimation.start();
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
      Text.create(
        props -> {
          props.text = String.valueOf(timer);
          props.alignment = Alignment.CENTER;
          props.fontSize = 32;
        }
      ),
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
      ComposeButton.create(
        "Click Me",
        ev -> { },
        Builder.create(
          () -> timer > 5 ? Text.create("World") : Text.create("Hello")
        )
      )
    );
  }

  @Override
  protected void dispose() {
    super.dispose();
    timerAnimation.end();
  }
}
