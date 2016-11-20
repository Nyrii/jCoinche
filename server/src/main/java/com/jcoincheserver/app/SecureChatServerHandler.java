package com.jcoincheserver.app;

import com.jcoincheserver.protobuff.Player.Person;
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
 * Created by Saursinet on 16/11/2016.
 */
public class SecureChatServerHandler extends SimpleChannelInboundHandler<String> {

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
    public void channelRead0(ChannelHandlerContext arg0, String i) throws Exception {
        // TODO Auto-generated method stub
//        byte[] bFile = i.toByteArray();
        System.out.println("MDR");
        System.out.println(i);

    }

//    @Override
//    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//        for (Channel c: clientSocket) {
//            if (c != ctx.channel()) {
//                c.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg + '\n');
//            } else {
//                if (!AnswerToClient.interprete(this, nameClient, ctx, msg))
//                    c.writeAndFlush("[you] " + msg + '\n');
//            }
//        }
//
//        for (int i = 0; i < nameClient.size(); i++) {
//            System.out.println(nameClient.get(i));
//        }
//        System.out.println("");
//
//        if ("quit".equals(msg.toLowerCase())) {
//            ctx.close();
//        }
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void setNameClient(ArrayList nameClient) {
        this.nameClient = nameClient;
    }
}
