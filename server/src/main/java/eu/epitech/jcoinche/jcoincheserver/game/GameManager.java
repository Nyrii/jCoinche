package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.jcoincheserver.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;

import static eu.epitech.jcoinche.jcoincheserver.protobuf.Game.Answer.Type.BIDDING;
import static eu.epitech.jcoinche.jcoincheserver.protobuf.Game.Answer.Type.GAME;

/**
 * Created by Saursinet on 22/11/2016.
 */
public class GameManager {

    ChannelGroup clientSocket = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    ArrayList nameClient = new ArrayList();
    boolean play = false;
    private int turn = -1;
    private int personWhoBet = -1;
    private int personWhoCoinche = -1;
    private int personWhoSurCoinche = -1;
    private int personWhoCapot = -1;
    private int contract = -1;
    private boolean coinche = false;
    private boolean surCoinche = false;
    private boolean capot = false;
    private boolean bidding = false;
    private boolean game = false;
    private int nbTurnInactive = 0;
    private int scoreTeam1 = 0;
    private int scoreTeam2 = 0;
    CardManager cm = new CardManager();

    public GameManager() {}

    public void addClient(String name, ChannelHandlerContext ctx) {
        clientSocket.add(ctx.channel());
        nameClient.add(name);
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
        Game.Answer answer;

        if (getClientPosition(ctx) == turn) {
            answer = AnswerToClient.interpreteBidding(this, ctx, bidding, contract);
            if (answer.getCode() == 200)
                personWhoBet = getClientPosition(ctx);
            else if (answer.getCode() == 201)
                personWhoCoinche = getClientPosition(ctx);
            else if (answer.getCode() == 202) {
                personWhoSurCoinche = getClientPosition(ctx);
                this.bidding = false;
            } else if (answer.getCode() == 203) {
                personWhoCapot = getClientPosition(ctx);
                this.bidding = false;
            }
        } else {
            answer = Game.Answer.newBuilder()
                    .setRequest("It's not your turn")
                    .setCode(400)
                    .setType(BIDDING)
                    .build();
        }
        return answer;
    }

    public boolean isFullGame() {
        return nameClient.size() == 4 ? true : false;
    }

    public boolean partyCanBegin() {
        return AnswerToClient.partyCanBegin(nameClient);
    }

    public void giveCardToAllPlayers() {
        cm.giveCardToAllPlayers(clientSocket);
        turn = 0;
        bidding = true;
    }

    public void askPlayerOneToBid() {
        int i = 0;
        for (Channel c : clientSocket) {
            if (i == 0)
                c.writeAndFlush(Game.Answer.newBuilder().setRequest("Please bid!").setCode(200).setType(BIDDING));
            ++i;
        }
    }

    public void askPlayerOneToPlay() {
        int i = 0;
        for (Channel c : clientSocket) {
            if (i == 0)
                c.writeAndFlush(Game.Answer.newBuilder().setRequest("Please play!").setCode(200).setType(GAME));
            ++i;
        }
    }

    public void getNextPlayerChannel() {
        turn = turn == 3 ? 0 : turn + 1;
        int i = 0;
        for (Channel c : clientSocket) {
            if (i == turn)
                c.writeAndFlush(Game.Answer.newBuilder().setRequest("Please bid!").setCode(200).setType(BIDDING));
            ++i;
        }
    }

    public boolean bidIsOver() {
        return bidding;
    }

    public int getClientPosition(ChannelHandlerContext ctx) {
        int i = 0;
        for (Channel c : clientSocket) {
            if (c == ctx.channel()) {
                break;
            }
            ++i;
        }
        return i;
    }

    public Game.Answer setName(ChannelHandlerContext ctx, String name) {
        return AnswerToClient.setName(this, nameClient, ctx, name);
    }

    public void setNameClient(ArrayList nameClient) {
        this.nameClient = nameClient;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public boolean getPlay() {
        return play;
    }

    public void setContract(int contract, ChannelHandlerContext ctx) { this.contract = contract; personWhoBet = getClientPosition(ctx); }

    public void setCoinche(boolean coinche, ChannelHandlerContext ctx) { this.coinche = coinche; this.personWhoCoinche = getClientPosition(ctx); }

    public void setSurCoinche(boolean surCoinche, ChannelHandlerContext ctx) { this.surCoinche = surCoinche; this.personWhoSurCoinche = getClientPosition(ctx); bidding = false; }

    public void setCapot(boolean capot, ChannelHandlerContext ctx) { this.capot= capot; personWhoCapot = getClientPosition(ctx); bidding = false; }

    public boolean getCoinche() { return this.coinche; }

    public boolean getSurCoinche() { return this.surCoinche; }

    public int getPersonWhoBet() { return personWhoBet; }

    public boolean arePartner(int play1, int play2) {
        if (((play1 == 1 || play1 == 3) && (play2 == 1 || play2 == 3)) ||
                ((play1 == 0 || play1 == 2) && (play2 == 0 || play2 == 2)))
            return true;
        return false;
    }

    public void addInactiveTurn(int nbTurnInactive) {
        this.nbTurnInactive = nbTurnInactive;
    }

    public int getNbTurnInactive() {
        return nbTurnInactive;
    }

    public void checkIfPartyCanRun() {
        if ((contract != -1 && nbTurnInactive == 4) ||
                capot || surCoinche) {
            bidding = false;
            game = true;
            askPlayerOneToPlay();
        } else if (contract == -1 && nbTurnInactive == 4) {
            bidding = false;
            cm.giveCardToAllPlayers(clientSocket);
            askPlayerOneToBid();
        }
    }
}
