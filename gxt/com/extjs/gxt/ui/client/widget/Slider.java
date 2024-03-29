/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.aria.FocusFrame;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.tips.Tip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;

/**
 * Slider component.
 * 
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>BeforeChange</b> : SliderEvent(slider, oldValue, newValue)<br>
 * <div>Fires before the slider value is changed. Listeners can cancel the
 * action by calling {@link BaseEvent#setCancelled(boolean)}.</div>
 * <ul>
 * <li>slider : this</li>
 * <li>oldValue : the old value which the slider was previously</li>
 * <li>newValue : the new value which the slider is being changed to</li>
 * </ul>
 * </dd>
 * 
 * <dd><b>Change</b> : SliderEvent(slider, oldValue, newValue)<br>
 * <div>Fires when the slider value is changed.</div>
 * <ul>
 * <li>slider : this</li>
 * <li>newValue : the new value which the slider has been changed to</li>
 * </ul>
 * </dd>
 * 
 * </dl>
 */
public class Slider extends BoxComponent {

  class Thumb extends Component {
    public Thumb() {
      baseStyle = "x-slider-thumb";
    }

    @Override
    protected void afterRender() {
      super.afterRender();
      addStyleOnOver(getElement(), "x-slider-thumb-over");
    }

    @Override
    protected void onRender(Element target, int index) {
      setElement(DOM.createDiv(), target, index);

      super.onRender(target, index);

      sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
    }

  }

  private boolean clickToChange = true;
  private Draggable drag;
  private boolean draggable = true;
  private El endEl;
  private int halfThumb;
  private int increment = 10;
  protected El innerEl;
  private int maxValue = 100;
  private String message = "{0}";
  private int minValue = 0;
  private Thumb thumb;
  private Tip tip;
  private boolean useTip = true;
  private int value = 0;
  protected boolean vertical;
  protected El targetEl;

  /**
   * Creates a new slider.
   */
  public Slider() {

  }

  @Override
  public El getFocusEl() {
    return super.getFocusEl();
  }

  /**
   * Returns the increment.
   * 
   * @return the increment
   */
  public int getIncrement() {
    return increment;
  }

  /**
   * Returns the max value (defaults to 100).
   * 
   * @return the max value
   */
  public int getMaxValue() {
    return maxValue;
  }

  /**
   * Returns the tool tip message.
   * 
   * @return the tool tip message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns the minimum value (defaults to 0).
   * 
   * @return the minimum value
   */
  public int getMinValue() {
    return minValue;
  }

  /**
   * Returns the current value.
   * 
   * @return the current value
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns whether whether or not clicking on the Slider axis will change the
   * slider.
   * 
   * @return true to allow axis clicks
   */
  public boolean isClickToChange() {
    return clickToChange;
  }

  /**
   * Returns true if the slider is draggable.
   * 
   * @return true if draggable
   */
  public boolean isDraggable() {
    return draggable;
  }

  /**
   * Returns true if tips are enabled.
   * 
   * @return true if tips are enabled
   */
  public boolean isUseTip() {
    return useTip;
  }

  public boolean isVertical() {
    return vertical;
  }

  @Override
  public void onComponentEvent(ComponentEvent ce) {
    super.onComponentEvent(ce);
    switch (ce.getEventTypeInt()) {
      case Event.ONKEYDOWN:
        onKeyDown(ce);
        break;
      case Event.ONCLICK:
        onClick(ce);
        break;
      case Event.ONFOCUS:
        onFocus(ce);
        break;
      case Event.ONBLUR:
        onBlur(ce);
        break;
    }
  }

  /**
   * Determines whether or not clicking on the slider axis will change the
   * slider (defaults to true).
   * 
   * @param clickToChange true to allow the slider axis to be clicked
   */
  public void setClickToChange(boolean clickToChange) {
    this.clickToChange = clickToChange;
  }

  /**
   * True to allow the slider to be dragged (default to true).
   * 
   * @param draggable true to enable dragging
   */
  public void setDraggable(boolean draggable) {
    this.draggable = draggable;
  }

  /**
   * How many units to change the slider when adjusting by drag and drop. Use
   * this option to enable 'snapping' (default to 10).
   * 
   * @param increment the increment
   */
  public void setIncrement(int increment) {
    this.increment = increment;
  }

  /**
   * Sets the max value (defaults to 100).
   * 
   * @param maxValue the max value
   */
  public void setMaxValue(int maxValue) {
    this.maxValue = maxValue;
    if (rendered) {
      targetEl.dom.setAttribute("aria-valuemax", "" + maxValue);
    }
  }

  /**
   * Sets the tool tip message (defaults to '{0}'). "{0} will be substituted
   * with the current slider value.
   * 
   * @param message the tool tip message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Sets the minimum value (defaults to 0).
   * 
   * @param minValue the minimum value
   */
  public void setMinValue(int minValue) {
    this.minValue = minValue;
    if (rendered) {
      targetEl.dom.setAttribute("aria-valuemin", "" + minValue);
    }
  }

