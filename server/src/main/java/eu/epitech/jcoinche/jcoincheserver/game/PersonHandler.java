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
                                        "!\nPlease enter your name:")
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
        GameManager gm = getGameFromChannel(arg0);

        if (gm == null)
            return ;
        if (answer.getCode() == -1) {
            if (gm != null)
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
                arg0.writeAndFlush(gm.setName(arg0, answer.getPlayer().getName()));
                gm.setPlay(gm.partyCanBegin());
                if (gm.getPlay()) {
                    gm.giveCardToAllPlayers();
                    gm.askPlayerOneToBid();
                }
                break;

            case BIDDING:
                System.out.println("biding: ");
                System.out.println(answer.getBidding());
                if (gm.getPlay()) {
                    Game.Answer ans = gm.interpreteBidding(arg0, answer.getBidding());
                    arg0.writeAndFlush(ans);
                    if (ans.getCode() < 400)
                        gm.getNextPlayerChannel();
                }
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
