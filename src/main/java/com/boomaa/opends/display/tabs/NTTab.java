package com.boomaa.opends.display.tabs;

import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.networktables.NTEntry;
import com.boomaa.opends.networktables.NTStorage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NTTab extends TabBase {
    private static final Insets stdInsets = new Insets(5, 5, 5, 5);
    private static final Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
    private static final int borderRadius = 5;
    private static final int tabWidth = 6;
    private static final int lineWidth = 5;
    private Map<Integer, JLabel> displayedEntries;
    private JScrollPane entryDisplayWrapper;
    private GBCPanelBuilder base;
    private JPanel entryDisplay;
    private int tabStartIndex = 0;
    private JPanel tabsPanel;
    private String currentTab;

    @Override
    public void config() {
        super.setLayout(new GridBagLayout());
        this.base = new GBCPanelBuilder(this).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(stdInsets);
        this.entryDisplayWrapper = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

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

        Dimension mainDim = new Dimension(100, lineHeight);
        leftMenubar.setPreferredSize(mainDim);
        rightMenubar.setPreferredSize(mainDim);
        base.clone().setPos(6, 0, 1, 1).setFill(GridBagConstraints.NONE).build(leftMenubar);
        base.clone().setPos(7, 0, 1, 1).setFill(GridBagConstraints.NONE).build(rightMenubar);

        tabsPanel = new JPanel();
        tabsPanel.setPreferredSize(new Dimension(440, lineHeight));
        tabsPanel.setLayout(new GridBagLayout());
        entryDisplay = new JPanel();
        entryDisplay.setLayout(new GridBagLayout());

        displayedEntries = new HashMap<>();
        populateTabsBar();
        boolean enableArrows = NTStorage.TABS.size() > tabWidth;
        leftMenubar.setEnabled(enableArrows);
        rightMenubar.setEnabled(enableArrows);
        populateTab("");
    }

    private String truncate(String in, int max, boolean addDots) {
        return in.substring(0, Math.min(in.length(), max)) + (in.length() > max && addDots ? "..." : "");
    }

    public void populateTab(String name) {
        currentTab = name;
        entryDisplay.removeAll();
        GBCPanelBuilder gbcEntry = new GBCPanelBuilder(entryDisplay).setInsets(stdInsets);
        List<NTEntry> ntEntries = new ArrayList<>(NTStorage.ENTRIES.values());
        if (!name.isEmpty()) {
            int entryCtr = 0;
            for (NTEntry entry : ntEntries) {
                if (entry.getTabName().equals(name) && (entry.isInShuffleboard() || entry.isInSmartDashboard()) && !entry.isInHidden()) {
                    JPanel tempPanel = new JPanel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            Graphics2D graphics = (Graphics2D) g;
                            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                            graphics.setColor(Color.LIGHT_GRAY);
                            graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, borderRadius, borderRadius);
                            graphics.setColor(Color.GRAY);
                            graphics.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, borderRadius, borderRadius);
                        }
                    };
                    tempPanel.setLayout(new BorderLayout());
                    JLabel key = new JLabel(entry.getKey(), SwingConstants.CENTER);
                    Font f = key.getFont();
                    key.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
                    JLabel value = new JLabel(String.valueOf(entry.getValue()), SwingConstants.CENTER);
                    tempPanel.add(key, BorderLayout.NORTH);
                    tempPanel.add(value, BorderLayout.SOUTH);
                    tempPanel.setBorder(emptyBorder);
                    gbcEntry.clone().setX(entryCtr % lineWidth).setY(entryCtr++ / lineWidth).build(tempPanel);
                    displayedEntries.put(entry.getId(), value);
                }
            }
        }
        entryDisplay.repaint();
        entryDisplay.revalidate();
        entryDisplayWrapper.setViewportView(entryDisplay);
        entryDisplayWrapper.setPreferredSize(new Dimension(400, 220));
        entryDisplayWrapper.getVerticalScrollBar().setUnitIncrement(10);
        base.clone().setPos(0, 1, 8, 5).build(entryDisplayWrapper);
        entryDisplayWrapper.repaint();
        entryDisplayWrapper.revalidate();
        super.repaint();
        super.revalidate();
    }

    public void populateTabsBar() {
        displayedEntries.clear();
        super.remove(tabsPanel);
        tabsPanel.removeAll();
        GBCPanelBuilder gbc = new GBCPanelBuilder(tabsPanel).setInsets(stdInsets);
        for (int i = tabStartIndex; i < tabWidth + tabStartIndex; i++) {
            JButton tabBtn = new JButton(i < NTStorage.TABS.size() ? truncate(NTStorage.TABS.get(i), 18, true) : "");
            tabBtn.addActionListener((e) -> populateTab(tabBtn.getText()));
            tabBtn.setVisible(i < NTStorage.TABS.size());
            gbc.clone().setPos(i - tabStartIndex, 0, 1, 1).build(tabBtn);
        }
        base.clone().setPos(0, 0, 6, 1).build(tabsPanel);
        tabsPanel.repaint();
        tabsPanel.revalidate();
        super.repaint();
        super.revalidate();
    }

    public void updateValue(NTEntry entry) {
        if (entry.getTabName().equals(currentTab)) {
            if (displayedEntries.containsKey(entry.getId())) {
                displayedEntries.get(entry.getId()).setText(String.valueOf(entry.getValue()));
            } else {
                populateTab(currentTab);
            }
        }
    }
}
