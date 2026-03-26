import dev.gamekit.core.Application;
import dev.gamekit.core.IO;
import dev.gamekit.core.Scene;
import dev.gamekit.settings.*;
import dev.gamekit.ui.enums.Alignment;
import dev.gamekit.ui.enums.CrossAxisAlignment;
import dev.gamekit.ui.enums.ImageFit;
import dev.gamekit.ui.enums.MainAxisAlignment;
import dev.gamekit.ui.widgets.*;
import dev.gamekit.ui.widgets.Button;
import dev.gamekit.ui.widgets.Checkbox;
import dev.gamekit.ui.widgets.Image;
import dev.gamekit.ui.widgets.Panel;
import dev.gamekit.utils.EngineImage;
import dev.gamekit.utils.Spacing;
import utils.MainMenuButton;
import utils.SubMenuButton;

import java.awt.*;

import static dev.gamekit.utils.Math.degToRad;

/**
 * This demo shows how to build user interfaces and performs the following actions:
 * <ul>
 *   <li>Creates an {@link Application application}</li>
 *   <li>Overrides the {@link Scene#createUI} method to construct the user interface</li>
 * </ul>
 */
public class Demo4UserInterface extends Scene {
  private static final EngineImage BACKDROP = IO.getImage("planetfall-artwork.jpg");
  private static final EngineImage LOGO = IO.getImage("planetfall-logo.png");
  private static final EngineImage SCRIM = IO.getImage("transparent-black.png");
  private static final EngineImage THUMB = IO.getImageWithInsets("slider-thumb.png", 10, 10, 10, 10);
  private static final EngineImage PROGRESS_TRACK = IO.getImageSlice("progress.png", 0, 4, 48, 8);
  private static final EngineImage PROGRESS_FILL = IO.getImageSliceWithInsets("progress.png", 50, 4, 4, 8, 0, 1, 0, 1);
  private static final EngineImage HEADPHONES_IMG = IO.getImage("headphones.jpg");

  private String fieldValue = "Hello";
  private boolean checkboxValue = false;
  private double sliderValue = 65;

  public Demo4UserInterface() {
    super("Main Scene");
    Widget.DEBUG_DRAW = true;
  }

  public static void main(String[] args) {
    Application game = new Application(
      new Settings(
        "Demo 4 - Declarative UI",
        Resolution.HD,
        WindowMode.BORDERLESS,
        Antialiasing.ON,
        TextAntialiasing.ON,
        AlphaInterpolation.SPEED,
        ImageInterpolation.NEAREST,
        RenderingStrategy.SPEED,
        Dithering.OFF
      )
    ) { };
    game.loadScene(new Demo4UserInterface());
    game.run();
  }

