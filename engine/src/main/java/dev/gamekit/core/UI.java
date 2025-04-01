package dev.gamekit.core;

import dev.gamekit.ui.Bounds;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.events.InputEvent;
import dev.gamekit.ui.events.MouseClickEvent;
import dev.gamekit.ui.events.MouseMotionEvent;
import dev.gamekit.ui.widgets.MultiChildParent;
import dev.gamekit.ui.widgets.Parent;
import dev.gamekit.ui.widgets.SingleChildParent;
import dev.gamekit.ui.widgets.Widget;
import dev.gamekit.utils.Constants;
import dev.gamekit.utils.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Queue;
import java.util.*;

/**
 * UI manages the user interface within for {@link Scene}.
 * A {@link Scene} will contain a single instance of UI
 */
public final class UI {
  private static final Logger LOGGER = LogManager.getLogger();

  private final WidgetTreeCreator treeCreator;
  private final Constraints windowConstraints;
  private final Queue<Widget> eventHitTestQueue;
  private final Stack<Widget> eventNotifyStack;
  private final List<InputEvent> availableInputEvents;
  private final Position mousePosition;
  private Widget tree;
  private boolean needsUpdate = false;
  private boolean needsRender = true;

  public UI(WidgetTreeCreator treeCreator) {
    Window window = Window.getInstance();

    this.windowConstraints = new Constraints(
      window.getDisplayWidth(),
      window.getDisplayWidth(),
      window.getDisplayHeight(),
      window.getDisplayHeight()
    );

    this.treeCreator = treeCreator;
    this.eventHitTestQueue = new ArrayDeque<>();
    this.eventNotifyStack = new Stack<>();
    this.availableInputEvents = new ArrayList<>();
    this.mousePosition = new Position();
  }

  /**
   * Uses the {@link Window} UI graphics object to retrieve the
   * {@link FontMetrics} for a given font
   */
  public static FontMetrics getFontMetrics(Font font) {
    return Window.getInstance().getUiGraphics().getFontMetrics(font);
  }

  /** Sets the widget tree and triggers the layout computation */
  public void setWidgetTree(Widget tree) {
    this.tree = tree;

    if (this.tree != null) {
      this.tree.layout(windowConstraints);
    }
  }

  public void triggerUpdate() {
    needsUpdate = true;
  }

  /**
   * If {@link #needsUpdate} is true, this performs a reconciliation process on
   * the widget tree.
   * <p>
   * During reconciliation, a new widget tree is generated and compared to the
   * current one. Widgets in the current tree whose states differ from those in
   * the new tree are substituted with those of the new tree.
   * <p>
   * At the end of this, the updated widget tree's layout is recomputed and
   * {@link #needsRender} is set to trigger a re-render.
   */
  public void update() {
    if (tree != null && needsUpdate)
      updateTree();

    if (inputEventsAvailable())
      dispatchAvailableInputEvents();
  }

  /** Draws the {@link Widget} tree to the {@link Window} UI target */
  public void render() {
    if (tree == null || !needsRender)
      return;

    BufferedImage canvasImage = tree.render();

    if (canvasImage == null)
      return;

    LOGGER.debug("Rendering UI");

    Window win = Window.getInstance();
    Graphics2D g = win.getUiGraphics();
    Bounds widgetBounds = tree.getComputedBounds();

    g.setBackground(Constants.TRANSPARENT_COLOR);
    g.clearRect(0, 0, win.getDisplayWidth(), win.getDisplayHeight());
    g.drawImage(
      canvasImage,
      widgetBounds.x,
      widgetBounds.y,
      widgetBounds.width,
      widgetBounds.height,
      null
    );

    needsRender = false;
  }

