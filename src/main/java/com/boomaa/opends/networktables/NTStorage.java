package com.boomaa.opends.networktables;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface NTStorage {
    Map<Integer, NTEntry> ENTRIES = new LinkedHashMap<>();
    List<String> TABS = new LinkedList<>();
    Map<String, Integer> CLIENTS = new LinkedHashMap<>();
}
