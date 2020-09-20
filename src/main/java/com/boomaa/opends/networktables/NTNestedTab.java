package com.boomaa.opends.networktables;

import java.util.ArrayList;

public class NTNestedTab extends ArrayList<String> {
    private final NTNestedTab nested = new NTNestedTab();

    public NTNestedTab getNestedTabs() {
        return nested;
    }
}
