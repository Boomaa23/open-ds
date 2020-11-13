package com.boomaa.opends.data.send;

import com.boomaa.opends.display.DisplayEndpoint;

public class RefSendTag extends NullSendTag {
    private final int index;

    private RefSendTag(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static RefSendTag indexOfAction(int index) {
        return new RefSendTag(index);
    }

    public static RefSendTag yearOfAction(int year) {
        for (int i = 0; i < DisplayEndpoint.VALID_PROTOCOL_YEARS.length; i++) {
            if (year == DisplayEndpoint.VALID_PROTOCOL_YEARS[i]) {
                return new RefSendTag(i);
            }
        }
        return new RefSendTag(0);
    }
}
