package com.boomaa.opends.display.tabs;

import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.display.elements.ColorCellRenderer;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class StatsTab extends TabBase {
    public static final DefaultTableModel TABLE_MODEL = new DefaultTableModel(new String[0][], new String[] { "", "Key", "Value" });

    public StatsTab() {
        super(new Dimension(395, 270));
    }

    @Override
    public void config() {
        StatsFields[] allFields = StatsFields.values();
        for (StatsFields field : allFields) {
            TABLE_MODEL.addRow(new Object[] { field.getSection(), field.getKey(), field.getValue() });
        }

        JTable table = new JTable(TABLE_MODEL) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setDefaultRenderer(Object.class, new ColorCellRenderer());
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setWidths(table, 0, 10, 50, 10);
        setWidths(table, 1, 100, -1, 150);
        setWidths(table, 2, 100, -1, 200);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(super.dimension);
        super.add(scroll);

        super.repaint();
        super.revalidate();
    }

    private void setWidths(JTable table, int index, int min, int max, int preferred) {
        TableColumn col = table.getColumnModel().getColumn(index);
        if (min != -1) {
            col.setMinWidth(min);
        }
        if (max != -1) {
            col.setMaxWidth(max);
        }
        if (preferred != -1) {
            col.setPreferredWidth(preferred);
        }
    }
}
