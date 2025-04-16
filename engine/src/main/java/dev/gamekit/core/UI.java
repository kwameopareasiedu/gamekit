package dev.gamekit.core;

import dev.gamekit.ui.Bounds;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.events.InputEvent;
import dev.gamekit.ui.events.InputEventHandler;
import dev.gamekit.ui.events.InputEventStore;
import dev.gamekit.ui.events.MouseEvent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * UI manages the user interface within for {@link Scene}.
 * A {@link Scene} will contain a single instance of UI
 */
public final class UI {
  private static final Logger LOGGER = LogManager.getLogger();
  private static UI instance;

  private final WidgetTreeCreator treeCreator;
  private final Constraints windowConstraints;
  private final List<Widget> currentHitTestList;
  private final List<Widget> previousHitTestList;

  private final InputEventStore eventStore;
  private final Position mousePosition;
  private final int[] mouseButtonIds;
  private Widget tree;
  private Widget focusWidget;
  private Widget activeWidget;
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
    this.currentHitTestList = new ArrayList<>();
    this.previousHitTestList = new ArrayList<>();
    this.eventStore = new InputEventStore();
    this.mousePosition = new Position();
    this.mouseButtonIds = new int[]{
      Input.BUTTON_LMB, Input.BUTTON_RMB, Input.BUTTON_MMB
    };

