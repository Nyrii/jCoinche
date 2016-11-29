package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.jcoincheserver.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;

import static eu.epitech.jcoinche.jcoincheserver.protobuf.Game.Answer.Type.BIDDING;
import static eu.epitech.jcoinche.jcoincheserver.protobuf.Game.Answer.Type.GAME;
import static eu.epitech.jcoinche.jcoincheserver.protobuf.Game.Answer.Type.STANDBY;

/**
 * Created by Saursinet on 22/11/2016.
 */
public class GameManager {

    ArrayList clientSocket = new ArrayList();
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
    private String message = "";

    public GameManager() {}

    public void addClient(String name, ChannelHandlerContext ctx) {
        clientSocket.add(ctx.channel());
        nameClient.add(name);
    }

    public boolean isInGame(ChannelHandlerContext ctx) {
        for (Object c : clientSocket) {
            if (c == ctx.channel())
                return true;
        }
        return false;
    }

    private String getNameFromSocket(ChannelHandlerContext ctx) {
        int i = 0;
        for (Object tmp : clientSocket) {
            if (ctx.channel() == tmp)
                break ;
            ++i;
        }
        return ((String) nameClient.get(i));
    }

    public void sendMessageToAllPersonInGame(ChannelHandlerContext arg0, String msg) {
        for (Object c: clientSocket) {
            if (c != arg0.channel()) {
//                c.writeAndFlush("[" + arg0.channel().remoteAddress() + "] " + msg + "\r\n");
//                c.writeAndFlush("[" + getNameFromSocket(arg0) + "] " + msg + "\n");
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest(msg).setType(STANDBY).setCode(300).build());
            } else {
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest(msg).setType(STANDBY).setCode(300).build());
//                c.writeAndFlush("[you] " + msg + "\n");
            }
        }
    }

    public void sendMessageToAllPersonInGame(String msg) {
        for (Object c: clientSocket) {
            ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest(msg).setType(STANDBY).setCode(300).build());
        }
    }

    public Game.Answer interpreteBidding(ChannelHandlerContext ctx, Game.Bidding bidding) {
        Game.Answer answer;

        if (getPersonWhoBet() == -1)
            System.out.println("the person who play is " + getNameFromClient(ctx));
        else
            System.out.println("the person who play is " + getNameFromClient(ctx) + " and the person who bet is " + nameClient.get(getPersonWhoBet()));
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
                    .setCards(getDeck(getClientPosition(ctx)))
                    .setType(BIDDING)
                    .build();
        }
        ctx.writeAndFlush(answer);
        sendMessageToAllPersonInGame(ctx, message);
        return answer;
    }

    public Game.Answer interpreteGaming(ChannelHandlerContext ctx, Game.GameProgress game) {
        Game.Answer answer = null;
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
        System.out.println("BIDDDDDDD");
        for (Object c : clientSocket) {
            if (i == 0) {
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder()
                        .setRequest("Your turn, you are allowed to bid.")
                        .setCode(200).setType(BIDDING)
                        .setCards(getDeck(i))
                        .build());
                System.out.println("send");
            } else
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder()
                        .setRequest("et fils de pute tu reponds?")
                        .setCode(200).setType(STANDBY)
                        .setCards(getDeck(i))
                        .build());
            ++i;
        }
    }

    public void advertAllPlayer() {
        int i = 0;
        for (Object c : clientSocket) {
            ((Channel) c).writeAndFlush(Game.Answer.newBuilder()
                    .setRequest("Your name is " + nameClient.get(i))
                    .setCode(200).setType(STANDBY)
                    .build());
            ++i;
        }
    }

    public void askPlayerOneToPlay() {
        int i = 0;
        for (Object c : clientSocket) {
            if (i == 0)
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest("Your turn, you have to play.").setCode(200).setType(GAME)
                        .setCards(getDeck(i)).build());
            ++i;
        }
    }

    public void getNextPlayerChannel(Game.Answer.Type type, String msg) {
        turn = turn == 3 ? 0 : turn + 1;
        int i = 0;
        for (Object c : clientSocket) {
            if (i == turn)
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest("Your turn, " + msg).setCode(200).setType(type).setCards(getDeck(i)).build());
            ++i;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean bidIsOver() {
        return bidding;
    }

    public int getClientPosition(ChannelHandlerContext ctx) {
        int i = 0;
        for (Object c : clientSocket) {
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

    public boolean getBid() { return bidding; }

    public boolean getGame() { return game; }

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
        } else if (contract == -1 && nbTurnInactive == 4) {
            bidding = true;
            cm = new CardManager();
            turn = 3;
            cm.giveCardToAllPlayers(clientSocket);
            sendMessageToAllPersonInGame("Nobody put a contract we will give new cards.");
        }
    }

    public Game.DistributionCard getDeck(int pos) {
        return cm.getDeckFromPosition(pos);
    }

    public String getNameFromClient(ChannelHandlerContext ctx) {
        return (String) nameClient.get(getClientPosition(ctx));
    }
}
