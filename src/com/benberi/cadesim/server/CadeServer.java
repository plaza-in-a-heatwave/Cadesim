package com.benberi.cadesim.server;

import com.benberi.cadesim.server.codec.ServerChannelHandler;
import com.benberi.cadesim.server.codec.util.PacketDecoder;
import com.benberi.cadesim.server.codec.util.PacketEncoder;
import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.config.ServerConfiguration;
import com.benberi.cadesim.server.service.GameServerBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Obsidio game server for Blockade Simulator
 *
 * @author Ben Beri <benberi545@gmail.com> | Jony
 */
public class CadeServer extends ServerBootstrap implements Runnable {

    /**
     * Server event loop worker
     */
    private EventLoopGroup worker = new NioEventLoopGroup(2);
    private EventLoopGroup workerBoss = new NioEventLoopGroup();

    /**
     * The server bootstrap
     */
    private GameServerBootstrap bootstrap;

    public CadeServer(ServerContext context, GameServerBootstrap bootstrap) {
        super();
        this.bootstrap = bootstrap;
        group(workerBoss, worker);
        channel(NioServerSocketChannel.class);
        childOption(ChannelOption.TCP_NODELAY, true);
        childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast("encoder", new PacketEncoder());
                p.addLast("decoder", new PacketDecoder());
                p.addLast("handler", new ServerChannelHandler(context));
            }
        });
    }

    /**
     * On server startup
     */
    public void run() {
    	int port = ServerConfiguration.getPort();
    	int error = Constants.EXIT_SUCCESS; // was there any error?
        try {
            ChannelFuture f = bind(port).sync();
            if (f.isSuccess()) {
                bootstrap.startServices();
            }
            else {
            	ServerContext.log("Could not bind the server on local port " + port + ". Cause: " + f.cause().getMessage());
            	throw new java.net.SocketException();
            }

            f.channel().closeFuture().sync();
        } catch (java.net.SocketException e) {
        	// check permissions and number of instances
        	ServerContext.log("Could not bind the server on local port " + port + ": " + e.getMessage());
            error = Constants.EXIT_ERROR_CANT_BIND_LOCAL;
        } catch (Exception e)
        {
        	ServerContext.log("Couldn't start server, reason unknown: " + e);
        	error = Constants.EXIT_ERROR_UNKNOWN;
    	} finally {
            worker.shutdownGracefully();
        }
        System.exit(error);
    }
}
