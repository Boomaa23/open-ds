package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.networktables.NTEntry;
import com.boomaa.opends.networktables.NTStorage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NTFrame extends PopupBase {
    private static final Insets stdInsets = new Insets(5, 5, 5, 5);
    private static final int tabWidth = 6;
    private static final int lineWidth = 8;
    private JScrollPane entryDisplayWrapper = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private GBCPanelBuilder base;
    private JPanel entryDisplay;
    private int tabStartIndex = 0;
    private JPanel tabsPanel;

    public NTFrame() {
        super("Shuffleboard", new Dimension(800, 450));
    }

    @Override
    public void config() {
        super.config();
        content.setLayout(new GridBagLayout());
        base = new GBCPanelBuilder(content).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(stdInsets);

        int lineHeight = 30;
        BasicArrowButton leftMenubar = new BasicArrowButton(SwingConstants.WEST);
        BasicArrowButton rightMenubar = new BasicArrowButton(SwingConstants.EAST);
        leftMenubar.addActionListener((e) -> {
            tabStartIndex = Math.max(0, tabStartIndex - 1);
            populateTabsBar();
        });
        rightMenubar.addActionListener((e) -> {
            tabStartIndex = Math.min(tabStartIndex + 1, NTStorage.TABS.size() - tabWidth);
            populateTabsBar();
        });

        Dimension mainDim = new Dimension(125, lineHeight);
        leftMenubar.setPreferredSize(mainDim);
        rightMenubar.setPreferredSize(mainDim);
        base.clone().setPos(6, 0, 1, 1).setFill(GridBagConstraints.NONE).build(leftMenubar);
        base.clone().setPos(7, 0, 1, 1).setFill(GridBagConstraints.NONE).build(rightMenubar);

        tabsPanel = new JPanel();
        tabsPanel.setPreferredSize(new Dimension(700, lineHeight));
        tabsPanel.setLayout(new GridBagLayout());
        entryDisplay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
//                g.fillRect(0, 0, getWidth(), getHeight());
                //TODO backgrounds for entries with insets
            }
        };
        entryDisplay.setLayout(new GridBagLayout());

        populateTabsBar();
        boolean enableArrows = NTStorage.TABS.size() > tabWidth;
        leftMenubar.setEnabled(enableArrows);
        rightMenubar.setEnabled(enableArrows);
    }

    private String truncate(String in, int max, boolean addDots) {
        return in.substring(0, Math.min(in.length(), max)) + (in.length() > max && addDots ? "..." : "");
    }

    private void populateTab(String name) {
        entryDisplay.removeAll();
        GBCPanelBuilder gbcEntry = new GBCPanelBuilder(entryDisplay).setInsets(stdInsets);
        List<NTEntry> entries = new ArrayList<>(NTStorage.NT_ENTRIES.values());
        int row = 0;
        int tempCtr = 0;
        if (!name.isBlank()) {
            for (int i = 0; i < entries.size(); i++) {
                NTEntry entry = entries.get(i);
                if (entry.getTabName().equals(name) && entry.isInShuffleboard()) {
                    gbcEntry.clone().setX(i % lineWidth).setY(row).build(new JLabel(entry.getKey()));
                    gbcEntry.clone().setX(i % lineWidth).setY(row + 1).build(new JLabel(String.valueOf(entry.getValue())));
                    tempCtr++;
                    if (tempCtr > lineWidth - 1) {
                        row += 2;
                        tempCtr = 0;
                    }
                }
            }
        }

        entryDisplayWrapper.setViewportView(entryDisplay);
        entryDisplayWrapper.setPreferredSize(new Dimension(760, 300));
        entryDisplayWrapper.getVerticalScrollBar().setUnitIncrement(10);
        base.clone().setPos(0, 1, 8, 5).build(entryDisplayWrapper);
        entryDisplayWrapper.repaint();
        content.repaint();
        content.revalidate();
        super.repaint();
        super.revalidate();
    }

    private void populateTabsBar() {
        content.remove(tabsPanel);
        tabsPanel.removeAll();
        List<String> tabs = NTStorage.TABS;
        GBCPanelBuilder gbc = new GBCPanelBuilder(tabsPanel).setInsets(stdInsets);
        for (int i = tabStartIndex; i < tabWidth + tabStartIndex; i++) {
            JButton tabBtn = new JButton(i < tabs.size() ? truncate(tabs.get(i), 18, true) : "");
            tabBtn.addActionListener((e) -> populateTab(tabBtn.getText()));
            gbc.clone().setPos(i - tabStartIndex, 0, 1, 1).build(tabBtn);
        }
        base.clone().setPos(0, 0, 6, 1).build(tabsPanel);
        content.repaint();
        content.revalidate();
        super.repaint();
        super.revalidate();
    }
}
