package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.protobuf.Game;
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

    private static ArrayList gameRunning = new ArrayList();

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

        if (gm == null) {
            return;
        }
        if (answer.getCode() == -1) {
            if (gm != null) {
                gm.sendMessageToAllPersonInGame(gm.getClientPosition(arg0), " left the game");
                gm.quitGame();
            }
            System.out.println(arg0.channel().remoteAddress() + " left the game");
            return ;
        }
        switch (answer.getType()) {

            case PLAYER:
                gm.setChannelHandlerContext(arg0);
                arg0.writeAndFlush(gm.setName(answer.getPlayer().getName()));
                gm.setPlay(gm.partyCanBegin());
                if (gm.getPlay()) {
                    gm.giveCardToAllPlayers();
                    gm.askPlayerOneToBid();
                }
                break;

            case BIDDING:
                if (gm.getPlay()) {
                    Game.Answer ans = gm.interpreteBidding(gm.getPersonByChannel(arg0), answer.getBidding());
                    arg0.writeAndFlush(ans);
                    if (gm.getBid() && ans.getCode() < 400 && ans.getCode() != 203 && ans.getCode() != 202 && ans.getCode() != 204)
                        gm.getNextPlayerChannel(Game.Answer.Type.BIDDING, "you are allowed to bid.");
                }
                if (gm.getGame()) {
                    gm.askPlayerOneToPlay();
                    gm.sendMessageToAllPersonToInteract(gm.getClientPosition(arg0), "You may interact with server");
                }
                break;

            case GAME:
                if (gm.getGame()) {
                    gm.setChannelHandlerContext(arg0);
                    Game.Answer ans = gm.interpreteGaming(gm.getClientPosition(arg0), answer.getGame());
                    if (gm.getEnd() || ans.getType() == Game.Answer.Type.LEAVE) {
                        gm.sendMessageToAllPersonInGame(gm.getClientPosition(arg0), " left the game");
                        gm.quitGame();
                        gameRunning.remove(gm);
                        break;
                    }
                    if (ans.getCode() < 300) {
                        gm.getNextPlayerChannel(Game.Answer.Type.NONE, "You have to play.");
                    }
                    arg0.writeAndFlush(ans);
                    gm.sendMessageToAllPersonToInteract(gm.getClientPosition(arg0), "You may interact with server");
                    if (gm.getBid())
                        gm.getNextPlayerChannel(Game.Answer.Type.BIDDING, "you may bid.");
                }
                break;

            case NONE:
                arg0.writeAndFlush(Game.Answer.newBuilder().setType(Game.Answer.Type.GAME).setCode(-1).setRequest(""));
                break;


            case LEAVE:
                gm.deleteClient(gm.getClientPosition(arg0));
                arg0.close();
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
