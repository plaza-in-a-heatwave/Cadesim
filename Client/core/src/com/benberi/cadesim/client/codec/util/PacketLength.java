package com.benberi.cadesim.client.codec.util;

public enum PacketLength {
    BYTE(1),
    SHORT(2),
    MEDIUM(4);

    private int type;

    PacketLength(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static PacketLength get(int length) {
        for (PacketLength l : values()) {
            if(l.type == length) {
                return l;
            }
        }

        return null;
    }
}