  /**
   * True to enable tool tips (default to true).
   * 
   * @param useTip true to enable tool tips
   */
  public void setUseTip(boolean useTip) {
    this.useTip = useTip;
  }

  /**
   * Sets the current value.
   * 
   * @param value the value
   */
  public void setValue(int value) {
    setValue(value, false);
  }

  /**
   * Sets the current value.
   * 
   * @param value the value
   * @param supressEvent true to suppress the change event
   */
  public void setValue(int value, boolean supressEvent) {
    value = normalizeValue(value);
    if (supressEvent || this.value != value) {
      SliderEvent se = new SliderEvent(this);
      se.setOldValue(this.value);
      se.setNewValue(value);
      if (supressEvent || fireEvent(Events.BeforeChange, se)) {
        this.value = value;
        if (rendered) {
          moveThumb(translateValue(value));
          if (useTip) {
            thumb.setToolTip(getToolTipConfig(value));
          }
          onValueChange(value);
          Accessibility.setState(targetEl.dom, "aria-valuenow", "" + value);
          Accessibility.setState(targetEl.dom, "aria-valuetext", useTip ? onFormatValue(value) : "" + value);
        }
        if (!supressEvent) {
          fireEvent(Events.Change, se);
        }
      }
    }
  }

  /**
   * True to orient the slider vertically (defaults to false).
   * 
   * @param vertical true for vertical
   */
  public void setVertical(boolean vertical) {
    this.vertical = vertical;
  }

  @Override
  protected void afterRender() {
    super.afterRender();

    if (getAriaLabelledBy() != null) {
      targetEl.dom.setAttribute("aria-labelledby", getAriaLabelledBy());
    }

    if (isDraggable()) {
      drag = new Draggable(thumb);
      drag.setConstrainVertical(!vertical);
      drag.setConstrainHorizontal(vertical);
      drag.setMoveAfterProxyDrag(false);
      drag.setProxy(new El(DOM.createDiv()));
      drag.setStartDragDistance(0);
      drag.addDragListener(new DragListener() {
        @Override
        public void dragCancel(DragEvent de) {
          onDragCancel(de);
        }

        @Override
        public void dragEnd(DragEvent de) {
          onDragEnd(de);
        }

        @Override
        public void dragMove(DragEvent de) {
          onDragMove(de);
        }

        @Override
        public void dragStart(DragEvent de) {
          onDragStart(de);
        }
      });
    }

    int value = this.value;
    setValue(value, true);
  }

  protected int constrain(int value) {
    return Util.constrain(value, minValue, maxValue);
  }

  @Override
  protected ComponentEvent createComponentEvent(Event event) {
    return new SliderEvent(this, event);
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(thumb);

    if (getElement() != targetEl.dom) {
      DOM.setEventListener(targetEl.dom, this);
    }
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(thumb);

    if (getElement() != targetEl.dom) {
      DOM.setEventListener(targetEl.dom, null);
    }
  }

  protected int doSnap(int v) {
    if (increment == 1) {
      return v;
    }
    int m = v % increment;
    if (m != 0) {
      v -= m;
      if (m * 2 > increment) {
        v += increment;
      } else if (m * 2 < -increment) {
        v -= increment;
      }
    }
    return v;
  }

  protected double getRatio() {
    int v = maxValue - minValue;
    if (vertical) {
      int h = innerEl.getHeight();
      return v == 0 ? h : ((double) h / v);
    } else {
      int w = innerEl.getWidth();
      return v == 0 ? w : ((double) w / v);
    }
  }

  protected ToolTipConfig getToolTipConfig(int value) {
    ToolTipConfig t = new ToolTipConfig();
    t.setDismissDelay(0);
    t.setText(onFormatValue(value));
    t.setMinWidth(0);
    return t;
  }

  protected void moveThumb(int v) {
    if (vertical) {
      thumb.el().setStyleAttribute("bottom", v + "px");
    } else {
      thumb.el().setLeft(v);
    }
  }

  protected int normalizeValue(int value) {
    value = doSnap(value);
    value = constrain(value);
    return value;
  }

  protected void onAttach() {
    super.onAttach();
    el().repaint();
  }

  protected void onBlur(ComponentEvent ce) {
    if (GXT.isAriaEnabled()) {
      FocusFrame.get().unframe();
    }
  }

  protected void onClick(ComponentEvent ce) {
    if (isClickToChange() && ce.getTarget() != thumb.el().dom) {
      if (vertical) {
        setValue(reverseValue(ce.getClientY() - innerEl.getTop(false)));
      } else {
        setValue(reverseValue(ce.getClientX() - innerEl.getLeft(false)));
      }
    }
    if (!ce.getTarget().getTagName().equals("INPUT")) {
      focus();
    }
  }

  protected void onDragCancel(DragEvent de) {
    onDragEnd(de);
  }

