package com.boomaa.opends.networking;

import java.io.OutputStream;
import java.net.Socket;

public class SimulatedRobot extends TCPServer {
    public SimulatedRobot() {
        super(1740);
    }

    @Override
    public void onRun(Socket client, OutputStream out, byte[] data) {

    }
}
