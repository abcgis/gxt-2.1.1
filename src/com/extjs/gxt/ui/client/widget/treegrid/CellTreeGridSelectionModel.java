/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.treegrid;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;

@SuppressWarnings("unchecked")
public class CellTreeGridSelectionModel<M extends ModelData> extends CellSelectionModel<M> {

  protected TreeGrid tree;
  protected TreeStore<M> treeStore;
  protected List<M> selectedPreRender;

  @Override
  public void bindGrid(Grid grid) {
    tree = null;
    treeStore = null;
    super.bindGrid(grid);
    if (grid != null) {
      tree = (TreeGrid) grid;
      treeStore = tree.getTreeStore();
    }
  }

  protected void ensureExpanded(M model) {
    List<M> stack = new ArrayList<M>();
    model = treeStore.getParent(model);
    while (model != null) {
      stack.add(model);
      model = treeStore.getParent(model);
    }

    for (int i = stack.size() - 1; i >= 0; i--) {
      M m = stack.get(i);
      tree.setExpanded(m, true);
    }
  }

  @Override
  protected void handleMouseDown(GridEvent<M> e) {
    if (!tree.getTreeView().isSelectableTarget(e.getModel(), e.getTarget())) {
      return;
    }
    super.handleMouseDown(e);
  }

  protected void hookPreRender(M item, boolean select) {
    if (selectedPreRender == null) {
      selectedPreRender = new ArrayList<M>();
      tree.addListener(Events.Render, new Listener<ComponentEvent>() {
        public void handleEvent(ComponentEvent be) {
          tree.removeListener(Events.Render, this);
          onRender();
        }
      });
    }
    if (select && !selectedPreRender.contains(item)) {
      selectedPreRender.add(item);
    } else if (!select) {
      selectedPreRender.remove(item);
    }
  }

  @Override
  protected void onAdd(List<? extends M> models) {

  }

  @Override
  protected void onKeyLeft(GridEvent<M> ce) {
    if (lastSelected == null) {
      return;
    }
    if (!tree.isLeaf(lastSelected) && tree.isExpanded(lastSelected)) {
      tree.setExpanded(lastSelected, false);
    } else if (treeStore.getParent(lastSelected) != null) {
      doSingleSelect(treeStore.getParent(lastSelected), false);
    }
  }

  @Override
  protected void onKeyRight(GridEvent<M> e) {
    if (lastSelected == null) {
      return;
    }
    if (!tree.isLeaf(lastSelected)) {
      if (!tree.isExpanded(lastSelected)) {
        tree.setExpanded(lastSelected, true);
      }
    }
  }

  protected void onRender() {
    if (selectedPreRender != null) {
      for (M item : selectedPreRender) {
        onSelectChange(item, true);
      }
      selectedPreRender = null;
    }
  }

  @Override
  protected void onSelectChange(M model, boolean select) {
    if (locked) return;
    if (!tree.isRendered()) {
      hookPreRender(model, select);
      return;
    }
    if (select) {
      ensureExpanded(model);
    }

    super.onSelectChange(model, select);
  }
}
