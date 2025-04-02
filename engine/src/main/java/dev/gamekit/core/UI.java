package dev.gamekit.core;

import dev.gamekit.ui.Bounds;
import dev.gamekit.ui.Constraints;
import dev.gamekit.ui.events.*;
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

  private final WidgetTreeCreator treeCreator;
  private final Constraints windowConstraints;
  private final List<Widget> widgetHitTestQueue;
  private final List<Widget> widgetHitList;
  private final List<Widget> prevWidgetHitList;
  private final Stack<Widget> widgetNotifyStack;
  private final List<InputEvent> inputEvents;
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
    this.widgetHitTestQueue = new ArrayList<>();
    this.widgetHitList = new ArrayList<>();
    this.prevWidgetHitList = new ArrayList<>();
    this.widgetNotifyStack = new Stack<>();
    this.inputEvents = new ArrayList<>();
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
  public void update() {
    if (tree != null && needsUpdate)
      updateTree();

    generateAndDispatchInputEvents();
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
   * Monitors the {@link Input} class, generates events if actions of interest
   * have occurred and dispatches them to the widget tree, if set
   */
  private void generateAndDispatchInputEvents() {
    if (tree == null)
      return;

    inputEvents.clear();
    widgetHitTestQueue.clear();
    widgetNotifyStack.clear();
    widgetHitList.clear();

    Position currentMousePos = Input.getMousePosition();

    // Generate mouse click input events
    if (Input.isButtonJustReleased(Input.BUTTON_LMB))
      inputEvents.add(new MouseClickEvent(currentMousePos.x,
        currentMousePos.y, Input.BUTTON_LMB));
    else if (Input.isButtonJustReleased(Input.BUTTON_RMB))
      inputEvents.add(new MouseClickEvent(currentMousePos.x,
        currentMousePos.y, Input.BUTTON_RMB));
    else if (Input.isButtonJustReleased(Input.BUTTON_MMB))
      inputEvents.add(new MouseClickEvent(currentMousePos.x,
        currentMousePos.y, Input.BUTTON_MMB));

    // Generate mouse motion input events
    if (!Objects.equals(mousePosition, currentMousePos))
      inputEvents.add(new MouseMotionEvent(currentMousePos.x,
        currentMousePos.y));

    widgetHitTestQueue.add(tree);

    // Determine which widgets are under the cursor with a hit test
    while (!widgetHitTestQueue.isEmpty()) {
      Widget widget = widgetHitTestQueue.remove(0);

      if (widget.hitTest(currentMousePos.x, currentMousePos.y)) {
        widgetNotifyStack.push(widget);
        widgetHitList.add(widget);
      }

      if (widget instanceof SingleChildParent parent) {
        widgetHitTestQueue.add(parent.getChild());
      } else if (widget instanceof MultiChildParent parent) {
        widgetHitTestQueue.addAll(parent.getChildren());
      }
    }

    if (!inputEvents.isEmpty()) {
      // Dispatch the generated mouse click and mouse motion events to the
      // widgets which pass the hit test
      if (!widgetNotifyStack.isEmpty()) {
        while (!widgetNotifyStack.isEmpty()) {
          Widget widget = widgetNotifyStack.pop();

          for (InputEvent ev : inputEvents) {
            if (!ev.isHandled())
              widget.handleEvent(ev);
          }
        }
      }
    }

    // Dispatch mouse enter and mouse exit based on the difference
    // between the widgetHitList and prevWidgetHitList
    MouseEnterEvent mouseEnterEvent = new MouseEnterEvent(currentMousePos.x,
      currentMousePos.y);
    MouseExitEvent mouseExitEvent = new MouseExitEvent(currentMousePos.x,
      currentMousePos.y);

    for (Widget widget : widgetHitList) {
      if (!prevWidgetHitList.contains(widget))
        widget.handleEvent(mouseEnterEvent);
    }

    for (Widget widget : prevWidgetHitList) {
      if (!widgetHitList.contains(widget))
        widget.handleEvent(mouseExitEvent);
    }

    prevWidgetHitList.clear();
    prevWidgetHitList.addAll(widgetHitList);
    mousePosition.set(currentMousePos);
  }

  public interface WidgetTreeCreator {
    /** Called to create a widget tree */
    Widget onCreateUI();
  }

  public interface WidgetTreeUpdater {
    void onUpdate();
  }
}
