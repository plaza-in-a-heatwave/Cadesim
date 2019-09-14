package com.benberi.cadesim.server.codec.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import com.benberi.cadesim.server.ServerContext;

public class PacketDecoder extends StatefulByteDecoder<PacketDecodeState> {

    /**
     * The opcode
     */
    private int opcode;

    /**
     * The length
     */
    private int length;

    /**
     * Packet's length type
     */
    private PacketLength lengthType;


    public PacketDecoder() {
        setState(PacketDecodeState.OPCODE);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf buffer, PacketDecodeState state, List<Object> out) {
        try {
	    	switch(state) {
	            /**
	             * Opcode decoding process
	             */
	            case OPCODE:
	                decodeOpcode(ctx, buffer, out);
	                break;
	
	            case LENGTH_TYPE:
	                decodeLengthType(ctx, buffer, out);
	                break;
	            /**
	             * Length decoding process
	             */
	            case LENGTH:
	                decodeLength(ctx, buffer, out);
	                break;
	
	            /**
	             * Data decoding process
	             */
	            case DATA:
	                decodeData(ctx, buffer, out);
	                break;
	        }
        }
        catch(Exception e)
        {
        	ServerContext.log(
            		"WARNING - malformed low-level packet from " +
            		ctx.channel().remoteAddress() +
            		", kicked sender"
            );
            ctx.channel().disconnect();
//            ctx.channel().deregister();
        }
    }

    /**
     * Decode the opcode of the packet
     * @param ctx       The channel context
     * @param buffer    The data buffer
     * @param out       POJO objects list
     */
    private void decodeOpcode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
    	if (buffer.isReadable()) {
            this.opcode = buffer.readByte();
            setState(PacketDecodeState.LENGTH_TYPE);
        }
    }

    /**
     * Decode the length of the packet
     * @param ctx       The channel context
     * @param buffer    The data buffer
     * @param out       POJO objects list
     */
    private void decodeLengthType(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
    	if (buffer.isReadable()) {
            byte lengthType = buffer.readByte();
            PacketLength length = PacketLength.get(lengthType);
            if (length != null) {
                this.lengthType = length;
            }
            else {
                this.lengthType = PacketLength.MEDIUM;
            }

            setState(PacketDecodeState.LENGTH);
        }
    }

    /**
     * Decode the length of the packet
     * @param ctx       The channel context
     * @param buffer    The data buffer
     * @param out       POJO objects list
     */
    private void decodeLength(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
    	if (buffer.isReadable()) {
            switch(lengthType) {
                case BYTE:
                    this.length = buffer.readUnsignedByte();
                    break;
                case SHORT:
                    this.length = buffer.readUnsignedShort();
                    break;
                case MEDIUM:
                    this.length = buffer.readUnsignedMedium();
                    break;
            }

            setState(PacketDecodeState.DATA);
        }
    }

    /**
     * Decode the data of the packet
     * @param ctx       The channel context
     * @param buffer    The data buffer
     * @param out       POJO objects list
     */
    private void decodeData(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {
    	if (buffer.readableBytes() >= length) {
            ByteBuf data = buffer.readBytes(length);
            out.add(new Packet(opcode, data));
            setState(PacketDecodeState.OPCODE);
    	}
    }
}
