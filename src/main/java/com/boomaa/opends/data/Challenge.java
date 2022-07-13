package com.boomaa.opends.data;

public class Challenge {
    private Challenge() {
    }

    private static final String[] RESPONSES = new String[] {
            "Johnny Five", "Data", "ED-209", "Bishop", "NXT", "Optimus Prime", "Roomba", "Rosie",
            "The Terminator", "HAL 9000", "Sojourner", "R2-D2", "C-3PO", "Wall-E", "Curiosity",
            "Opportunity", "Spirit", "Cylons", "Stinky", "Iron Giant", "RCX", "EV3", "ASIMO"
    };

    public static String getResponse(int rand, int teamNum) {
        return RESPONSES[(teamNum * rand) % 23];
    }
}
