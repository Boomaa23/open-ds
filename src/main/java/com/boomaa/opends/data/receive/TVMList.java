package com.boomaa.opends.data.receive;

import java.util.ArrayList;

public class TVMList extends ArrayList<TagValueMap<?>> {
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
}
