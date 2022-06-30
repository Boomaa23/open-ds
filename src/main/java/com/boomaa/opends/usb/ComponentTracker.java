package com.boomaa.opends.usb;

import java.util.LinkedHashMap;
import java.util.Map;

public class ComponentTracker {
    private final Map<Component.Identifier, Integer> hardwarePath;
    private final Map<Component.Identifier, Component.Identifier> userMap;
    private final Map<Component.Identifier, Integer> directMap;

    public ComponentTracker() {
        this.hardwarePath = new LinkedHashMap<>();
        this.userMap = new LinkedHashMap<>();
        this.directMap = new LinkedHashMap<>();
    }

    public ComponentTracker map(Component.Identifier userId, Component.Identifier hardwareId) {
        directMap.clear();
        if (hardwarePath.containsKey(hardwareId)) {
            userMap.put(userId, hardwareId);
        }
        return this;
    }

    public ComponentTracker mapAllSelf(Component.Identifier[] ids) {
        directMap.clear();
        for (Component.Identifier id : ids) {
            if (hardwarePath.containsKey(id)) {
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
        hardwarePath.put(hardwareId, idx);
        return this;
    }

    public int getIndex(Component.Identifier userId) {
        return userMap.containsKey(userId) ? hardwarePath.get(userMap.get(userId)) : -1;
    }

    public Map<Component.Identifier, Integer> getDirectMap() {
        if (directMap.size() == 0) {
            for (Map.Entry<Component.Identifier, Component.Identifier> userMapEntry : userMap.entrySet()) {
                directMap.put(userMapEntry.getKey(), hardwarePath.get(userMapEntry.getValue()));
            }
        }
        return directMap;
    }

    public int numMapped() {
        return userMap.size();
    }

    public int numTracked() {
        return hardwarePath.size();
    }
}
