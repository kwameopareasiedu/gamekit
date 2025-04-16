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
  private Widget tree;
  private Widget focusWidget;
  private Widget activeWidget;
  private Widget lastActiveWidget;
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

    // Generate mouse down event if button LMB has just been pressed
    if (Input.isButtonDown(Input.BUTTON_LMB)) {
      eventStore.mouseDownEvent = new MouseEvent(
        MouseEvent.Type.DOWN,
        mousePosition.x, mousePosition.y,
        Input.BUTTON_LMB
      );

      if (activeWidget == null)
        activeWidget = focusWidget;
    }

    // Generate mouse press events if button LMB is being pressed
    if (Input.isButtonPressed(Input.BUTTON_LMB)) {
      eventStore.mousePressEvent = new MouseEvent(
        MouseEvent.Type.PRESS,
        mousePosition.x, mousePosition.y,
        Input.BUTTON_LMB
      );
    }

    // Generate mouse release and click events if button LMB has been released
    if (Input.isButtonReleased(Input.BUTTON_LMB)) {
      if (activeWidget == focusWidget) {
        eventStore.mouseReleaseEvent = new MouseEvent(
          MouseEvent.Type.RELEASE,
          mousePosition.x, mousePosition.y,
          Input.BUTTON_LMB
        );

        eventStore.mouseClickEvent = new MouseEvent(
          MouseEvent.Type.CLICK,
          mousePosition.x, mousePosition.y,
          Input.BUTTON_LMB
        );
      }

      lastActiveWidget = activeWidget;
      activeWidget = null;
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

    this.mousePosition.set(mousePosition);
  }

  /** Dispatches generated {@link InputEvent} to widgets */
  private void dispatchInputEvents() {
    // Dispatch mouse motion events
    if (focusWidget != null && eventStore.mouseMotionEvent != null) {
      traverseTree(focusWidget, TraverseDirection.UP, widget -> {
        if (widget instanceof InputEventHandler eventHandler &&
          !eventStore.mouseMotionEvent.isHandled())
          eventHandler.handleEvent(eventStore.mouseMotionEvent);
      });
    }

    // Dispatch mouse enter events
    if (eventStore.mouseEnterEvent != null) {
      for (Widget widget : currentHitTestList) {
        if (activeWidget == null || widget == activeWidget) {
          if (!previousHitTestList.contains(widget) &&
            widget instanceof InputEventHandler eventHandler &&
            !eventStore.mouseEnterEvent.isHandled())
            eventHandler.handleEvent(eventStore.mouseEnterEvent);
        }
      }
    }

    // Dispatch mouse down and press events to the active widget
    if (activeWidget != null) {
      traverseTree(activeWidget, TraverseDirection.UP, widget -> {
        if (widget instanceof InputEventHandler eventHandler) {
          if (eventStore.mouseDownEvent != null &&
            !eventStore.mouseDownEvent.isHandled())
            eventHandler.handleEvent(eventStore.mouseDownEvent);

          if (eventStore.mousePressEvent != null &&
            !eventStore.mousePressEvent.isHandled())
            eventHandler.handleEvent(eventStore.mousePressEvent);
        }
      });
    }

    // Dispatch mouse release and click events to the active widget
    if (lastActiveWidget != null) {
      traverseTree(lastActiveWidget, TraverseDirection.UP, widget -> {
        if (widget instanceof InputEventHandler eventHandler) {
          if (eventStore.mouseReleaseEvent != null &&
            !eventStore.mouseReleaseEvent.isHandled())
            eventHandler.handleEvent(eventStore.mouseReleaseEvent);

          if (eventStore.mouseClickEvent != null &&
            !eventStore.mouseClickEvent.isHandled())
            eventHandler.handleEvent(eventStore.mouseClickEvent);
        }
      });
    }

    // Dispatch mouse exit events
    if (eventStore.mouseExitEvent != null) {
      for (Widget widget : previousHitTestList) {
        if (activeWidget == null || widget == activeWidget) {
          if (!currentHitTestList.contains(widget) &&
            widget instanceof InputEventHandler eventHandler &&
            !eventStore.mouseExitEvent.isHandled())
            eventHandler.handleEvent(eventStore.mouseExitEvent);
        }
      }
    }

    previousHitTestList.clear();
    previousHitTestList.addAll(currentHitTestList);
    lastActiveWidget = null;
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
}
