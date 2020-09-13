package com.benberi.cadesim.client.codec.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Jony | Ben
 */
public class Packet {

    /**
     * Packets opcode
     */
    private int opcode;

    private int packetLength;

    private int length;

    /**
     * The data buffer of the packet
     */
    private ByteBuf dataBuffer;


    public Packet(int opcode) {
        this(opcode, Unpooled.buffer());
    }

    public Packet(int opcode, ByteBuf data) {
        this.opcode = opcode;
        this.dataBuffer = data;
    }

    public Packet(int opcode, int size) {
        this.opcode = opcode;
        this.dataBuffer = Unpooled.buffer(size);

    }

    public int getPacketLengthType() {
        return this.packetLength;
    }

    public void setPacketLengthType(PacketLength length) {
        this.packetLength = length.getType();
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }

    public int getSize() {
        return dataBuffer.readableBytes();
    }

    public void setOpcode(byte opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return this.opcode;
    }

    public int readInt() {
        return this.dataBuffer.readInt();
    }
    
    public int readShort() {
    	return this.dataBuffer.readShort();
    }

    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        this.dataBuffer.readBytes(bytes);

        return bytes;
    }

    public byte readByte() {
        return this.dataBuffer.readByte();
    }

    public String readByteString() {
        byte[] bytes = new byte[dataBuffer.readByte()];
        dataBuffer.readBytes(bytes);
        return new String(bytes);
    }

    public String readMediumString() {
        byte[] bytes = new byte[dataBuffer.readMedium()];
        dataBuffer.readBytes(bytes);
        return new String(bytes);
    }

    public String readIntString() {
        byte[] bytes = new byte[dataBuffer.readInt()];
        dataBuffer.readBytes(bytes);
        return new String(bytes);
    }

    public void writeByteString(String s) {
        byte[] strData = s.getBytes();
        dataBuffer.writeByte(strData.length);
        dataBuffer.writeBytes(strData);
    }

    public void writeMediumString(String s) {
        byte[] strData = s.getBytes();
        dataBuffer.writeMedium(strData.length);
        dataBuffer.writeBytes(strData);
    }

    public void writeIntString(String s) {
        byte[] strData = s.getBytes();
        dataBuffer.writeInt(strData.length);
        dataBuffer.writeBytes(strData);
    }

    public void writeInt(int integer) {
        this.dataBuffer.writeInt(integer);
    }

    public void writeBytes(byte[] bytes) {
        this.dataBuffer.writeBytes(bytes);
    }

    public void writeByte(int b) {
        this.dataBuffer.writeByte(b);
    }

    public ByteBuf getBuffer() {
        return dataBuffer;
    }
}