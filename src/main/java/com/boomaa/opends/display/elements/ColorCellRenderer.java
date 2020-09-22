package com.boomaa.opends.display.elements;

import com.boomaa.opends.data.StatsFields;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class ColorCellRenderer implements TableCellRenderer {
    public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

    @Override
    public synchronized Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof StatsFields.DataSection) {
            StatsFields.DataSection data = (StatsFields.DataSection) value;
            comp.setBackground(data.getColor());
            ((JLabel) comp).setToolTipText(data.getName());
        } else {
            comp.setBackground(!isSelected ? Color.WHITE : UIManager.getDefaults().getColor("List.selectionBackground"));
        }
        return comp;
    }
}
