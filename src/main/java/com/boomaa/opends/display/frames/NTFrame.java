package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.elements.GBCPanelBuilder;
import com.boomaa.opends.networktables.NTEntry;
import com.boomaa.opends.networktables.NTNestedTab;
import com.boomaa.opends.networktables.NTStorage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NTFrame extends PopupBase {
    private static final Insets stdInsets = new Insets(5, 5, 5, 5);
    private static final Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
    private static final int borderRadius = 5;
    private static final int tabWidth = 6;
    private static final int lineWidth = 8;
    private final NTNestedTab tab;
    private JScrollPane entryDisplayWrapper;
    private GBCPanelBuilder base;
    private JPanel entryDisplay;
    private int tabStartIndex = 0;
    private JPanel tabsPanel;

    public NTFrame(String baseTabName, NTNestedTab tab) {
        super(baseTabName, new Dimension(800, 450));
        this.tab = tab;
    }

    @Override
    public void config() {
        super.config();
        content.setLayout(new GridBagLayout());
        this.base = new GBCPanelBuilder(content).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(stdInsets);
        this.entryDisplayWrapper = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        int lineHeight = 30;
        BasicArrowButton leftMenubar = new BasicArrowButton(SwingConstants.WEST);
        BasicArrowButton rightMenubar = new BasicArrowButton(SwingConstants.EAST);
        leftMenubar.addActionListener((e) -> {
            tabStartIndex = Math.max(0, tabStartIndex - 1);
            populateTabsBar();
        });
        rightMenubar.addActionListener((e) -> {
            tabStartIndex = Math.min(tabStartIndex + 1, tab.size() - tabWidth);
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
        entryDisplay = new JPanel();
        entryDisplay.setLayout(new GridBagLayout());

        populateTabsBar();
        boolean enableArrows = tab.size() > tabWidth;
        leftMenubar.setEnabled(enableArrows);
        rightMenubar.setEnabled(enableArrows);
        populateTab("");
    }

    private String truncate(String in, int max, boolean addDots) {
        return in.substring(0, Math.min(in.length(), max)) + (in.length() > max && addDots ? "..." : "");
    }

    private void populateTab(String name) {
        entryDisplay.removeAll();
        GBCPanelBuilder gbcEntry = new GBCPanelBuilder(entryDisplay).setInsets(stdInsets);
        List<NTEntry> entries = new ArrayList<>(NTStorage.ENTRIES.values());
        if (!name.isEmpty()) {
            for (int i = 0; i < entries.size(); i++) {
                NTEntry entry = entries.get(i);
                if (entry.getTabName().equals(name) && (entry.isInShuffleboard() || entry.isInSmartDashboard())) {
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
                    JLabel key = new JLabel(entry.getKey());
                    JLabel value = new JLabel(String.valueOf(entry.getValue()));
                    tempPanel.add(key, BorderLayout.NORTH);
                    tempPanel.add(value, BorderLayout.SOUTH);
                    tempPanel.setBorder(emptyBorder);
                    gbcEntry.clone().setX(i % lineWidth).setY(i / lineWidth).build(tempPanel);
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
        GBCPanelBuilder gbc = new GBCPanelBuilder(tabsPanel).setInsets(stdInsets);
        for (int i = tabStartIndex; i < tabWidth + tabStartIndex; i++) {
            JButton tabBtn = new JButton(i < tab.size() ? truncate(tab.get(i), 18, true) : "");
            tabBtn.addActionListener((e) -> populateTab(tabBtn.getText()));
            tabBtn.setVisible(i < tab.size());
            gbc.clone().setPos(i - tabStartIndex, 0, 1, 1).build(tabBtn);
        }
        base.clone().setPos(0, 0, 6, 1).build(tabsPanel);
        content.repaint();
        content.revalidate();
        super.repaint();
        super.revalidate();
    }
}
