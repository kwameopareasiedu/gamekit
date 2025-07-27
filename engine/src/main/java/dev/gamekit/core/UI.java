package dev.gamekit.core;

import dev.gamekit.settings.Settings;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.events.*;
import dev.gamekit.ui.widgets.MultiChildParent;
import dev.gamekit.ui.widgets.Parent;
import dev.gamekit.ui.widgets.SingleChildParent;
import dev.gamekit.ui.widgets.Widget;
import dev.gamekit.utils.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** {@link UI} manages the user interface within a {@link Scene} */
public final class UI {
  private static final int MAX_RENDERS_PER_TRIGGER = 12;
  private static final Logger LOGGER = LogManager.getLogger(UI.class);
  private static UI instance;

  private final Scene scene;
  private final Constraints windowConstraints;
  private final List<Widget> currentHitTestList;
  private final List<Widget> previousHitTestList;
  private final InputEventStore eventStore;
  private final Position mousePosition;
  private final BufferedImage canvasImage;
  private final Graphics2D canvasGraphics;
  private Widget tree;
  private Widget hoverWidget;
  private Widget activeWidget;
  private Widget lastActiveWidget;
  private boolean needsLayout = false;
  private int renderCount;

  /** Return the {@link FontMetrics} for a given font */
  public static FontMetrics getFontMetrics(Font font) {
    return Window.getInstance().getUiGraphics().getFontMetrics(font);
  }

  public static UI getInstance() {
    return instance;
  }

  public UI(Scene scene) {
    Settings settings = Application.getInstance().getSettings();
    Window.Info windowInfo = Window.getInfo();
    int dw = windowInfo.displayWidth();
    int dh = windowInfo.displayHeight();

    this.scene = scene;
    this.windowConstraints = new Constraints(dw, dw, dh, dh);
    this.currentHitTestList = new ArrayList<>();
    this.previousHitTestList = new ArrayList<>();
    this.eventStore = new InputEventStore();
    this.mousePosition = new Position();
    this.canvasImage = new BufferedImage(dw, dh, BufferedImage.TYPE_INT_ARGB);
    this.canvasGraphics = canvasImage.createGraphics();
    this.renderCount = MAX_RENDERS_PER_TRIGGER;

    settings.antialiasing.apply(canvasGraphics);
    settings.textAntialiasing.apply(canvasGraphics);
    settings.alphaInterpolation.apply(canvasGraphics);
    settings.imageInterpolation.apply(canvasGraphics);
    settings.renderingStrategy.apply(canvasGraphics);
    settings.dithering.apply(canvasGraphics);

    UI.instance = this;
  }

  public void triggerRender() {
    renderCount = MAX_RENDERS_PER_TRIGGER;
  }

  void triggerUpdate() {
    needsLayout = true;
  }

  void clear() {
    drawTree();
  }

  /** Set the initial widget tree */
  void setWidgetTree(Widget tree) {
    this.tree = tree;

    if (this.tree != null) {
      this.tree.mounted();
      this.tree.layout(windowConstraints);
      this.tree.postLayout();
      triggerRender();
    }
  }

  /**
   * Called to update updates the UI state. This involves recomputing layout, generating input
   * events and dispatching them
   */
  void update() {
    if (tree != null && needsLayout) {
      LOGGER.debug("Laying out UI");
      updateTree();
    }

    generateInputEvents();
    dispatchInputEvents();
  }

  /** Called to draw the {@link Widget} tree to the {@link Window} UI layer */
  void draw() {
    if (tree != null && renderCount > 0) {
      LOGGER.debug("Rendering UI");
      renderCount--;
      drawTree();
    }
  }

  /**
   * Updates the widget tree using a "diffing" algorithm
   * <p>
   * This "diffing" algorithm involves generating a new widget tree with the new state,
   * comparing it to the current widget tree and updating widgets whose states have changed.
   */
  private void updateTree() {
    List<Widget> currentWidgetQueue = new ArrayList<>();
    List<Widget> newWidgetQueue = new ArrayList<>();
    Widget newTree = scene.createUI();
    boolean treeUpdated = false;

    currentWidgetQueue.add(tree);
    newWidgetQueue.add(newTree);
    newTree.mounted();

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

        if (treeWidget == hoverWidget) hoverWidget = newWidget;
        if (treeWidget == activeWidget) activeWidget = newWidget;
        if (treeWidget == lastActiveWidget) lastActiveWidget = newWidget;
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

    if (treeUpdated) {
      tree.layout(windowConstraints);
      tree.postLayout();
      triggerRender();
    }

    currentWidgetQueue.clear();
    newWidgetQueue.clear();
    needsLayout = false;
  }