  protected void onDragEnd(DragEvent de) {
    thumb.el().removeStyleName("x-slider-thumb-drag");
    if (useTip) {
      tip.hide();
    }
  }

  protected void onDragMove(DragEvent de) {
    if (vertical) {
      setValue(reverseValue(de.getClientY() - innerEl.getTop(false)));
    } else {
      setValue(reverseValue(de.getClientX() - halfThumb - innerEl.getLeft(false)));
    }
    updateTip();
  }

  protected void onDragStart(DragEvent de) {
    thumb.el().addStyleName("x-slider-thumb-drag");
    thumb.el().setStyleAttribute("position", "");
    if (useTip) {
      thumb.getToolTip().hide();
      updateTip();
    }
  }

  protected void onFocus(ComponentEvent ce) {
    if (GXT.isAriaEnabled()) {
      FocusFrame.get().frame(this, targetEl.dom);
    }
  }

  protected String onFormatValue(int value) {
    return Format.substitute(getMessage(), value);
  }

  protected void onKeyDown(ComponentEvent ce) {
    int keyCode = ce.getKeyCode();
    switch (keyCode) {
      case KeyCodes.KEY_LEFT:
      case KeyCodes.KEY_DOWN:
      case KeyCodes.KEY_PAGEDOWN:
        ce.stopEvent();
        if (ce.isControlKey()) {
          setValue(minValue);
        } else {
          setValue(value - increment);
        }
        break;
      case KeyCodes.KEY_RIGHT:
      case KeyCodes.KEY_UP:
      case KeyCodes.KEY_PAGEUP:
        ce.stopEvent();
        if (ce.isControlKey()) {
          setValue(maxValue);
        } else {
          setValue(value + increment);
        }
        break;
      case KeyCodes.KEY_TAB:
        // do nothing
        break;
      case KeyCodes.KEY_HOME:
        setValue(minValue);
        break;
      case KeyCodes.KEY_END:
        setValue(maxValue);
        break;
    }
  }

  @Override
  protected void onRender(Element target, int index) {
    Element div = DOM.createDiv();
    if (el() == null) {
      setElement(div, target, index);
      div = getElement();
    } else {
      target.appendChild(div);
    }

    super.onRender(target, index);

    targetEl = new El(div);

    // handle wrapped slider
    if (getElement() != targetEl.dom) {
      DOM.sinkEvents(targetEl.dom, Event.FOCUSEVENTS);
    }

    targetEl.addStyleName("x-slider");
    targetEl.addStyleName(vertical ? "x-slider-vert" : "x-slider-horz");

    endEl = new El(DOM.createDiv());
    endEl.addStyleName("x-slider-end");

    innerEl = new El(DOM.createDiv());
    innerEl.addStyleName("x-slider-inner");
    endEl.appendChild(innerEl.dom);

    targetEl.appendChild(endEl.dom);

    thumb = new Thumb();
    thumb.render(innerEl.dom, 0);

    halfThumb = (vertical ? thumb.el().getHeight() : thumb.el().getWidth()) / 2;

    targetEl.setTabIndex(0);
    targetEl.setElementAttribute("hideFocus", "true");

    if (GXT.isAriaEnabled()) {
      Accessibility.setRole(targetEl.dom, "slider");

      if (!getTitle().equals("")) {
        Accessibility.setState(targetEl.dom, "aria-label", getTitle());
      }

      setMinValue(minValue);
      setMaxValue(maxValue);
    }

    sinkEvents(Event.ONKEYDOWN | Event.ONCLICK | Event.FOCUSEVENTS);

    if (useTip) {
      tip = new Tip();
      tip.setHeading("");
      tip.setMinWidth(0);
    }
  }

  @Override
  protected void onResize(int width, int height) {
    if (vertical) {
      innerEl.setHeight(height - el().getPadding("t") - endEl.getPadding("b"));
    } else {
      innerEl.setWidth(width - el().getPadding("l") - endEl.getPadding("r"));
    }
    syncThumb();
  }

  protected void onValueChange(int value) {

  }

  protected int reverseValue(int pos) {
    double ratio = getRatio();
    if (vertical) {
      return (int) ((minValue + innerEl.getHeight() - pos) / ratio);
    } else {
      return (int) ((pos + halfThumb + (minValue * ratio)) / ratio);
    }
  }

  protected void syncThumb() {
    if (rendered) {
      moveThumb(translateValue(value));
    }
  }

  protected int translateValue(int v) {
    double ratio = getRatio();
    return (int) ((v * ratio) - (minValue * ratio) - halfThumb);
  }

  protected void updateTip() {
    if (useTip) {
      if (!tip.isRendered()) {
        tip.showAt(-100, -100);
      }
      tip.getBody().update(onFormatValue(value));
      Point p = tip.el().getAlignToXY(thumb.el().dom, vertical ? "r-l?" : "b-t?",
          vertical ? new int[] {-5, 0} : new int[] {0, -5});
      tip.showAt(p.x, p.y);
    }
  }

}
