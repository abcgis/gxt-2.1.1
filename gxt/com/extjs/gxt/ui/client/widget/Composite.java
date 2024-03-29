/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * A component that wraps another component, hiding the wrapped components
 * public API.
 * 
 * <p />
 * {@link #initComponent(Component)} must be called to initialize the composite.
 * 
 * <p />
 * If the wrapped components is a <code>LayoutContainer</code> instance, it's
 * layout will be executed as if the composite was a
 * <code>LayoutContainer</code>.
 * 
 * <p />
 * Code snippet:
 * 
 * <pre>
 * public void onModuleLoad() {
 *   class TestComposite extends Composite {
 *     public TestComposite() {
 *       LayoutContainer c = new LayoutContainer();
 *       c.setLayout(new RowLayout(Orientation.HORIZONTAL));
 * 
 *       ContentPanel cp1 = new ContentPanel();
 *       cp1.setHeading(&quot;Composite Test 1&quot;);
 *       c.add(cp1, new RowData(.5, 1));
 * 
 *       ContentPanel cp2 = new ContentPanel();
 *       cp2.setHeading(&quot;Composite Test 2&quot;);
 *       c.add(cp2, new RowData(.5, 1));
 * 
 *       initComponent(c);
 *     }
 *   }
 * 
 *   Viewport v = new Viewport();
 *   v.setLayout(new FitLayout());
 *   v.add(new TestComposite());
 *   RootPanel.get().add(v);
 * }
 * </pre>
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>BoxComponent Move</dd>
 * <dd>BoxComponent Resize</dd>
 * <dd>Component Enable</dd>
 * <dd>Component Disable</dd>
 * <dd>Component BeforeHide</dd>
 * <dd>Component Hide</dd>
 * <dd>Component BeforeShow</dd>
 * <dd>Component Show</dd>
 * <dd>Component Attach</dd>
 * <dd>Component Detach</dd>
 * <dd>Component BeforeRender</dd>
 * <dd>Component Render</dd>
 * <dd>Component BrowserEvent</dd>
 * <dd>Component BeforeStateRestore</dd>
 * <dd>Component StateRestore</dd>
 * <dd>Component BeforeStateSave</dd>
 * <dd>Component SaveState</dd>
 * </dl>
 */
public class Composite extends BoxComponent {

  protected Component component;

  /**
   * Returns the wrapped component.
   * 
   * @return the component
   */
  public Component getComponent() {
    return component;
  }

  @Override
  public Element getElement() {
    // we need this because of lazy rendering
    return component.getElement();
  }

  @Override
  public boolean isAttached() {
    if (component != null) {
      return component.isAttached();
    }
    return false;
  }

  @Override
  public void onBrowserEvent(Event event) {
    // Fire any handler added to the composite itself.
    super.onBrowserEvent(event);

    // Delegate events to the widget.
    component.onBrowserEvent(event);
  }

  @Override
  public void setSize(int width, int height) {
    if (component instanceof BoxComponent) {
      ((BoxComponent) component).setSize(width, height);
    }
  }

  @Override
  public void setSize(String width, String height) {
    if (component instanceof BoxComponent) {
      ((BoxComponent) component).setSize(width, height);
    }
  }

  protected void initComponent(Component component) {
    component.removeFromParent();
    this.component = component;
    component.setParent(this);
  }

  @Override
  protected void onAttach() {
    component.onAttach();
    DOM.setEventListener(getElement(), this);
    onLoad();
  }

  @Override
  protected void onDetach() {
    try {
      onUnload();
    } finally {
      component.onDetach();
    }
    onDetachHelper();
  }

  @Override
  protected void onDisable() {
    super.onDisable();
    component.disable();
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    component.enable();
  }

  @Override
  protected void onRender(Element target, int index) {
    component.render(target, index);
    setElement(component.getElement());
  }

}
