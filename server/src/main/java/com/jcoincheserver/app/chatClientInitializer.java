package com.jcoincheserver.app;

import com.jcoincheserver.protobuf.Game.Answer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
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

        pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        pipeline.addLast("protobufDecoder", new ProtobufDecoder(Answer.getDefaultInstance()));
        pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast("protobufEncoder", new ProtobufEncoder());

        pipeline.addLast("personHandler", new PersonHandler());

    }
}
