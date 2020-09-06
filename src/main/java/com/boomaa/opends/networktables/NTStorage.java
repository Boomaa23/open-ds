package com.boomaa.opends.networktables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface NTStorage {
    List<NTPacketData> PACKET_DATA = new ArrayList<>();
    Map<Integer, NTEntry> ENTRIES = new HashMap<>();
    List<String> TABS = new ArrayList<>();
}
