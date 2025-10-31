package com.boomaa.opends.data.receive.parser;

public class Parser2024 {
    public static class RioToDsUdp extends Parser2020.RioToDsUdp {
        public RioToDsUdp(byte[] packet) {
            super(packet);
        }
    }

    public static class RioToDsTcp extends Parser2020.RioToDsTcp {
        public RioToDsTcp(byte[] packet) {
            super(packet);
        }
    }

    public static class FmsToDsUdp extends Parser2020.FmsToDsUdp {
        public FmsToDsUdp(byte[] packet) {
            super(packet);
        }
    }

    public static class FmsToDsTcp extends Parser2020.FmsToDsTcp {
        public FmsToDsTcp(byte[] packet) {
            super(packet);
        }
    }
}
