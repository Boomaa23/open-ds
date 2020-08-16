package com.boomaa.opends.networking;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SimulatedFMS extends SimulateBase {
    public SimulatedFMS() throws IOException {
        super(AddressConstants.getFMSPorts(), new TCPServer(AddressConstants.getFMSPorts().getTcp()) {
            @Override
            public void onRun(Socket client, OutputStream out, byte[] data) {

            }
        });
        //TODO figure out if the "from UDP port 1145" matters for FMS
    }
}
