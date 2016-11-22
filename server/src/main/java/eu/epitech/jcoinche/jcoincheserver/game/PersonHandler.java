package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.jcoincheserver.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by noboud_n on 20/11/2016.
 */
public class PersonHandler extends SimpleChannelInboundHandler<Game.Answer>{

    static ArrayList gameRunning = new ArrayList();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        Game.Answer person = Game.Answer.newBuilder()
                                .setRequest("Welcome to jCoinche game hosted by " +
                                        InetAddress.getLocalHost().getHostName() +
                                        "!\nPlease enter your name:\r\n")
                                .setType(Game.Answer.Type.PLAYER)
                                .setCode(200)
                                .build();
                        ctx.writeAndFlush(person);
                        addPersonToGames(ctx);
                    }
                });
    }

    private void addPersonToGames(ChannelHandlerContext ctx) {
        if (gameRunning.size() == 0)
            gameRunning.add(new GameManager());
        String[] tokens = ctx.toString().split(" ");
        boolean isId = false;
        boolean putInGame = false;
        String tmp = "";
        for (String t : tokens) {
            if (isId) {
                tmp = t;
                isId = false;
            }
            if (t.toLowerCase().contains("id")) {
                isId = true;
            }
        }
        for (int i = 0; i < gameRunning.size(); ++i) {
            if (!((GameManager) gameRunning.get(i)).isFullGame()) {
                ((GameManager) gameRunning.get(i)).addClient(tmp, ctx);
                putInGame = true;
            }
        }
        if (!putInGame) {
            GameManager gm = new GameManager();
            gm.addClient(tmp, ctx);
            gameRunning.add(gm);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext arg0, Game.Answer answer) throws Exception {
        if (answer.getCode() == -1) {
            GameManager gm;
            if ((gm = getGameFromChannel(arg0)) != null)
                 gm.sendMessageToAllPersonInGame(arg0, " just quit the game");
            System.out.println(arg0.channel().remoteAddress() + " just quit the game");
            arg0.close();
            return ;
        }
        System.out.println(answer.getAllFields());
        switch (answer.getType()) {

            case PLAYER:
                System.out.println("player: ");
                System.out.println(answer.getPlayer());
                arg0.writeAndFlush(getGameFromChannel(arg0).setName(arg0, answer.getPlayer().getName().substring(0, answer.getPlayer().getName().length() - 2)));
                getGameFromChannel(arg0).setPlay(getGameFromChannel(arg0).partyCanBegin());
                if (getGameFromChannel(arg0).getPlay())
                    getGameFromChannel(arg0).giveCardToAllPlayers();
                break;

            case BIDDING:
                System.out.println("biding: ");
                System.out.println(answer.getBidding());
                if (getGameFromChannel(arg0).getPlay())
                    arg0.writeAndFlush(getGameFromChannel(arg0).interpreteBidding(arg0, answer.getBidding()));
                break;

            case GAME:
                System.out.println("game: ");
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

    private GameManager getGameFromChannel(ChannelHandlerContext ctx) {
        for (Object gm : gameRunning) {
            if (((GameManager) gm).isInGame(ctx))
                return ((GameManager) gm);
        }
        return null;
    }

}
