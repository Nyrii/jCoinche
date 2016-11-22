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

    boolean play = false;

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        Answer person = Answer.newBuilder()
                                .setRequest("Welcome to jCoinche game hosted by " +
                                        InetAddress.getLocalHost().getHostName() +
                                        "!\nPlease enter your name:\r\n")
                                .setType(Answer.Type.PLAYER)
                                .setCode(200)
                                .build();
                        ctx.writeAndFlush(person);
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
        if (answer.getCode() == -1) {
            System.out.println(getNameFromSocket(arg0) + " just quit the game");
            sendMessageToAllClient(arg0, getNameFromSocket(arg0) + " just quit the game");
            arg0.close();
            return ;
        }

        for (int i = 0; i < nameClient.size(); i++) {
            System.out.println(nameClient.get(i));
        }
        System.out.println("");

        System.out.println(answer.getAllFields());
        switch (answer.getType()) {

            case PLAYER:
                System.out.println("player: ");
                System.out.println(answer.getPlayer());
                arg0.writeAndFlush(AnswerToClient.setName(this, nameClient, arg0, answer.getPlayer().getName().substring(0, answer.getPlayer().getName().length() - 2)));
                break;

            case BIDDING:
                System.out.println("biding: ");
                System.out.println(answer.getBidding());
                if (play)
                    arg0.writeAndFlush(AnswerToClient.interpreteBidding(this, nameClient, arg0, answer.getBidding()));
                break;

            case GAME:
                System.out.println("game: ");
                System.out.println(answer.getGame());
                // do the processing
                break;

        }
        play = AnswerToClient.partyCanBegin(nameClient);
//        if (play)
        if (CardManager.initCardList())
            CardManager.giveCardToAllPlayers(clientSocket);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendMessageToAllClient(ChannelHandlerContext arg0, String msg) {
        for (Channel c: clientSocket) {
            if (c != arg0.channel()) {
                c.writeAndFlush("[" + arg0.channel().remoteAddress() + "] " + msg + "\r\n");
            } else {
//                if (!AnswerToClient.interprete(this, nameClient, arg0, answer))
                c.writeAndFlush("[you] " + msg + "\r\n");
            }
        }
    }

    public void setNameClient(ArrayList nameClient) {
        this.nameClient = nameClient;
    }

    private String getNameFromSocket(ChannelHandlerContext ctx) {
        int i = 0;
        for (Channel tmp : clientSocket) {
            if (ctx.channel() == tmp)
                break ;
            ++i;
        }
        return ((String) nameClient.get(i));
    }

}