  /**
   * When called, this method generates a new widget tree and compares it to the
   * current one. If any widget has changed, the subtree is replaced, the layout
   * is recomputed and the {@link #needsRender} flag is set
   */
  private void updateTree() {
    Queue<Widget> currentWidgetQueue = new ArrayDeque<>();
    Queue<Widget> newWidgetQueue = new ArrayDeque<>();
    boolean treeUpdated = false;

    currentWidgetQueue.add(tree);
    newWidgetQueue.add(treeCreator.onCreateUI());

    while (true) {
      Widget treeWidget = currentWidgetQueue.poll();
      Widget newWidget = newWidgetQueue.poll();

      if (treeWidget == null && newWidget == null)
        break;

      if (!Objects.equals(treeWidget, newWidget)) {
        // Widget tree differs at this point, reconcile subtrees at this depth
        Parent treeWidgetParent = (Parent) treeWidget.getParent();

        if (treeWidgetParent == null) {
          tree = newWidget;
          treeUpdated = true;
        } else if (treeWidgetParent instanceof SingleChildParent currentParent) {
          currentParent.updateChild(newWidget);
          treeUpdated = true;
        } else if (treeWidgetParent instanceof MultiChildParent currentParent) {
          int index = currentParent.getChildren().indexOf(treeWidget);
          currentParent.updateChild(newWidget, index);
          treeUpdated = true;
        }
      } else if (treeWidget instanceof SingleChildParent currentParent
        && newWidget instanceof SingleChildParent newParent) {
        // Add child of SingleChildParent to queue for processing
        currentWidgetQueue.add(currentParent.getChild());
        newWidgetQueue.add(newParent.getChild());
      } else if (treeWidget instanceof MultiChildParent currentParent
        && newWidget instanceof MultiChildParent newParent) {
        // Add children of MultiChildParent to queue for processing
        currentWidgetQueue.addAll(currentParent.getChildren());
        newWidgetQueue.addAll(newParent.getChildren());
      }
    }

    if (treeUpdated)
      tree.layout(windowConstraints);

    needsRender = treeUpdated;
    currentWidgetQueue.clear();
    newWidgetQueue.clear();
    needsUpdate = false;
  }

  /**
   * Monitors the {@link Input} class and generates events if an action of
   * interest has occurred
   */
  private boolean inputEventsAvailable() {
    availableInputEvents.clear();

    boolean eventsAvailable = false;
    Position newMousePos = Input.getMousePosition();

    if (Input.isButtonJustReleased(Input.BUTTON_LMB)) {
      availableInputEvents.add(new MouseClickEvent(newMousePos.x, newMousePos.y, Input.BUTTON_LMB));
      eventsAvailable = true;
    } else if (Input.isButtonJustReleased(Input.BUTTON_RMB)) {
      availableInputEvents.add(new MouseClickEvent(newMousePos.x, newMousePos.y, Input.BUTTON_RMB));
      eventsAvailable = true;
    } else if (Input.isButtonJustReleased(Input.BUTTON_MMB)) {
      availableInputEvents.add(new MouseClickEvent(newMousePos.x, newMousePos.y, Input.BUTTON_MMB));
      eventsAvailable = true;
    }

    if (!mousePosition.equals(newMousePos)) {
      availableInputEvents.add(new MouseMotionEvent(newMousePos.x, newMousePos.y));
      eventsAvailable = true;
    }

    mousePosition.set(newMousePos);
    return eventsAvailable;
  }

  private void dispatchAvailableInputEvents() {
    eventHitTestQueue.clear();
    eventNotifyStack.clear();

    if (tree != null) {
      Position pos = Input.getMousePosition();
      eventHitTestQueue.add(tree);

      while (!eventHitTestQueue.isEmpty()) {
        Widget widget = eventHitTestQueue.poll();

        if (widget.hitTest(pos.x, pos.y))
          eventNotifyStack.push(widget);

        if (widget instanceof SingleChildParent parent) {
          eventHitTestQueue.add(parent.getChild());
        } else if (widget instanceof MultiChildParent parent) {
          eventHitTestQueue.addAll(parent.getChildren());
        }
      }

      if (!eventNotifyStack.isEmpty()) {
        for (InputEvent ev : availableInputEvents)
          ev.setTarget(eventNotifyStack.peek());

        while (!eventNotifyStack.isEmpty()) {
          Widget widget = eventNotifyStack.pop();

          for (InputEvent ev : availableInputEvents) {
            if (!ev.isHandled())
              widget.handleEvent(ev);
          }
        }
      }
    }
  }

  public interface WidgetTreeCreator {
    /** Called to create a widget tree */
    Widget onCreateUI();
  }

  public interface WidgetTreeUpdater {
    void onUpdate();
  }
}