    UI.instance = this;
  }

  public static UI getInstance() { return instance; }

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

  public void triggerUpdate() { needsUpdate = true; }

  public void triggerRender() { needsRender = true; }

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
  void update() {
    if (tree != null && needsUpdate)
      updateTree();

    generateInputEvents();
    dispatchInputEvents();
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
    List<Widget> currentWidgetQueue = new ArrayList<>();
    List<Widget> newWidgetQueue = new ArrayList<>();
    boolean treeUpdated = false;

    currentWidgetQueue.add(tree);
    newWidgetQueue.add(treeCreator.onCreateUI());

    while (!currentWidgetQueue.isEmpty() && !newWidgetQueue.isEmpty()) {
      Widget treeWidget = currentWidgetQueue.remove(0);
      Widget newWidget = newWidgetQueue.remove(0);

      if (!treeWidget.stateEquals(newWidget)) {
        // Widget tree differs at this point, reconcile subtrees at this depth
        Parent treeWidgetParent = (Parent) treeWidget.getParent();

        if (treeWidgetParent == null) {
          tree = newWidget;
          treeUpdated = true;
          break;
        } else if (treeWidgetParent instanceof SingleChildParent currentParent) {
          currentParent.updateChild(newWidget);
          treeUpdated = true;
          break;
        } else if (treeWidgetParent instanceof MultiChildParent currentParent) {
          int index = currentParent.getChildren().indexOf(treeWidget);
          currentParent.updateChild(index, newWidget);
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

  /** Monitors {@link Input} and generates events for input actions */
  private void generateInputEvents() {
    if (tree == null)
      return;

    eventStore.clear();
    currentHitTestList.clear();

    Position mousePosition = Input.getMousePosition();

    traverseTree(tree, TraverseDirection.DOWN, widget -> {
      if (widget instanceof InputEventHandler &&
        widget.hitTest(mousePosition.x, mousePosition.y)) {
        currentHitTestList.add(widget);
      }
    });

    // Assign the last/uppermost widget as the focused widget
    focusWidget = !currentHitTestList.isEmpty()
      ? currentHitTestList.get(currentHitTestList.size() - 1)
      : null;

    // Generate mouse motion events if the mouse position has changed
    if (!Objects.equals(this.mousePosition, mousePosition)) {
      eventStore.mouseMotionEvent = new MouseEvent(
        MouseEvent.Type.MOTION,
        mousePosition.x, mousePosition.y,
        Input.BUTTON_NONE
      );
    }

    // Generate a mouse enter event if widgets exist in currentHitTestCheckList
    // but don't exist in previousHitTestCheckList. In essence, this means
    // there are new widgets under the mouse cursor in the current frame that
    // were not in the previous frame
    for (Widget widget : currentHitTestList) {
      if (!previousHitTestList.contains(widget))
        eventStore.mouseEnterEvent = new MouseEvent(
          MouseEvent.Type.ENTER,
          mousePosition.x, mousePosition.y,
          Input.BUTTON_NONE
        );
    }

    // Generate mouse down events if a mouse button has just been pressed
    for (int mouseButtonId : mouseButtonIds) {
      if (Input.isButtonDown(mouseButtonId)) {
        eventStore.mouseDownEvents.add(
          new MouseEvent(
            MouseEvent.Type.DOWN,
            mousePosition.x, mousePosition.y,
            mouseButtonId
          )
        );
      }
    }

    // Generate mouse press events if a mouse button is being pressed
    for (int mouseButtonId : mouseButtonIds) {
      if (Input.isButtonPressed(mouseButtonId)) {
        eventStore.mousePressEvents.add(
          new MouseEvent(
            MouseEvent.Type.PRESS,
            mousePosition.x, mousePosition.y,
            mouseButtonId
          )
        );
      }
    }

    // Generate mouse release events if a mouse button has just been released
    for (int mouseButtonId : mouseButtonIds) {
      if (Input.isButtonReleased(mouseButtonId)) {
        eventStore.mouseReleaseEvents.add(
          new MouseEvent(
            MouseEvent.Type.RELEASE,
            mousePosition.x, mousePosition.y,
            mouseButtonId
          )
        );
      }
    }

    // Generate mouse click events if a mouse button has just been clicked
    for (int mouseButtonId : mouseButtonIds) {
      if (Input.isButtonClicked(mouseButtonId)) {
        eventStore.mouseClickEvents.add(
          new MouseEvent(
            MouseEvent.Type.CLICK,
            mousePosition.x, mousePosition.y,
            mouseButtonId
          )
        );
      }
    }

    // Generate a mouse exit event if widgets exist in previousHitTestCheckList
    // but don't exist in currentHitTestCheckList. In essence, this means
    // there are widgets not under the mouse cursor in the current frame that
    // were in the previous frame
    for (Widget widget : previousHitTestList) {
      if (!currentHitTestList.contains(widget))
        eventStore.mouseExitEvent = new MouseEvent(
          MouseEvent.Type.EXIT,
          mousePosition.x, mousePosition.y,
          Input.BUTTON_NONE
        );
    }

    // If mouse down events have occurred, activeWidget is the widget currently
    // under the mouse cursor, which is also focusWidget
    if (!eventStore.mouseDownEvents.isEmpty())
      activeWidget = focusWidget;

    this.mousePosition.set(mousePosition);
  }

  /** Dispatches generated {@link InputEvent} to widgets */
  private void dispatchInputEvents() {
    // Dispatch mouse enter events
    if (eventStore.mouseEnterEvent != null) {
      for (Widget widget : currentHitTestList) {
        if (!previousHitTestList.contains(widget) &&
          widget instanceof InputEventHandler eventHandler &&
          !eventStore.mouseEnterEvent.isHandled())
          eventHandler.handleEvent(eventStore.mouseEnterEvent);
      }
    }

    // Dispatch mouse motion events
    if (focusWidget != null && eventStore.mouseMotionEvent != null) {
      traverseTree(focusWidget, TraverseDirection.UP, widget -> {
        if (widget instanceof InputEventHandler eventHandler &&
          !eventStore.mouseMotionEvent.isHandled())
          eventHandler.handleEvent(eventStore.mouseMotionEvent);
      });
    }

    // Dispatch mouse exit events
    if (eventStore.mouseExitEvent != null) {
      for (Widget widget : previousHitTestList) {
        if (!currentHitTestList.contains(widget) &&
          widget instanceof InputEventHandler eventHandler &&
          !eventStore.mouseExitEvent.isHandled())
          eventHandler.handleEvent(eventStore.mouseExitEvent);
      }
    }

    //    inputEvents.clear();
    //    widgetHitTestQueue.clear();
    //    widgetDispatchStack.clear();
    //    widgetHitList.clear();
    //
    //    Position mousePosition = Input.getMousePosition();

    //    // Determine which widgets to dispatch events to with a hit test
    //    widgetHitTestQueue.add(tree);
    //
    //    while (!widgetHitTestQueue.isEmpty()) {
    //      Widget widget = widgetHitTestQueue.remove(0);
    //
    //      if (widget instanceof InputEventHandler eventHandler &&
    //        widget.hitTest(mousePosition.x, mousePosition.y)) {
    //        widgetDispatchStack.push(eventHandler);
    //        widgetHitList.add(eventHandler);
    //      }
    //
    //      if (widget instanceof SingleChildParent parent) {
    //        widgetHitTestQueue.add(parent.getChild());
    //      } else if (widget instanceof MultiChildParent parent) {
    //        widgetHitTestQueue.addAll(parent.getChildren());
    //      }
    //    }
    //
    //    // Dispatch mouse enter event based on the difference between the
    //    // widgetHitList and prevWidgetHitList
    //    MouseEvent mouseEnterEvent = new MouseEvent(
    //      MouseEvent.Type.ENTER,
    //      mousePosition.x, mousePosition.y,
    //      Input.BUTTON_NONE
    //    );
    //
    //    for (InputEventHandler eventHandler : widgetHitList) {
    //      if (!prevWidgetHitList.contains(eventHandler))
    //        eventHandler.handleEvent(mouseEnterEvent);
    //    }
    //
    //    // Generate mouse motion input events
    //    generateMouseMotionEvents(
    //      inputEvents,
    //      this.mousePosition,
    //      mousePosition
    //    );
    //
    //    // Generate mouse down events
    //    generateMouseActionEvents(
    //      inputEvents,
    //      Input::isButtonDown,
    //      MouseEvent.Type.DOWN
    //    );
    //
    //    // Generate mouse hold events
    //    generateMouseActionEvents(
    //      inputEvents,
    //      Input::isButtonPressed,
    //      MouseEvent.Type.PRESS
    //    );
    //
    //    // Generate mouse release events
    //    generateMouseActionEvents(
    //      inputEvents,
    //      Input::isButtonReleased,
    //      MouseEvent.Type.RELEASE
    //    );
    //
    //    // Generate mouse click events
    //    generateMouseActionEvents(
    //      inputEvents,
    //      Input::isButtonClicked,
    //      MouseEvent.Type.CLICK
    //    );
    //
    //    // Dispatch mouse press, hold, release, click and motion events to widgets
    //    // which pass the hit test
    //    if (!inputEvents.isEmpty()) {
    //      if (!widgetDispatchStack.isEmpty()) {
    //        while (!widgetDispatchStack.isEmpty()) {
    //          InputEventHandler widget = widgetDispatchStack.pop();
    //
    //          for (InputEvent ev : inputEvents) {
    //            if (!ev.isHandled())
    //              widget.handleEvent(ev);
    //          }
    //        }
    //      }
    //    }
    //
    //    // Dispatch mouse exit event based on the difference between the
    //    // widgetHitList and prevWidgetHitList
    //    MouseEvent mouseExitEvent = new MouseEvent(
    //      MouseEvent.Type.EXIT,
    //      mousePosition.x, mousePosition.y,
    //      Input.BUTTON_NONE
    //    );
    //
    //    for (InputEventHandler widget : prevWidgetHitList) {
    //      if (!widgetHitList.contains(widget))
    //        widget.handleEvent(mouseExitEvent);
    //    }
    //
    //    prevWidgetHitList.clear();
    //    prevWidgetHitList.addAll(widgetHitList);

    previousHitTestList.clear();
    previousHitTestList.addAll(currentHitTestList);
  }

  private void generateMouseActionEvents(
    List<InputEvent> inputEvents,
    ButtonConditionChecker checker,
    MouseEvent.Type eventType
  ) {
    int buttonAction = -1;

    if (checker.check(Input.BUTTON_LMB))
      buttonAction = Input.BUTTON_LMB;
    else if (checker.check(Input.BUTTON_RMB))
      buttonAction = Input.BUTTON_RMB;
    else if (checker.check(Input.BUTTON_MMB))
      buttonAction = Input.BUTTON_RMB;

    if (buttonAction != -1) {
      inputEvents.add(
        new MouseEvent(
          eventType,
          mousePosition.x,
          mousePosition.y,
          buttonAction
        )
      );
    }
  }

  private void generateMouseMotionEvents(
    List<InputEvent> inputEvents,
    Position previousMousePosition,
    Position currentMousePosition
  ) {
    if (!Objects.equals(previousMousePosition, currentMousePosition)) {
      inputEvents.add(
        new MouseEvent(
          MouseEvent.Type.MOTION,
          mousePosition.x, mousePosition.y,
          Input.BUTTON_NONE
        )
      );
    }
  }

  private void traverseTree(
    Widget tree,
    TraverseDirection direction,
    WidgetTreeVisitor visitor
  ) {
    visitor.visit(tree);

    switch (direction) {
      case UP -> {
        Widget parent = tree.getParent();

        if (parent == null)
          return;

        traverseTree(parent, direction, visitor);
      }
      case DOWN -> {
        if (tree instanceof SingleChildParent parent) {
          traverseTree(parent.getChild(), direction, visitor);
        } else if (tree instanceof MultiChildParent parent) {
          List<Widget> children = parent.getChildren();

          for (Widget child : children)
            traverseTree(child, direction, visitor);
        }
      }
    }
  }

  private enum TraverseDirection {
    UP, DOWN
  }

  public interface WidgetTreeCreator {
    /** Called to create a widget tree */
    Widget onCreateUI();
  }

  public interface WidgetTreeUpdater {
    void onUpdate();
  }

  private interface WidgetTreeVisitor {
    void visit(Widget widget);
  }

  private interface ButtonConditionChecker {
    boolean check(int button);
  }
}
