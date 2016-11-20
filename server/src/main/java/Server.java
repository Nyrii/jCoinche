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

    public static void main(String[] args) throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();

        EventLoopGroup gameGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(gameGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new chatClientInitializer(sslCtx));
            b.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            gameGroup.shutdownGracefully();
        }
    }

}
