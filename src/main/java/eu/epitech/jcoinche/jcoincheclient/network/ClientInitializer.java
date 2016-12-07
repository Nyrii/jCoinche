package eu.epitech.jcoinche.jcoincheclient.network;

import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * Created by noboud_n on 16/11/2016.
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public ClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), Connection.HOST, Integer.parseInt(System.getProperty("port", Connection.PORT))));
        }

        pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(180));
        pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder ());
        pipeline.addLast("protobufDecoder", new ProtobufDecoder(Game.Answer.getDefaultInstance()));
        pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender ());
        pipeline.addLast("protobufEncoder", new ProtobufEncoder ());

        pipeline.addLast(new ClientHandler());
    }
}
