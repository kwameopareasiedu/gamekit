package dev.gamekit.core;

import dev.gamekit.settings.Settings;
import dev.gamekit.ui.events.*;
import dev.gamekit.ui.widgets.Widget;
import dev.gamekit.utils.Constraints;
import dev.gamekit.utils.Position;
import dev.gamekit.utils.ValueGetter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** {@link UI} manages the user interface within a {@link Scene} */
public final class UI implements Widget.Host, Widget.Updater, Widget.Traveller {
  public static final Color TRANSPARENT_COLOR = new Color(0x0000000, true);

  private static final Logger LOGGER = LogManager.getLogger(UI.class);

  private final ValueGetter<Widget> createUI;
  private final Constraints windowConstraints;
  private final List<Widget> currentHitTestList;
  private final List<Widget> previousHitTestList;
  private final EventStore eventStore;
  private final Position mousePosition;
  private final BufferedImage canvasImage;
  private final Graphics2D canvasGraphics;
  private Widget tree;
  private Widget focusWidget;
  private Widget activeWidget;
  private Widget lastFocusWidget;
  private Widget lastActiveWidget;
  private boolean needsUpdate = false;
  private boolean needsRender = false;
  private boolean needsDraw = false;

  public UI(ValueGetter<Widget> createUI) {
    Settings settings = Application.getInstance().getSettings();
    Window window = Window.getInstance();
    int displayWidth = window.getDisplayWidth();
    int displayHeight = window.getDisplayHeight();

    this.createUI = createUI;
    this.windowConstraints = new Constraints(displayWidth, displayWidth, displayHeight, displayHeight);
    this.currentHitTestList = new ArrayList<>();
    this.previousHitTestList = new ArrayList<>();
    this.eventStore = new EventStore();
    this.mousePosition = new Position();
    this.canvasImage = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_ARGB);
    this.canvasGraphics = canvasImage.createGraphics();

