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
import dev.gamekit.utils.Spacing;
import utils.MainMenuButton;
import utils.SubMenuButton;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This demo shows how to build user interfaces and performs the following actions:
 * <ul>
 *   <li>Creates an {@link Application application}</li>
 *   <li>Overrides the {@link Scene#createUI} method to construct the user interface</li>
 * </ul>
 */
public class Demo4UserInterface extends Scene {
  private static final BufferedImage BACKDROP = IO.getResourceImage("planetfall-artwork.jpg");
  private static final BufferedImage LOGO = IO.getResourceImage("planetfall-logo.png");
  private static final BufferedImage SCRIM = IO.getResourceImage("transparent-black.png");
  private static final BufferedImage THUMB = IO.getResourceImage("slider-thumb.png");
  private static final BufferedImage PROGRESS_TRACK =
    IO.getResourceImage("progress.png", 0, 4, 48, 8);
  private static final BufferedImage PROGRESS_FILL =
    IO.getResourceImage("progress.png", 50, 4, 4, 8);

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
        WindowMode.WINDOWED,
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
    return Stack.create(
      StackConfig.children(
        Image.create(
          ImageConfig.image(BACKDROP)
        ),
        Align.create(
          AlignConfig.child(
            Padding.create(
              PaddingConfig.padding(new Spacing(0, 24, 0, 24)),
              PaddingConfig.child(
                FixedSize.create(
                  FixedSizeConfig.width(480.0),
                  FixedSizeConfig.height(480.0),
                  FixedSizeConfig.child(
                    Image.create(
                      ImageConfig.image(LOGO)
                    )
                  )
                )
              )
            )
          )
        ),
        Align.create(
          AlignConfig.verticalAlignment(Alignment.CENTER),
          AlignConfig.child(
            Padding.create(
              PaddingConfig.padding(new Spacing(256, 8, 16, 96)),
              PaddingConfig.child(
                ThemeOld.create(
                  ThemeConfig.textFontSize(10),
                  ThemeConfig.textFontStyle(Text.ITALIC),
                  ThemeConfig.child(
                    Column.create(
                      ColumnConfig.mainAxisAlignment(MainAxisAlignment.START),
                      ColumnConfig.crossAxisAlignment(CrossAxisAlignment.STRETCH),
                      ColumnConfig.gapSize(24),
                      ColumnConfig.children(

                        MainMenuButton.create("Tutorial", e -> System.out.println("0: " + e.type)),
                        MainMenuButton.create("New Planet", e -> System.out.println("1: " + e.type)),
                        Field.create(
                          FieldConfig.fontSize(28),
                          FieldConfig.fontStyle(Text.PLAIN),
                          FieldConfig.edgeInsets(new Spacing(12, 12, 12, 12)),
                          FieldConfig.padding(new Spacing(12, 12, 12, 12)),
                          FieldConfig.changeListener(ev -> {
                            fieldValue = ev.value;
                            logger.debug("Field value: {}", fieldValue);
                            updateUI();
                          }),
                          FieldConfig.text(fieldValue)
                        ),
                        Checkbox.create(
                          CheckboxConfig.toggled(checkboxValue),
                          CheckboxConfig.iconEdgeInsets(new Spacing(4, 4, 4, 4)),
                          CheckboxConfig.changeListener(ev -> {
                            checkboxValue = ev.value;
                            updateUI();
                          }),
                          CheckboxConfig.child(
                            Text.create(
                              TextConfig.fontSize(32),
                              TextConfig.fontStyle(Text.PLAIN),
                              TextConfig.text("Active")
                            )
                          )
                        ),
                        IntrinsicSize.create(
                          //  IntrinsicSizeConfig.width(256),
                          //  IntrinsicSizeConfig.intrinsicHeight(),
                          IntrinsicSizeConfig.child(
                            Slider.create(
                              SliderConfig.value(sliderValue),
                              SliderConfig.minValue(0.0),
                              SliderConfig.maxValue(100.0),
                              SliderConfig.fillMode(Slider.FillMode.CLIP),
                              SliderConfig.thumbBackground(THUMB),
                              SliderConfig.thumbEdgeInsets(new Spacing(10, 10, 10, 10)),
                              SliderConfig.changeListener(e -> {
                                sliderValue = e.value;
                                updateUI();
                              })
                            )
                          )
                        ),
                        FixedSize.create(
                          FixedSizeConfig.width(256.0),
                          FixedSizeConfig.height(48.0),
                          FixedSizeConfig.child(
                            Progress.create(
                              ProgressConfig.value(sliderValue),
                              ProgressConfig.minValue(0.0),
                              ProgressConfig.maxValue(100.0),
                              ProgressConfig.trackBackground(PROGRESS_TRACK),
                              ProgressConfig.trackEdgeInsets(new Spacing(0, 0, 0, 0)),
                              ProgressConfig.fillMargin(new Spacing(0, 12, 0, 12)),
                              ProgressConfig.fillBackground(PROGRESS_FILL),
                              ProgressConfig.fillEdgeInsets(new Spacing(0, 1, 0, 1)),
                              ProgressConfig.fillMode(Progress.FillMode.CLIP)
                            )
                          )
                        ),
                        Column.create(
                          ColumnConfig.mainAxisAlignment(MainAxisAlignment.START),
                          ColumnConfig.crossAxisAlignment(CrossAxisAlignment.START),
                          ColumnConfig.gapSize(12),
                          ColumnConfig.children(
                            SubMenuButton.create("Commander Customization"),
                            SubMenuButton.create("Options"),
                            SubMenuButton.create("Credits"),
                            SubMenuButton.create("Exit Game")
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        ),
        Align.create(
          AlignConfig.horizontalAlignment(Alignment.CENTER),
          AlignConfig.child(
            FractionalSize.create(
              FractionalSizeConfig.widthRatio(1.0),
              FractionalSizeConfig.heightRatio(0.15),
              FractionalSizeConfig.child(
                Image.create(
                  ImageConfig.fit(ImageFit.CROP),
                  ImageConfig.image(SCRIM)
                )
              )
            )
          )
        ),
        Align.create(
          AlignConfig.horizontalAlignment(Alignment.CENTER),
          AlignConfig.verticalAlignment(Alignment.END),
          AlignConfig.child(
            FractionalSize.create(
              FractionalSizeConfig.widthRatio(1.0),
              FractionalSizeConfig.heightRatio(0.15),
              FractionalSizeConfig.child(
                Stack.create(
                  StackConfig.children(
                    FractionalSize.create(
                      FractionalSizeConfig.widthRatio(1.0),
                      FractionalSizeConfig.heightRatio(1.0),
                      FractionalSizeConfig.child(
                        Image.create(
                          ImageConfig.fit(ImageFit.STRETCH),
                          ImageConfig.image(SCRIM)
                        )
                      )
                    ),
                    FractionalSize.create(
                      FractionalSizeConfig.widthRatio(1.0),
                      FractionalSizeConfig.heightRatio(1.0),
                      FractionalSizeConfig.child(
                        Row.create(
                          RowConfig.mainAxisAlignment(MainAxisAlignment.END),
                          RowConfig.crossAxisAlignment(CrossAxisAlignment.CENTER),
                          RowConfig.gapSize(24),
                          RowConfig.children(
                            FixedSize.create(
                              FixedSizeConfig.width(48.0),
                              FixedSizeConfig.height(48.0),
                              FixedSizeConfig.child(
                                Colored.create(
                                  ColoredConfig.color(Color.RED),
                                  ColoredConfig.borderRadius(4)
                                )
                              )
                            ),
                            Button.create(
                              ButtonConfig.edgeInsets(new Spacing(12, 12, 18, 12)),
                              ButtonConfig.child(
                                Text.create(
                                  TextConfig.fontSize(12),
                                  TextConfig.fontStyle(Text.BOLD),
                                  TextConfig.text("Create Account")
                                )
                              )
                            ),
                            Button.create(
                              ButtonConfig.edgeInsets(new Spacing(12, 12, 18, 12)),
                              ButtonConfig.child(
                                Text.create(
                                  TextConfig.fontSize(12),
                                  TextConfig.fontStyle(Text.BOLD),
                                  TextConfig.text("Login")
                                )
                              )
                            )
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    );
  }
}
