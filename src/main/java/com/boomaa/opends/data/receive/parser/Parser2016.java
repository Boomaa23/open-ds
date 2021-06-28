package com.boomaa.opends.data.receive.parser;

public class Parser2016 {
    public static class RioToDsUdp extends Parser2015.RioToDsUdp {
        public RioToDsUdp(byte[] packet) {
            super(packet);
        }
    }

    public static class RioToDsTcp extends Parser2015.RioToDsTcp {
        public RioToDsTcp(byte[] packet) {
            super(packet);
        }
    }

    public static class FmsToDsUdp extends Parser2015.FmsToDsUdp {
        public FmsToDsUdp(byte[] packet) {
            super(packet);
        }
    }

    public static class FmsToDsTcp extends Parser2015.FmsToDsTcp {
        public FmsToDsTcp(byte[] packet) {
            super(packet);
        }
    }
}