  /** Monitors {@link Input} and generates events for input actions */
  private void generateInputEvents() {
    if (tree == null)
      return;

    eventStore.clear();
    currentHitTestList.clear();

    Position mousePosition = Input.getMousePosition();

    traverseTree(tree, TraverseDirection.IN, widget -> {
      if (widget instanceof InputEventHandler &&
        widget.hitTest(mousePosition.x, mousePosition.y)) {
        currentHitTestList.add(widget);
      }
    });

    // Assign the last hit test widget as the hover widget
    hoverWidget = !currentHitTestList.isEmpty()
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

    // Generate a mouse enter event if widgets exist in currentHitTestCheckList but don't exist
    // in previousHitTestCheckList. In essence, this means there are new widgets under the mouse
    // cursor in the current frame that were not in the previous frame
    for (Widget widget : currentHitTestList) {
      if (!previousHitTestList.contains(widget)) {
        eventStore.mouseEnterEvent = new MouseEvent(
          MouseEvent.Type.ENTER,
          mousePosition.x, mousePosition.y,
          Input.BUTTON_NONE
        );

        break;
      }
    }

    // Generate mouse down event if LMB has just been pressed
    if (Input.isButtonDown(Input.BUTTON_LMB)) {
      eventStore.mouseDownEvent = new MouseEvent(
        MouseEvent.Type.DOWN,
        mousePosition.x, mousePosition.y,
        Input.BUTTON_LMB
      );

      // Since the mouse is down, the hover widget becomes the active widget until the mouse is
      // released (i.e. activeWidget == null)
      if (activeWidget == null)
        activeWidget = hoverWidget;
    }

    // Generate mouse press events if LMB is being pressed
    if (Input.isButtonPressed(Input.BUTTON_LMB)) {
      eventStore.mousePressEvent = new MouseEvent(
        MouseEvent.Type.PRESS,
        mousePosition.x, mousePosition.y,
        Input.BUTTON_LMB
      );
    }

    // Generate a mouse release event if LMB has been released
    if (Input.isButtonReleased(Input.BUTTON_LMB)) {
      eventStore.mouseReleaseEvent = new MouseEvent(
        MouseEvent.Type.RELEASE,
        mousePosition.x, mousePosition.y,
        Input.BUTTON_LMB
      );

      // Generate a mouse click event if LMB was released on the active widget
      if (activeWidget == hoverWidget) {
        eventStore.mouseClickEvent = new MouseEvent(
          MouseEvent.Type.CLICK,
          mousePosition.x, mousePosition.y,
          Input.BUTTON_LMB
        );
      }

      // Since the mouse is released, there is no active widget, but we do need a reference to
      // the last active widget for event dispatch purposes
      lastActiveWidget = activeWidget;
      activeWidget = null;
    }

    // Generate a mouse exit event if widgets exist in previousHitTestCheckList but don't exist
    // in currentHitTestCheckList. In essence, this means there are widgets not under the mouse
    // cursor in the current frame that were in the previous frame
    for (Widget widget : previousHitTestList) {
      if (!currentHitTestList.contains(widget)) {
        eventStore.mouseExitEvent = new MouseEvent(
          MouseEvent.Type.EXIT,
          mousePosition.x, mousePosition.y,
          Input.BUTTON_NONE
        );

        break;
      }
    }

    this.mousePosition.set(mousePosition);

    // Generate a key char event if a key has been pressed
    if (Input.getPressedCharacter() != 0)
      eventStore.keyCharEvent = new KeyCharEvent(
        Input.getPressedCharacter()
      );
  }

  /** Dispatches generated {@link InputEvent} to widgets */
  private void dispatchInputEvents() {
    // Dispatch mouse motion events to widgets under mouse
    if (eventStore.mouseMotionEvent != null) {
      for (Widget widget : currentHitTestList) {
        if (widget instanceof InputEventHandler eventHandler &&
          !eventStore.mouseMotionEvent.isHandled())
          eventHandler.handleEvent(eventStore.mouseMotionEvent);
      }
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
      traverseTree(activeWidget, TraverseDirection.OUT, widget -> {
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
      traverseTree(lastActiveWidget, TraverseDirection.OUT, widget -> {
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

  /** Draws the widget tree to the {@link Window} UI buffer */
  private void drawTree() {
    Window win = Window.getInstance();
    Window.Info windowInfo = Window.getInfo();
    Graphics2D uiGraphics = win.getUiGraphics();
    int displayWidth = windowInfo.displayWidth();
    int displayHeight = windowInfo.displayHeight();

    canvasGraphics.setBackground(Constants.TRANSPARENT_COLOR);
    canvasGraphics.clearRect(0, 0, displayWidth, displayHeight);

    if (tree != null)
      tree.render(canvasGraphics);

    uiGraphics.setBackground(Constants.TRANSPARENT_COLOR);
    uiGraphics.clearRect(0, 0, displayWidth, displayHeight);
    uiGraphics.drawImage(canvasImage, 0, 0, displayWidth, displayHeight, null);

    renderCount--;
  }

  private void traverseTree(
    Widget tree,
    TraverseDirection direction,
    TreeWidgetVisitor visitor
  ) {
    visitor.visit(tree);

    switch (direction) {
      case OUT -> {
        Widget parent = tree.getParent();

        if (parent == null)
          return;

        traverseTree(parent, direction, visitor);
      }
      case IN -> {
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
    OUT, IN
  }

  private interface TreeWidgetVisitor {
    void visit(Widget widget);
  }
}
