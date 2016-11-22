package com.jcoincheserver.app;

import com.jcoincheserver.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;

/**
 * Created by Saursinet on 22/11/2016.
 */
public class GameManager {

    ChannelGroup clientSocket = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    ArrayList nameClient = new ArrayList();
    boolean play = false;
    private int turn = 0;
    CardManager cm = new CardManager();

    public GameManager() {}

    public void addClient(String name, ChannelHandlerContext ctx) {
        clientSocket.add(ctx.channel());
        nameClient.add(name);
    }

    public ChannelGroup getClientSocket() {
        return clientSocket;
    }

    public boolean isInGame(ChannelHandlerContext ctx) {
        for (Channel c : clientSocket) {
            if (c == ctx.channel())
                return true;
        }
        return false;
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

    public void sendMessageToAllPersonInGame(ChannelHandlerContext arg0, String msg) {
        for (Channel c: clientSocket) {
            if (c != arg0.channel()) {
//                c.writeAndFlush("[" + arg0.channel().remoteAddress() + "] " + msg + "\r\n");
                c.writeAndFlush("[" + getNameFromSocket(arg0) + "] " + msg + "\r\n");
            } else {
                c.writeAndFlush("[you] " + msg + "\r\n");
            }
        }
    }

    public Game.Answer interpreteBidding(ChannelHandlerContext ctx, Game.Bidding bidding) {
        return AnswerToClient.interpreteBidding(nameClient, ctx, bidding);
    }

    public boolean isFullGame() {
        return nameClient.size() == 4 ? true : false;
    }

    public boolean partyCanBegin() {
        return AnswerToClient.partyCanBegin(nameClient);
    }

    public void giveCardToAllPlayers() {
        cm.giveCardToAllPlayers(clientSocket);
    }

    public Game.Answer setName(ChannelHandlerContext ctx, String name) { return AnswerToClient.setName(this, nameClient, ctx, name); }

    public void setNameClient(ArrayList nameClient) {
        this.nameClient = nameClient;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public boolean getPlay() {
        return play;
    }
}
