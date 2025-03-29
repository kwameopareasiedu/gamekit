package dev.gamekit.ui.events;

public class MouseClickEvent extends MouseEvent {
  public final int buttonIndex;

  public MouseClickEvent(int x, int y, int buttonIndex) {
    super(x, y);
    this.buttonIndex = buttonIndex;
  }
}
