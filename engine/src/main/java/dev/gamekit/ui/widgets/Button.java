package dev.gamekit.ui.widgets;

import dev.gamekit.core.Scene;
import dev.gamekit.ui.BlendComposite;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.events.*;

import java.awt.*;
import java.util.Objects;

/** A {@link Widget} which can be clicked to trigger an event */
public class Button extends SingleChildParent {
  protected MouseMotionEvent.Listener mouseMotionListener;
  protected MouseEnterEvent.Listener mouseEnterListener;
  protected MouseExitEvent.Listener mouseExitListener;
  protected MouseClickEvent.Listener mouseClickListener;
  protected Color hoverTintColor;
  protected boolean intersectsWithMouse;

  protected Button(Widget child) {
    super(child);
    hoverTintColor = new Color(0x22ffffff, true);
  }

  public static Button create(Widget child) {
    return new Button(child);
  }

  @Override
  protected void performLayout(Constraints constraints) {
    child.layout(
      new Constraints(
        0, constraints.maxWidth(),
        0, constraints.maxHeight()
      )
    );

    intrinsicBounds.setSize(child.computedBounds.width,
      child.computedBounds.height);

    int computedWidth = constraints.constrainWidth(intrinsicBounds.width);
    int computedHeight = constraints.constrainHeight(intrinsicBounds.height);
    computedBounds.setSize(computedWidth, computedHeight);

    child.computedBounds.setPosition(0, 0);
  }

  @Override
  public void performRender(Graphics2D g) {
    super.performRender(g);

    if (intersectsWithMouse) {
      Composite composite = g.getComposite();

      g.setColor(hoverTintColor);
      g.setComposite(BlendComposite.MULTIPLY);
      g.fillRect(0, 0, computedBounds.width, computedBounds.height);
      g.setComposite(composite);
    }
  }

  @Override
  protected boolean stateEquals(Widget widget) {
    if (widget instanceof Button buttonWidget) {
      return Objects.equals(hoverTintColor, buttonWidget.hoverTintColor);
    }

    return false;
  }

  @Override
  public void handleEvent(InputEvent event) {
    super.handleEvent(event);

    if (event instanceof MouseClickEvent mouseClickEvent) {
      if (mouseClickListener != null)
        mouseClickListener.onMouseClick(mouseClickEvent);
    } else if (event instanceof MouseEnterEvent mouseEnterEvent) {
      intersectsWithMouse = true;
      Scene.getCurrent().redrawUI();

      if (mouseEnterListener != null)
        mouseEnterListener.onMouseEnter(mouseEnterEvent);
    } else if (event instanceof MouseExitEvent mouseExitEvent) {
      intersectsWithMouse = false;
      Scene.getCurrent().redrawUI();

      if (mouseExitListener != null)
        mouseExitListener.onMouseExit(mouseExitEvent);
    } else if (event instanceof MouseMotionEvent mouseMotionEvent) {
      if (mouseMotionListener != null)
        mouseMotionListener.onMouseMove(mouseMotionEvent);
    }
  }

  public Button withMouseMotionListener(MouseMotionEvent.Listener listener) {
    this.mouseMotionListener = listener;
    return this;
  }

  public Button withMouseEnterListener(MouseEnterEvent.Listener listener) {
    this.mouseEnterListener = listener;
    return this;
  }

  public Button withMouseExitListener(MouseExitEvent.Listener listener) {
    this.mouseExitListener = listener;
    return this;
  }

  public Button withMouseClickListener(MouseClickEvent.Listener listener) {
    this.mouseClickListener = listener;
    return this;
  }

  public Button withHoverTintColor(Color hoverTintColor) {
    this.hoverTintColor = hoverTintColor;
    return this;
  }
}
