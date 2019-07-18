package com.benberi.cadesim.server;

import com.benberi.cadesim.server.codec.ServerChannelHandler;
import com.benberi.cadesim.server.codec.util.PacketDecoder;
import com.benberi.cadesim.server.codec.util.PacketEncoder;
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
     * The server context
     */
    private ServerContext context;

    /**
     * The server bootstrap
     */
    private GameServerBootstrap bootstrap;

    public CadeServer(ServerContext context, GameServerBootstrap bootstrap) {
        super();
        this.context = context;
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
        try {
            int port = context.getConfiguration().getPort();
            ChannelFuture f = bind(port).sync();
            if (f.isSuccess()) {
                bootstrap.startServices();
            }
            else {
                ServerContext.log("Could not bind the server on port " + port + ". Cause: " + f.cause().getMessage());
                System.exit(0);
            }

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
