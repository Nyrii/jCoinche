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

    static final ChannelGroup clientSocket = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    static ArrayList nameClient = new ArrayList();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        ctx.writeAndFlush("Welcome to jCoinche game hosted by " + InetAddress.getLocalHost().getHostName() + "!\n");
                        clientSocket.add(ctx.channel());
                        String[] tokens = ctx.toString().split(" ");
                        boolean isId = false;
                        for (String t : tokens) {
                            if (isId) {
                                System.out.println("token is " + t);
                                nameClient.add(t);
                                isId = false;
                            }
                            if (t.toLowerCase().contains("id")) {
                                isId = true;
                            }
                        }
                    }
                });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    @Override
    public void channelRead0(ChannelHandlerContext arg0, Answer answer) throws Exception {
        switch (answer.getType()) {

            case PLAYER:
                System.out.println(answer.getPlayer());
                break;

            case BIDDING:
                System.out.println(answer.getBidding());
                // do the processing
                break;

            case GAME:
                System.out.println(answer.getGame());
                // do the processing
                break;

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