    settings.antialiasing.apply(canvasGraphics);
    settings.textAntialiasing.apply(canvasGraphics);
    settings.alphaInterpolation.apply(canvasGraphics);
    settings.imageInterpolation.apply(canvasGraphics);
    settings.renderingStrategy.apply(canvasGraphics);
    settings.dithering.apply(canvasGraphics);
  }

  /** Clears the UI buffer with transparent pixels */
  public void clear() {
    Window win = Window.getInstance();
    Graphics2D uiGraphics = win.getUiGraphics();
    int displayWidth = win.getDisplayWidth();
    int displayHeight = win.getDisplayHeight();

    uiGraphics.setBackground(TRANSPARENT_COLOR);
    uiGraphics.clearRect(0, 0, displayWidth, displayHeight);
  }

  @Override
  public FontMetrics getFontMetrics(Font font) {
    return Window.getInstance().getUiGraphics().getFontMetrics(font);
  }

  /** Triggers a layout update during the next frame */
  @Override
  public void triggerUpdate() {
    needsUpdate = true;
  }

  @Override
  public void triggerRender() {
    needsRender = true;
  }

  /** Set the initial widget tree */
  void setWidgetTree(Widget tree) {
    this.tree = tree;

    if (this.tree != null) {
      this.tree.init(this);
      this.tree.mount();
      this.tree.layout(windowConstraints);
      this.tree.postLayout();
      triggerRender();
    }
  }

  /** Update updates the UI tree, recomputes layout, generates and dispatches input events */
  void update() {
    if (tree != null && needsUpdate) {
      LOGGER.debug("Updating UI");
      updateTree(this, windowConstraints, this::getTree, createUI, this::setTree, this::triggerRender);
      needsUpdate = false;
    }

    generateInputEvents();
    dispatchInputEvents();
  }

  /** Renders the UI tree to the canvas which is drawn to the {@link Window} by the draw thread at a later time */
  void render() {
    if (tree != null && needsRender && !needsDraw) {
      LOGGER.debug("Rendering UI");

      canvasGraphics.setBackground(TRANSPARENT_COLOR);
      canvasGraphics.clearRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
      tree.render(canvasGraphics);

      needsRender = false;
      needsDraw = true;
    }
  }

  /** Called by the render thread to draw the canvas to the {@link Window} UI layer */
  void draw() {
    if (needsDraw) {
      Window win = Window.getInstance();
      Graphics2D uiGraphics = win.getUiGraphics();
      int displayWidth = win.getDisplayWidth();
      int displayHeight = win.getDisplayHeight();

      uiGraphics.setBackground(TRANSPARENT_COLOR);
      uiGraphics.clearRect(0, 0, displayWidth, displayHeight);
      uiGraphics.drawImage(canvasImage, 0, 0, displayWidth, displayHeight, null);

      needsDraw = false;
    }
  }

  /** Called to unmount the {@link Widget} tree before being disposed */
  void unmount() {
    if (tree != null)
      tree.unmount();

    clear();
  }

  /** Monitors {@link Input} and generates events for input actions */
  private void generateInputEvents() {
    if (tree == null) return;

    eventStore.clear();
    currentHitTestList.clear();

    Position mousePosition = Input.getMousePosition();

    travelTree(tree, Direction.INWARD, widget -> {
      if (widget instanceof InputEvent.Handler && widget.hitTest(mousePosition.x, mousePosition.y)) {
        currentHitTestList.add(widget);
      }
    });

    // Assign the last hit test widget as the hover widget
    Widget hoverWidget = !currentHitTestList.isEmpty() ? currentHitTestList.get(currentHitTestList.size() - 1) : null;

    // Generate mouse motion events if the mouse position has changed
    if (!Objects.equals(this.mousePosition, mousePosition)) {
      eventStore.mouseMotionEvent = new MouseEvent(
        MouseEvent.Type.MOTION,
        mousePosition.x, mousePosition.y,
        Input.BUTTON_NONE
      );
    }

    // Generate a mouse enter event if widgets exist in currentHitTestCheckList but doesn't exist in
    // previousHitTestCheckList. In essence, this means there are new widgets under the mouse cursor in the current
    // frame that were not in the previous frame
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

      // If the hover widget is not the focus widget, a new widget has the focus now, so generate a blur event for
      // the last focused widget
      if (focusWidget != hoverWidget) {
        lastFocusWidget = focusWidget;
        eventStore.blurEvent = new FocusEvent(FocusEvent.Type.BLUR);
      }

      // Since the mouse is down, the hover widget becomes the focus widget
      focusWidget = hoverWidget;

      // Also generate a focus event for the newly focused widget
      eventStore.focusEvent = new FocusEvent(FocusEvent.Type.FOCUS);

      // If no widget is currently being activated, the hover widget also becomes the active widget until the mouse
      // is released
      if (activeWidget == null) activeWidget = hoverWidget;
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

      // Since the mouse is released, there is no active widget, but we do need a reference to the last active widget
      // for event dispatch purposes
      lastActiveWidget = activeWidget;
      activeWidget = null;
    }

    // Generate a mouse exit event if widgets exist in previousHitTestCheckList but don't exist in
    // currentHitTestCheckList. In essence, this means there are widgets not under the mouse cursor in the current
    // frame that were in the previous frame
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

    // Generate a key char event if a character key has been pressed
    if (Input.getPressedCharacter() != 0) {
      eventStore.keyCharEvent = new KeyCharEvent(Input.getPressedCharacter());
    }

    // Generate a key code event if an action key has been pressed
    if (Input.getPressedKeyCode() != 0) {
      eventStore.keyCodeEvent = new KeyCodeEvent(Input.getPressedKeyCode());
    }

    this.mousePosition.set(mousePosition);
  }

  /** Dispatches generated {@link InputEvent} to widgets */
  private void dispatchInputEvents() {
    // Dispatch mouse motion event to widgets under mouse
    if (eventStore.mouseMotionEvent != null) {
      for (Widget widget : currentHitTestList) {
        if (widget instanceof MouseEvent.Handler eventHandler && !eventStore.mouseMotionEvent.isHandled())
          eventHandler.handleEvent(eventStore.mouseMotionEvent);
      }
    }

    // Dispatch mouse enter event
    if (eventStore.mouseEnterEvent != null) {
      for (Widget widget : currentHitTestList) {
        if (activeWidget == null || widget == activeWidget) {
          if (!previousHitTestList.contains(widget) && widget instanceof MouseEvent.Handler eventHandler
            && !eventStore.mouseEnterEvent.isHandled())
            eventHandler.handleEvent(eventStore.mouseEnterEvent);
        }
      }
    }

    // Dispatch mouse down and press events to the active widget
    if (activeWidget != null) {
      travelTree(activeWidget, Direction.OUTWARD, widget -> {
        if (widget instanceof MouseEvent.Handler eventHandler) {
          if (eventStore.mouseDownEvent != null && !eventStore.mouseDownEvent.isHandled())
            eventHandler.handleEvent(eventStore.mouseDownEvent);

          if (eventStore.mousePressEvent != null && !eventStore.mousePressEvent.isHandled())
            eventHandler.handleEvent(eventStore.mousePressEvent);
        }
      });
    }

    // Dispatch mouse release and click events to the active widget
    if (lastActiveWidget != null) {
      travelTree(lastActiveWidget, Direction.OUTWARD, widget -> {
        if (widget instanceof MouseEvent.Handler eventHandler) {
          if (eventStore.mouseReleaseEvent != null && !eventStore.mouseReleaseEvent.isHandled())
            eventHandler.handleEvent(eventStore.mouseReleaseEvent);

          if (eventStore.mouseClickEvent != null && !eventStore.mouseClickEvent.isHandled())
            eventHandler.handleEvent(eventStore.mouseClickEvent);
        }
      });
    }

    // Dispatch mouse exit event
    if (eventStore.mouseExitEvent != null) {
      for (Widget widget : previousHitTestList) {
        if (activeWidget == null || widget == activeWidget) {
          if (!currentHitTestList.contains(widget) && widget instanceof MouseEvent.Handler eventHandler
            && !eventStore.mouseExitEvent.isHandled())
            eventHandler.handleEvent(eventStore.mouseExitEvent);
        }
      }
    }

    // Dispatch focus event
    if (eventStore.focusEvent != null) {
      if (focusWidget != null && !eventStore.focusEvent.isHandled()
        && focusWidget instanceof FocusEvent.Handler eventHandler)
        eventHandler.handleEvent(eventStore.focusEvent);
    }

    // Dispatch blur event
    if (eventStore.blurEvent != null) {
      if (lastFocusWidget != null && !eventStore.blurEvent.isHandled()
        && lastFocusWidget instanceof FocusEvent.Handler eventHandler)
        eventHandler.handleEvent(eventStore.blurEvent);
    }

    // Dispatch key char event
    if (eventStore.keyCharEvent != null) {
      if (focusWidget != null && !eventStore.keyCharEvent.isHandled()
        && focusWidget instanceof KeyCharEvent.Handler eventHandler)
        eventHandler.handleEvent(eventStore.keyCharEvent);
    }

    // Dispatch key code event
    if (eventStore.keyCodeEvent != null) {
      if (focusWidget != null && !eventStore.keyCodeEvent.isHandled()
        && focusWidget instanceof KeyCodeEvent.Handler eventHandler)
        eventHandler.handleEvent(eventStore.keyCodeEvent);
    }

    previousHitTestList.clear();
    previousHitTestList.addAll(currentHitTestList);
    lastActiveWidget = null;
    lastFocusWidget = null;
  }

  private Widget getTree() {
    return tree;
  }

  private void setTree(Widget tree) {
    this.tree = tree;
  }

  /** Convenience class which stores structures of {@link InputEvent} */
  private static class EventStore {
    public MouseEvent mouseMotionEvent;
    public MouseEvent mouseEnterEvent;
    public MouseEvent mouseDownEvent;
    public MouseEvent mousePressEvent;
    public MouseEvent mouseReleaseEvent;
    public MouseEvent mouseClickEvent;
    public MouseEvent mouseExitEvent;
    public FocusEvent focusEvent;
    public FocusEvent blurEvent;
    public KeyCharEvent keyCharEvent;
    public KeyCodeEvent keyCodeEvent;

    public void clear() {
      mouseMotionEvent = null;
      mouseEnterEvent = null;
      mouseDownEvent = null;
      mousePressEvent = null;
      mouseReleaseEvent = null;
      mouseClickEvent = null;
      mouseExitEvent = null;
      focusEvent = null;
      blurEvent = null;
      keyCharEvent = null;
      keyCodeEvent = null;
    }
  }
}
