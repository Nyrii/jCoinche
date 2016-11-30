package eu.epitech.jcoinche.jcoincheserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Created by Saursinet on 16/11/2016.
 */
public class Server {
    static final int PORT = Integer.parseInt(System.getProperty("port", "4242"));
    static EventLoopGroup gameGroup = null;

    public static void launchServer() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();

        gameGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(gameGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer(sslCtx));
            b.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            shutDownServer();
        }
    }

    public static void shutDownServer() {
        gameGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        try {
            launchServer();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(84);
        }
    }

}
