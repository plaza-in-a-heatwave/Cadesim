package com.benberi.cadesim.client;

import com.benberi.cadesim.Constants;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.client.codec.ClientChannelHandler;
import com.benberi.cadesim.client.codec.util.PacketDecoder;
import com.benberi.cadesim.client.codec.util.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientConnectionTask extends Bootstrap implements Runnable {

    /**
     * The game context
     */
    private GameContext context;

    /**
     * The worker
     */
    private EventLoopGroup worker = new NioEventLoopGroup(1);

    /**
     * The IP Address to connect
     */
    private String ip;

    /**
     * The callback to notify
     */
    private ClientConnectionCallback callback;
    private Channel channel;

    public ClientConnectionTask(GameContext context, String ip, ClientConnectionCallback callback) {
        this.context = context;
        this.ip = ip;
        this.callback = callback;
    }

    @Override
    public void run() {	
        group(worker);
        channel(NioSocketChannel.class);
        option(ChannelOption.TCP_NODELAY, true);
        handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast("encoder", new PacketEncoder());
                p.addLast("decoder", new PacketDecoder());
                p.addLast("handler", new ClientChannelHandler(context));
            }
        });
        ChannelFuture future = connect(ip,Constants.PROTOCOL_PORT);
        future.addListener(new ChannelFutureListener() {
        	@Override public void operationComplete(ChannelFuture future) throws Exception{
		        if(!future.isSuccess()) {
		        	if(channel != null) {channel.flush();}
		        	future.channel().close();
		        	callback.onFailure();
		        }else {
		        	channel = future.channel();
		        	channel.closeFuture().addListener(new ChannelFutureListener() {
		            	@Override public void operationComplete(ChannelFuture future) throws Exception{
		            		worker.shutdownGracefully().addListener(e -> {
		            			context.dispose();
		            			context.getConnectScene().closePopup();
		            		});
		            			try {
			            			if (!context.clientInitiatedDisconnect()) { // whodunnit?
			            				context.handleServersideDisconnect();
			            			}
		            			}catch(Exception e) {
			            			System.out.println(e);
			            		}
		            			context.setClientInitiatedDisconnect(false); // clear flag
		    		    }
		        	});
		        	callback.onSuccess(channel);
		        }
        	}
        });

	}
 
}
