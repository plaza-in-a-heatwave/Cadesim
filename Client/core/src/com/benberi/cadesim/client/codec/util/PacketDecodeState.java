package com.benberi.cadesim.client.codec.util;


public enum PacketDecodeState {

    /**
     * The opcode decoding state
     */
    OPCODE,

    /**
     * Length type of the packet length (byte, short or other)
     */
    LENGTH_TYPE,

    /**
     * The length decoding state
     */
    LENGTH,

    /**
     * The data decoding state
     */
    DATA
}
