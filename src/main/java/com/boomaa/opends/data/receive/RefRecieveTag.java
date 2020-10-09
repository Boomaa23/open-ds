package com.boomaa.opends.data.receive;

import com.boomaa.opends.display.DisplayEndpoint;

public class RefRecieveTag extends NullReceiveTag {
    private final int index;

    private RefRecieveTag(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static RefRecieveTag indexOfAction(int index) {
        return new RefRecieveTag(index);
    }

    public static RefRecieveTag yearOfAction(int year) {
        for (int i = 0; i < DisplayEndpoint.VALID_PROTOCOL_YEARS.length; i++) {
            if (year == DisplayEndpoint.VALID_PROTOCOL_YEARS[i]) {
                return new RefRecieveTag(i);
            }
        }
        return new RefRecieveTag(0);
    }
}
