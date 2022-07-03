package com.boomaa.opends.data.receive.parser;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;

public class ParserNull extends PacketParser {
    private static final ParserNull PARSER_NULL = new ParserNull();

    private ParserNull() {
        super(new byte[0], Remote.ROBO_RIO, Protocol.UDP, 0);
    }

    @Override
    public int getTagSize(int index) {
        return 0;
    }

    public static ParserNull getInstance() {
        return PARSER_NULL;
    }
}
