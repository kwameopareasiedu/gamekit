package dev.gamekit.ui.widgets;

import dev.gamekit.core.Scene;
import dev.gamekit.core.UI;
import dev.gamekit.core.Window;
import dev.gamekit.utils.Bounds;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Size;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

/**
 * A widget is an abstract representation of a portion of a {@link Scene scene's} user interface.
 * <p>
 * Widgets are used to describe all aspects of a user interface, including physical aspects such as text and buttons
 * to layout effects like padding and alignment.
 * <p>
 * Widgets form a hierarchy based on composition. Each widget nests inside its parent and can receive context from
 * the parent.
 * <p>
 * To create a user interface in a scene, you override its {@code createUI} method and return the desired widget
 * hierarchy.
 * <p>
 * Widget layout is based on the <a href="https://docs.flutter.dev/ui/layout/constraints">box-constraint</a>
 * model which is used in Flutter, where constraints go down the tree, size go up and parents set positions
 */
public abstract class Widget {
  public static boolean DEBUG_DRAW = false;

  protected final Logger logger = LogManager.getLogger(getClass());
  protected final Bounds absoluteBounds;
  protected final Bounds computedBounds;
  protected final Size intrinsicSize;
  protected Constraints constraints;
  protected Config config;
  protected Widget parent;
  protected Host host;

  /** Creates a new widget with a list of configurations */
  public Widget(Config... configs) {
    if (configs == null)
      throw new IllegalArgumentException("Widget config cannot be null");

    this.config = configs[0].mergeWith(configs);
    this.absoluteBounds = new Bounds(0, 0, 0, 0);
    this.computedBounds = new Bounds(0, 0, 0, 0);
    this.intrinsicSize = new Size(0, 0);
    this.parent = null;
  }

  /** Returns the {@link #parent} of this widget */
  public Widget getParent() {
    return parent;
  }

  /** Checks if the {@link Config} object of this widget is equivalent to another {@link Widget} */
  public final boolean stateEquals(Widget widget) {
    return config.equals(widget.config);
  }

  /**
   * Called to initialize the widget after it has been inserted into the widget tree and
   * {@link #parent} has been set.
   * <p>
   * If the widget is updated some time after first initialization, this method is called
   * afterward to re-initialize the widget.
   * <p>
   * Since the {@link #config} has either been set in the constructor or updated via the {@link #updateState} method,
   * it is used to update this widget instance.
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performInit} method instead to perform any post-mount operations
   */
  public final void init(Host host) {
    this.host = host;
    config.updateWidget(this);
    performInit();
  }

  /** Delegate method for subclasses to perform additional initialization operations */
  protected void performInit() { /* No-op */ }

  /**
   * During UI updates, the engine checks which widgets need to be replaced or just updated by
   * comparing the types and states.
   * <p>
   * For widgets that only need state updates, this method is called with the updated widget
   * containing the new configuration.
   * <p>
   * It is guaranteed that the type of the incoming widget will be the same as this widget so it
   * is safe to cast the incoming widget to this widget's config class (E.g. the config class for
   * the {@code Image} widget will be the {@code ImageConfig}).
   * <p>
   * {@link #init} method is called afterward to re-initialize the widget.
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performUpdate} method instead to perform any state updates
   */
  public final void updateState(Widget widget) {
    this.config = widget.config;
    performUpdate(widget);
    init(host);
  }

  /** Delegate method for subclasses to perform additional state update operations */
  protected void performUpdate(Widget widget) { /* No-op */ }

  /**
   * Computes the size of the widget and the relative position(s) of its child/children
   * <p>
   * This is called by either the parent widget or window and receives the {@link Constraints}
   * from its parent or the window, and the resulting computed size <b>must</b> always respect
   * this constraint
   * <p>
   * The goal of this method is to set the {@link #computedBounds} which controls where on the
   * screen the widget is rendered
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performLayout} method instead to perform their layout
   */
  public final void layout(Constraints constraints) {
    this.constraints = constraints;
    performLayout(constraints);
  }

  /**
   * Delegate method for subclasses to perform the actual layout
   * <p>
   * It is passed the {@link Constraints} object which it <b>must</b> always respect
   */
  protected abstract void performLayout(Constraints constraints);

  /**
   * Performs post-layout logic
   * <p>
   * This exists because the engine calls {@link #layout} in a depth-first manner in traversing
   * this widget tree.
   * <p>
   * This means a widget's {@link #layout} method will not complete until its entire widget
   * subtree has completed and hence certain data (e.g. relative position and that of the parent)
   * will not be available until after the scene's UI widget tree has been completely laid out.
   * <p>
   * After complete scene layout, this method is called to perform computations that depend on
   * the data computed during {@link #layout}
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performPostLayout} method instead to perform any post layout logic
   */
  public final void postLayout() {
    // Compute absolute position by adding computed position of this widget and its ancestors
    double absoluteX = computedBounds.x;
    double absoluteY = computedBounds.y;
    Widget visitedParent = this.parent;

    while (visitedParent != null) {
      absoluteX += visitedParent.computedBounds.x;
      absoluteY += visitedParent.computedBounds.y;
      visitedParent = visitedParent.parent;
    }

    absoluteBounds.set(
      absoluteX, absoluteY,
      computedBounds.width,
      computedBounds.height
    );

    performPostLayout();
  }

  /** Delegate method for subclasses to perform additional post-layout operations */
  protected void performPostLayout() { /* No-op */ }

  /**
   * Renders the widget with the provided {@link Graphics2D} object
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performRender} method instead to perform rendering
   */
  public final void render(Graphics2D canvasGraphics) {
    performRender(canvasGraphics);

    if (DEBUG_DRAW) {
      Color originalColor = canvasGraphics.getColor();
      Stroke originalStroke = canvasGraphics.getStroke();

      canvasGraphics.setColor(UI.DEBUG_COLOR);
      canvasGraphics.setStroke(UI.DEBUG_STROKE);
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
   * Delegate method for subclasses to performs the actual rendering.
   * <p>
   * The {@link Graphics2D} object of the widget's {@code canvasImage} to perform drawing
   */
  protected abstract void performRender(Graphics2D g);

  /**
   * Called before the widget is removed from the widget tree during UI reconciliation or during
   * the scene's disposal.
   * <p>
   * This is a good place to clean up any resources and stop animations used by the widget.
   * <p>
   * Since this method is marked as {@code final}, subclasses should override the
   * {@link #performUnmount} method instead to perform rendering
   */
  public final void unmount() {
    performUnmount();
  }

  /** Delegate method for subclasses to perform additional unmount operations */
  protected void performUnmount() { /* No-op */ }

  /** Determines if the point {@code (x,y)} falls within the absolute bounds of this widget */
  public boolean hitTest(double x, double y) {
    double absoluteRight = absoluteBounds.x + absoluteBounds.width;
    double absoluteBottom = absoluteBounds.y + absoluteBounds.height;
    return absoluteBounds.x <= x && x <= absoluteRight &&
      absoluteBounds.y <= y && y <= absoluteBottom;
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
  public interface Config {
    /** Updates matching widget state variables with its own variables */
    void updateWidget(Widget widget);

    /** Returns a new config which merges this and provided config objects */
    Config mergeWith(Config[] configs);
  }

  /**
   * Interface for the host containing a {@link Widget}, allowing widgets to invoke necessary
   * methods on it
   */
  public interface Host {

    /** Returns the font metrics for the given font from the {@link Window} object */
    FontMetrics getFontMetrics(Font font);

    /** Triggers a re-render of the {@link Widget widget} tree */
    void triggerRender();
  }
}
