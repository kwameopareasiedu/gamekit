package dev.gamekit.ui.widgets;

import dev.gamekit.core.Scene;
import dev.gamekit.core.UI;
import dev.gamekit.ui.Constraints;
import dev.gamekit.utils.Bounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

/**
 * A widget is an abstract representation of a portion of a {@link Scene Scene's} user interface.
 * <p>
 * Subclasses must implement the {@link #performLayout} and {@link #performRender(Graphics2D)} to
 * compute their position and size
 * <p>
 * Widget layout is based on the
 * <a href="https://docs.flutter.dev/ui/layout/constraints">box-constraint</a>
 * model which is used in Flutter, where constraints go down the tree, size go up and parents set
 * positions
 */
public abstract class Widget {
  public static boolean DEBUG_DRAW = false;

  private static final BasicStroke DEBUG_OUTLINE_STROKE = new BasicStroke(
    1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
  );

  protected final Logger logger = LogManager.getLogger(getClass());
  protected final Bounds absoluteBounds;
  protected final Bounds computedBounds;
  protected final Bounds intrinsicBounds;
  protected final Bounds clipBounds;
  protected UI.BridgeObject uiBridge;
  protected Constraints constraints;
  protected Widget parent;
  protected WidgetConfig config;

  public Widget(WidgetConfig config) {
    if (config == null)
      throw new IllegalArgumentException("Widget config cannot be null");

    this.config = config;
    this.absoluteBounds = new Bounds(0, 0, 0, 0);
    this.computedBounds = new Bounds(0, 0, 0, 0);
    this.intrinsicBounds = new Bounds(0, 0, 0, 0);
    this.clipBounds = new Bounds(0, 0, 0, 0);
    this.parent = null;
  }

  /** Returns the {@link #computedBounds} of this widget */
  public Bounds getComputedBounds() {
    return computedBounds;
  }

  /** Returns the {@link #parent} of this widget */
  public Widget getParent() {
    return parent;
  }

  /** Sets the {@link #parent} of this widget */
  public void setParent(Widget parent) {
    this.parent = parent;
  }

  /** Delegate method which determines if this widget's state matches another {@code widget} */
  public abstract boolean stateEquals(Widget widget);

  /**
   * Called to initialize the widget after it has been inserted into the widget tree and
   * {@link #parent} has been set.
   * <p>
   * If the widget is updated some time after first initialization, this method is called
   * afterward to re-initialize the widget.
   * <p>
   * A common use-case is to look up ancestors for additional information (E.g. Theme look up)
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performInit()} method instead to perform any post-mount operations
   */
  public final void init(UI.BridgeObject uiBridge) {
    this.uiBridge = uiBridge;
    performInit();
  }

  /** Delegate method which performs initialization logic in subclasses */
  protected void performInit() { /* No-op */ }

  /**
   * During UI updates, the engine checks which widgets need to be replaced or just updated by
   * comparing the types and states.
   * <p>
   * For widgets that only need state updates, this method is called with the updated widget
   * containing the new state.
   * <p>
   * It is guaranteed that the type of the incoming widget will be the same as this widget so it
   * is safe to cast the incoming widget to this widget's class.
   * <p>
   * {@link #init(UI.BridgeObject)} method is called afterward to re-initialize the widget.
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performUpdate(Widget widget)} method instead to perform any state updates
   */
  public final void update(Widget widget) {
    this.config = widget.config;
    performUpdate(widget);
    init(uiBridge);
  }

  /** Delegate method performs the state update for this widget */
  protected void performUpdate(Widget widget) { /* No-op */ }

  /**
   * Computes the layout for the widget
   * <p>
   * This is called by either the parent widget or window and receives the {@link Constraints}
   * from its parent or the window, and the resulting computed size must always respect this
   * constraint
   * <p>
   * The goal of this method is to set the {@link #computedBounds} which controls where on the
   * screen the widget is rendered.
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performLayout} method instead to perform their layout
   */
  public final void layout(Constraints constraints) {
    this.constraints = constraints;
    performLayout(constraints);
  }

  /**
   * Delegate method which performs the actual layout and is passed the constraints from
   * {@link #layout(Constraints)}.
   */
  protected abstract void performLayout(Constraints constraints);

  /**
   * Performs post-layout logic
   * <p>
   * This exists because {@link #layout(Constraints)} uses a depth-first approach in traversing
   * this widget tree.
   * <p>
   * With this approach, certain data (e.g. computed bounds position and parent computed bounds)
   * may not be available until after the scene's UI widget tree has been completely laid out
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performPostLayout} method instead to perform any post layout logic
   */
  public final void postLayout() {
    computeAbsoluteBounds();
    performPostLayout();
  }

  protected void performPostLayout() { /* No-op */ }

  /**
   * Renders the widget with the provided {@link Graphics2D} object
   * <p>
   * This method is {@code final} and delegates the actual drawing to
   * {@link #performRender(Graphics2D)}.
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performRender(Graphics2D)} method instead to perform rendering
   */
  public final void render(Graphics2D canvasGraphics) {
    performRender(canvasGraphics);

    if (DEBUG_DRAW) {
      Color originalColor = canvasGraphics.getColor();
      Stroke originalStroke = canvasGraphics.getStroke();

      canvasGraphics.setColor(Color.CYAN);
      canvasGraphics.setStroke(DEBUG_OUTLINE_STROKE);
      canvasGraphics.drawRect(
        (int) absoluteBounds.x,
        (int) absoluteBounds.y,
        (int) absoluteBounds.width,
        (int) absoluteBounds.height
      );

      canvasGraphics.setColor(originalColor);
      canvasGraphics.setStroke(originalStroke);
    }
  }

  /**
   * Delegate method which performs the actual rendering and is passed a {@link Graphics2D}
   * object of the widget's {@code canvasImage}.
   */
  protected abstract void performRender(Graphics2D g);

  /** Determines if the point {@code (x,y)} falls within the absolute bounds of this widget */
  public boolean hitTest(double x, double y) {
    double absoluteRight = absoluteBounds.x + absoluteBounds.width;
    double absoluteBottom = absoluteBounds.y + absoluteBounds.height;
    return absoluteBounds.x <= x && x <= absoluteRight &&
      absoluteBounds.y <= y && y <= absoluteBottom;
  }

  /**
   * Computes the absolute bounds, starting with the computed size and walking up its ancestry,
   * to determine the absolute position
   */
  protected void computeAbsoluteBounds() {
    double absoluteX = computedBounds.x;
    double absoluteY = computedBounds.y;
    Widget parent = this.parent;

    while (parent != null) {
      absoluteX += parent.computedBounds.x;
      absoluteY += parent.computedBounds.y;
      parent = parent.parent;
    }

    absoluteBounds.set(
      absoluteX, absoluteY,
      computedBounds.width,
      computedBounds.height
    );
  }

  /**
   * Looks up the ancestor of this widget for a {@link Widget} of the specified {@code type} and
   * returns the first instance of said ancestor.
   * <p>
   * If no ancestor of the specified type is found, it returns {@code null}
   */
  public <T extends Widget> T getAncestorOfType(Class<T> type) {
    if (this.parent == null)
      return null;

    Widget parent = this.parent;

    while (parent != null) {
      if (type.isInstance(parent))
        //noinspection unchecked
        return (T) parent;

      parent = parent.parent;
    }

    return null;
  }

  /** Base class for all widget constructor configurations */
  public static abstract class WidgetConfig { }
}
