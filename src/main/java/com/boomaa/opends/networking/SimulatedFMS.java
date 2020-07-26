package com.boomaa.opends.networking;

import java.io.OutputStream;
import java.net.Socket;

public class SimulatedFMS extends TCPServer {
    public SimulatedFMS() {
        super(1750);
    }

    @Override
    public void onRun(Socket client, OutputStream out, byte[] data) {

    }
}
