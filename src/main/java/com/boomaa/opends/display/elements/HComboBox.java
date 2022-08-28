package com.boomaa.opends.display.elements;

import java.awt.ItemSelectable;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;

public class HComboBox<T> extends HideBase<JComboBox<T>> implements ItemSelectable {
    private final T[] items;
    private final List<ItemListener> listeners = new ArrayList<>();
    private int selectedIndex;

    @SafeVarargs
    public HComboBox(T... items) {
        super(() -> new JComboBox<>(items));
        this.items = items;
    }

    public Object getSelectedItem() {
        return getElement() == null ? items[selectedIndex] : getElement().getSelectedItem();
    }

    public void setSelectedItem(T item) {
        if (getElement() != null) {
            getElement().setSelectedItem(item);
        } else {
            int prevSelected = selectedIndex;
            for (int i = 0; i < items.length; i++) {
                if (item.equals(items[i])) {
                    selectedIndex = i;
                    break;
                }
            }
            ItemEvent selectEvent = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, items[selectedIndex], ItemEvent.SELECTED);
            ItemEvent deselectEvent = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, items[prevSelected], ItemEvent.DESELECTED);
            for (ItemListener listener : listeners) {
                listener.itemStateChanged(selectEvent);
                listener.itemStateChanged(deselectEvent);
            }
        }
    }

    public int getSelectedIndex() {
        return getElement() == null ? selectedIndex : getElement().getSelectedIndex();
    }

    public void addItemListener(ItemListener listener) {
        if (getElement() != null) {
            getElement().addItemListener(listener);
        } else {
            listeners.add(listener);
        }
    }

    public void removeItemListener(ItemListener listener) {
        if (getElement() != null) {
            getElement().removeItemListener(listener);
        } else {
            listeners.remove(listener);
        }
    }

    public Object[] getSelectedObjects() {
        return new Object[] { getSelectedItem() };
    }

    public void addActionListener(ActionListener listener) {
        //TODO make this work
    }
}
