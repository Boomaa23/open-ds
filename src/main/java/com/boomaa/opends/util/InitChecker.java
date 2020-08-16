package com.boomaa.opends.util;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;

public class InitChecker {
    private boolean rioUdp = false;
    private boolean rioTcp = false;
    private boolean fmsUdp = false;
    private boolean fmsTcp = false;

    public boolean get(Remote remote, Protocol protocol) {
        if (remote == Remote.ROBO_RIO) {
            if (protocol == Protocol.UDP) {
                return rioUdp;
            } else {
                return rioTcp;
            }
        } else {
            if (protocol == Protocol.UDP) {
                return fmsUdp;
            } else {
                return fmsTcp;
            }
        }
    }

    public void set(boolean value, Remote remote, Protocol protocol) {
        if (remote == Remote.ROBO_RIO) {
            if (protocol == Protocol.UDP) {
                rioUdp = value;
            } else {
                rioTcp = value;
            }
        } else {
            if (protocol == Protocol.UDP) {
                fmsUdp = value;
            } else {
                fmsTcp = value;
            }
        }
    }

    public boolean isAndInit(Protocol protocol) {
        return protocol == Protocol.UDP ? (rioUdp && fmsUdp) : (rioTcp && fmsTcp);
    }

    public boolean isAndInit(Remote remote) {
        return remote == Remote.ROBO_RIO ? (rioUdp && rioTcp) : (fmsUdp && fmsTcp);
    }

    public boolean isOrnit(Protocol protocol) {
        return protocol == Protocol.UDP ? (rioUdp || fmsUdp) : (rioTcp || fmsTcp);
    }

    public boolean isOrInit(Remote remote) {
        return remote == Remote.ROBO_RIO ? (rioUdp || rioTcp) : (fmsUdp || fmsTcp);
    }
}
