package com.jcoincheserver.app;

import com.jcoincheserver.protobuf.Game.Answer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by noboud_n on 20/11/2016.
 */
public class PersonHandler extends SimpleChannelInboundHandler<Answer>{

    @Override
    public void channelRead0(ChannelHandlerContext arg0, Answer i) throws Exception {
//        byte[] bFile = i.toByteArray();
        System.out.println("Par ici frere");
        System.out.println(i);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
