import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

/**
 * Created by Saursinet on 16/11/2016.
 */
public class chatClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslc;

    public chatClientInitializer(SslContext sslc) {
        this.sslc = sslc;
    }

    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(sslc.newHandler(ch.alloc()));

        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new SecureChatServerHandler());
    }
}
