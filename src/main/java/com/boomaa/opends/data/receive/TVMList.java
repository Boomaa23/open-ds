package com.boomaa.opends.data.receive;

import java.util.LinkedList;

public class TVMList extends LinkedList<TagValueMap<?>> {
    public TVMList getMatching(ReceiveTag tag) {
        TVMList matching = new TVMList();
        for (TagValueMap<?> map : this) {
            if (map.getBaseTag().equals(tag)) {
                matching.add(map);
            }
        }
        return matching;
    }

    public TagValueMap<?> first() {
        return this.get(0);
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }
}
