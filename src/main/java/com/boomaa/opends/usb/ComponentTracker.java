package com.boomaa.opends.usb;

import java.util.LinkedHashMap;
import java.util.Map;

public class ComponentTracker {
    private final Map<Component.Identifier, Integer> hardwareMap;
    private final Map<Component.Identifier, Component.Identifier> userMap;
    private final Map<Component.Identifier, Integer> directMap;

    public ComponentTracker() {
        this.hardwareMap = new LinkedHashMap<>();
        this.userMap = new LinkedHashMap<>();
        this.directMap = new LinkedHashMap<>();
    }

    public ComponentTracker map(Component.Identifier userId, Component.Identifier hardwareId) {
        directMap.clear();
        if (hardwareMap.containsKey(hardwareId)) {
            userMap.put(userId, hardwareId);
        }
        return this;
    }

    public ComponentTracker mapAllSelf(Component.Identifier[] ids) {
        directMap.clear();
        for (Component.Identifier id : ids) {
            if (hardwareMap.containsKey(id)) {
                userMap.put(id, id);
            }
        }
        return this;
    }

    public ComponentTracker unmap(Component.Identifier userId) {
        directMap.clear();
        userMap.remove(userId);
        return this;
    }

    public ComponentTracker track(Component.Identifier hardwareId, int idx) {
        hardwareMap.put(hardwareId, idx);
        return this;
    }

    public int getIndex(Component.Identifier userId) {
        return userMap.containsKey(userId) ? hardwareMap.get(userMap.get(userId)) : -1;
    }

    public Map<Component.Identifier, Integer> getDirectMap() {
        if (directMap.size() == 0) {
            for (Map.Entry<Component.Identifier, Component.Identifier> userMapEntry : userMap.entrySet()) {
                directMap.put(userMapEntry.getKey(), hardwareMap.get(userMapEntry.getValue()));
            }
        }
        return directMap;
    }

    public Map<Component.Identifier, Integer> getHardwareMap() {
        return hardwareMap;
    }

    public Map<Component.Identifier, Component.Identifier> getUserMap() {
        return userMap;
    }

    public int numMapped() {
        return userMap.size();
    }

    public int numTracked() {
        return hardwareMap.size();
    }
}