  @Override
  public Widget createUI() {
    return Measure.create(
      props -> {
        props.showRuler = true;
        props.rulerTicks = 50;
      },
      Stack.create(
        Image.create(BACKDROP),
        Align.create(
          props -> {
            props.verticalAlignment = Alignment.START;
            props.horizontalAlignment = Alignment.START;
          },
          Padding.create(
            0, 24, 0, 24,
            Sized.create(
              props -> {
                props.fixedWidth = 480.0;
                props.fixedHeight = 480.0;
              },
              Image.create(LOGO)
            )
          )
        ),
        Align.create(
          props -> props.verticalAlignment = Alignment.CENTER,
          Padding.create(
            256, 8, 16, 96,
            Theme.create(
              props -> {
                props.textFontSize = 10;
                props.textFontStyle = Text.ITALIC;
                props.textFontHeightRatio = 0.85;
                props.fieldFontHeightRatio = 0.85;
                props.fieldColor = Color.BLACK;
              },
              Column.create(
                props -> {
                  props.mainAxisAlignment = MainAxisAlignment.START;
                  props.crossAxisAlignment = CrossAxisAlignment.START;
                  props.gapSize = 24;
                },
                MainMenuButton.create("Tutorial", e -> System.out.println("0: " + e.type)),
                MainMenuButton.create("New Planet", e -> System.out.println("1: " + e.type)),
                Field.create(
                  (FieldConfig.Updater) props -> {
                    props.text = fieldValue;
                    props.fontSize = 28;
                    props.fontStyle = Text.PLAIN;
                    props.padding = new Spacing(12, 12, 12, 12);
                    props.changeListener = ev -> {
                      fieldValue = ev.value;
                      logger.debug("Field value: {}", fieldValue);
                      updateUI();
                    };
                  }
                ),
                Checkbox.create(
                  props -> {
                    props.toggled = checkboxValue;
                    props.changeListener = ev -> {
                      checkboxValue = ev.value;
                      updateUI();
                    };
                  },
                  Text.create(
                    props -> {
                      props.fontSize = 32;
                      props.fontStyle = Text.PLAIN;
                      props.text = "Active";
                    }
                  )
                ),
                Sized.create(
                  props -> {
                    props.fixedWidth = 256.0;
                    props.useIntrinsicHeight = true;
                  },
                  Slider.create(
                    (SliderConfig.Updater) props -> {
                      props.value = sliderValue;
                      props.minValue = 0.0;
                      props.maxValue = 100.0;
                      props.fillMode = Slider.FillMode.CLIP;
                      props.thumbBackground = THUMB;
                      props.changeListener = e -> {
                        sliderValue = e.value;
                        updateUI();
                      };
                    }
                  )
                ),
                Sized.create(
                  props -> {
                    props.fixedWidth = 256.0;
                    props.fixedHeight = 48.0;
                  },
                  Progress.create(
                    props -> {
                      props.value = sliderValue;
                      props.minValue = 0.0;
                      props.maxValue = 100.0;
                      props.trackBackground = PROGRESS_TRACK;
                      props.fillMargin = new Spacing(0, 12, 0, 12);
                      props.fillBackground = PROGRESS_FILL;
                      props.fillMode = Progress.FillMode.CLIP;
                    }
                  )
                ),
                Column.create(
                  props -> {
                    props.mainAxisAlignment = MainAxisAlignment.START;
                    props.crossAxisAlignment = CrossAxisAlignment.START;
                    props.gapSize = 12;
                  },
                  SubMenuButton.create("Commander Customization"),
                  SubMenuButton.create("Options"),
                  SubMenuButton.create("Credits"),
                  SubMenuButton.create("Exit Game")
                )
              )
            )
          )
        ),
        Align.create(
          props -> props.horizontalAlignment = Alignment.CENTER,
          Sized.create(
            props -> {
              props.fractionalWidth = 1.0;
              props.fractionalHeight = 0.15;
            },
            Image.create(
              props -> {
                props.fit = ImageFit.CROP;
                props.image = SCRIM;
              }
            )
          )
        ),
        Align.create(
          props -> {
            props.horizontalAlignment = Alignment.CENTER;
            props.verticalAlignment = Alignment.END;
          },
          Sized.create(
            props -> {
              props.fractionalWidth = 1.0;
              props.fractionalHeight = 0.15;
            },
            Stack.create(
              Sized.create(
                props -> {
                  props.fractionalWidth = 1.0;
                  props.fractionalHeight = 1.0;
                },
                Image.create(
                  props -> {
                    props.fit = ImageFit.STRETCH;
                    props.image = SCRIM;
                  }
                )
              ),
              Sized.create(
                props -> {
                  props.fractionalWidth = 1.0;
                  props.fractionalHeight = 1.0;
                },
                Row.create(
                  props -> {
                    props.mainAxisAlignment = MainAxisAlignment.END;
                    props.crossAxisAlignment = CrossAxisAlignment.CENTER;
                    props.gapSize = 24;
                  },
                  Sized.create(
                    props -> {
                      props.fixedWidth = 48.0;
                      props.fixedHeight = 48.0;
                    },
                    Panel.create(
                      props -> {
                        props.color = Color.RED;
                        props.background = null;
                        props.cornerRadius = 4;
                      },
                      Empty.create()
                    )
                  ),
                  Button.create(
                    props -> { },
                    Padding.create(
                      12, 12, 18, 12,
                      Text.create(
                        props -> {
                          props.fontSize = 12;
                          props.fontStyle = Text.BOLD;
                          props.text = "Create Account";
                        }
                      )
                    )
                  ),
                  Button.create(
                    props -> { },
                    Padding.create(
                      12, 12, 18, 12,
                      Text.create(
                        props -> {
                          props.fontSize = 12;
                          props.fontStyle = Text.BOLD;
                          props.text = "Login";
                        }
                      )
                    )
                  )
                )
              )
            )
          )
        ),
        Align.create(
          props -> {
            props.horizontalAlignment = Alignment.END;
            props.verticalAlignment = Alignment.CENTER;
          },
          Rotated.create(
            degToRad(30),
            Scaled.create(
              1.0,
              Sized.create(
                props -> {
                  props.fixedWidth = 200.0;
                  props.fixedHeight = 200.0;
                },
                Image.create(HEADPHONES_IMG)
              )
            )
          )
        ),
        Center.create(
          Sized.create(
            props -> props.fixedWidth = props.fixedHeight = 32.0,
            Spinner.create()
          )
        )
      )
    );
  }
}
