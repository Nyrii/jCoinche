package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.*;
import static eu.epitech.jcoinche.protobuf.Game.Card.CardValue.*;

/**
 * Created by Saursinet on 22/11/2016.
 */
public class GameManager {

    ArrayList clientSocket = new ArrayList();
    ArrayList nameClient = new ArrayList();
    ArrayList lastTrick = new ArrayList();
    ArrayList currentTrick = new ArrayList();

    private boolean play = false;
    private int turn = -1;
    private int turnPos = -1;
    private int turnPersonToPlay = -1;
    private int personWhoBet = -1;
    private int personWhoCoinche = -1;
    private int personWhoSurCoinche = -1;
    private int personWhoCapot = -1;
    private int personWhoGenerale = -1;
    private int contract = -1;
    private boolean coinche = false;
    private boolean surCoinche = false;
    private boolean capot = false;
    private boolean generale = false;
    private boolean bidding = false;
    private boolean game = false;
    private  Game.Bidding.Options atout = Game.Bidding.Options.UNKNOWNOPTION;
    private int nbTurnInactive = 0;

    private int scoreTeam1 = 0;
    private int scoreTeam2 = 0;
    private int scoreTeamParty1 = 0;
    private int scoreTeamParty2 = 0;
    private int nbTrick1 = 0;
    private int nbTrick2 = 0;
    private int nbTrick3 = 0;
    private int nbTrick4 = 0;

    private CardManager cm = new CardManager();
    private String message = "";
    ChannelHandlerContext ctxTmp;
    private boolean end = false;

    private HashMap valueCardsAtout = new HashMap();
    private HashMap valueCards = new HashMap();

    public GameManager() {
        valueCards.put(SEVEN, 0);
        valueCards.put(EIGHT, 0);
        valueCards.put(NINE, 0);
        valueCards.put(JACK, 2);
        valueCards.put(QUEEN, 3);
        valueCards.put(KING, 4);
        valueCards.put(TEN, 10);
        valueCards.put(AS, 19);
        valueCardsAtout.put(SEVEN, 0);
        valueCardsAtout.put(EIGHT, 0);
        valueCardsAtout.put(QUEEN, 2);
        valueCardsAtout.put(KING, 3);
        valueCardsAtout.put(TEN, 5);
        valueCardsAtout.put(AS, 7);
        valueCardsAtout.put(NINE, 9);
        valueCardsAtout.put(JACK, 14);
    }

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

    public Game.Answer sendMessageToAllPersonInGame(int clientPosition, String msg) {
        int i = 0;
        for (Object c: clientSocket) {
            if (i != clientPosition)
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest("[" + nameClient.get(clientPosition) + "] " +msg).setType(Game.Answer.Type.NONE).setCode(300).build());
            ++i;
        }
        return Game.Answer.newBuilder()
                .setRequest("Message send")
                .setCode(300)
                .setCards(getDeck(clientPosition))
                .setType(GAME)
                .build();
    }

    public Game.Answer sendMessageToAllPersonToInteract(int clientPosition, String msg) {
        int i = 0;
        for (Object c: clientSocket) {
            if (i != clientPosition)
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest(msg).setType(Game.Answer.Type.GAME).setCode(0).build());
            ++i;
        }
        return Game.Answer.newBuilder()
                .setRequest("Message send")
                .setCode(300)
                .setCards(getDeck(clientPosition))
                .setType(GAME)
                .build();
    }

    public void sendMessageToAllPersonInGame(String msg) {
        for (Object c: clientSocket) {
            ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest(msg).setType(Game.Answer.Type.NONE).setCode(300).build());
        }
    }

    public Game.Answer interpreteBidding(ChannelHandlerContext ctx, Game.Bidding bidding) {
        Game.Answer answer;

        if (getClientPosition(ctx) == turn) {
            answer = BiddingManager.interpreteBidding(this, ctx, bidding, contract);
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
                    .setType(Game.Answer.Type.NONE)
                    .build();
        }
        ctx.writeAndFlush(answer);
        sendMessageToAllPersonInGame(getClientPosition(ctx), message);
        return answer;
    }

    public Game.Answer interpreteGaming(int clientPosition, Game.GameProgress game) {
        return PartyManager.interpreteGaming(clientPosition, game, this);
    }

    public boolean isFullGame() {
        return nameClient.size() == 4 ? true : false;
    }

    public boolean partyCanBegin() {
        return NameManager.partyCanBegin(nameClient);
    }

    public void giveCardToAllPlayers() {
        cm.generateCardForAllPlayer();
        cm.giveCardToAllPlayers(clientSocket);
        turn = 0;
        bidding = true;
    }

    public void askPlayerOneToBid() {
        int i = 0;
        for (Object c : clientSocket) {
            if (i == 0)
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder()
                        .setRequest("Your turn, you are allowed to bid.")
                        .setCode(200).setType(BIDDING)
                        .setCards(getDeck(i))
                        .build());
            ++i;
        }
    }

    public void askPlayerOneToPlay() {
        int i = 0;
        System.out.println("send invite to play to " + turnPersonToPlay);
        turnPersonToPlay = turnPersonToPlay == 3 ? 0 : turnPersonToPlay + 1;
        turn = turnPersonToPlay;
        for (Object c : clientSocket) {
            if (i == turnPersonToPlay)
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest("Your turn, you have to play.").setCode(200).setType(NONE)
                        .setCards(getDeck(i)).build());
            ++i;
        }
    }

    public void getNextPlayerChannel(Game.Answer.Type type, String msg) {
        turn = turn == 3 ? 0 : turn + 1;
        if (type == NONE) {
            if (turn == turnPos) {
                PartyManager.endLastTrick();
            }
        }
        if (type == NONE && bidding) {
            return ;
        }
        int i = 0;
        for (Object c : clientSocket) {
            if (i == turn)
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest("Your turn, " + msg).setCode(200).setType(type).setCards(getDeck(i)).build());
            ++i;
        }
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

    public boolean arePartner(int play1, int play2) {
        if (((play1 == 1 || play1 == 3) && (play2 == 1 || play2 == 3)) ||
                ((play1 == 0 || play1 == 2) && (play2 == 0 || play2 == 2)))
            return true;
        return false;
    }

    public void addInactiveTurn(int nbTurnInactive) {
        this.nbTurnInactive = nbTurnInactive;
    }

    public void checkIfPartyCanRun() {
        System.out.println("enter checkif party can run");
        if ((contract != -1 && nbTurnInactive == 3) ||
                capot || surCoinche) {
            bidding = false;
            game = true;
            while (turn < 4) {
                ((Channel) clientSocket.get(turn)).writeAndFlush(
                Game.Answer.newBuilder()
                        .setRequest("You may change your or send message or do what you want! Enjoy!")
                        .setCode(200)
                        .setCards(getDeck(turn))
                        .setType(GAME)
                        .build());
                ++turn;
            }
            if (turnPersonToPlay != -1)
                turn = turnPersonToPlay;
            else
                turn = 0;
            turnPos = turn;
            nbTrick1 = 0;
            nbTrick2 = 0;
            nbTrick3 = 0;
            nbTrick4 = 0;
            scoreTeamParty1 = 0;
            scoreTeamParty2 = 0;
            System.out.println("just put game to true");
        } else if (contract == -1 && nbTurnInactive == 4) {
            bidding = true;
            cm = new CardManager();
            turn = 3;
            cm.generateCardForAllPlayer();
            cm.giveCardToAllPlayers(clientSocket);
            sendMessageToAllPersonInGame("Nobody put a contract we will give new cards.");
        }
    }

    public void deleteClient(int clientPosition) {
        bidding = false;
        game = false;
        clientSocket.remove(clientPosition);
        nameClient.remove(clientPosition);
    }

    public int getNbTurnInactive() {
        return nbTurnInactive;
    }

    public Game.DistributionCard getDeck(int pos) {
        return cm.getDeckFromPosition(pos);
    }

    public void setDeck(int pos, Game.DistributionCard deck) {
        cm.setDeckWithPosition(pos, deck);
    }

    public String getNameFromClient(ChannelHandlerContext ctx) {
        return (String) nameClient.get(getClientPosition(ctx));
    }

    public void setAtout(Game.Bidding.Options atout) {
        this.atout = atout;
    }

    public void setChannelHandlerContext(ChannelHandlerContext ctx) {
        ctxTmp = ctx;
    }

    public CardManager getCardManager() {
        return cm;
    }

    public Boolean getEnd() {
        return end;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getTurn() {
        return turn;
    }

    public ArrayList getCurrentTrick() {
        return currentTrick;
    }

    public void setCurrentTrick(ArrayList trick) {
        currentTrick = trick;
    }

    public ArrayList getLastTrick() {
        return lastTrick;
    }

    public Game.Bidding.Options getAtout() {
        return atout;
    }

    public HashMap getValueCardsAtout() {
        return valueCardsAtout;
    }

    public HashMap getValueCards() {
        return valueCards;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public int getScoreTeam1() {
        return scoreTeam1;
    }

    public int getScoreTeam2() {
        return scoreTeam2;
    }

    public int getScoreTeamParty1() {
        return scoreTeamParty1;
    }

    public int getScoreTeamParty2() {
        return scoreTeamParty2;
    }

    public int getNbTrick1() {
        return nbTrick1;
    }

    public int getNbTrick2() {
        return nbTrick2;
    }

    public int getNbTrick3() {
        return nbTrick3;
    }

    public int getNbTrick4() {
        return nbTrick4;
    }

    public int getPersonWhoCoinche() {
        return personWhoCoinche;
    }

    public int getPersonWhoSurCoinche() {
        return personWhoSurCoinche;
    }

    public int getPersonWhoCapot() {
        return personWhoCapot;
    }

    public int getPersonWhoGenerale() {
        return personWhoGenerale;
    }

    public int getContract() {
        return contract;
    }

    public boolean isBidding() {
        return bidding;
    }

    public void setBidding(boolean bidding) {
        this.bidding = bidding;
    }

    public boolean isGame() {
        return game;
    }

    public void setGame(boolean game) {
        this.game = game;
    }

    public void setScoreTeam1(int scoreTeam1) {
        this.scoreTeam1 = scoreTeam1;
    }

    public void setScoreTeam2(int scoreTeam2) {
        this.scoreTeam2 = scoreTeam2;
    }

    public void setScoreTeamParty1(int scoreTeamParty1) {
        this.scoreTeamParty1 = scoreTeamParty1;
    }

    public void setScoreTeamParty2(int scoreTeamParty2) {
        this.scoreTeamParty2 = scoreTeamParty2;
    }

    public void setNbTrick1(int nbTrick1) {
        this.nbTrick1 = nbTrick1;
    }

    public void setNbTrick2(int nbTrick2) {
        this.nbTrick2 = nbTrick2;
    }

    public void setNbTrick3(int nbTrick3) {
        this.nbTrick3 = nbTrick3;
    }

    public void setNbTrick4(int nbTrick4) {
        this.nbTrick4 = nbTrick4;
    }

    public String getMessage() {
        return message;
    }

    public void setLastTrick(ArrayList lastTrick) {
        this.lastTrick = lastTrick;
    }

    public void setTurnPos(int turnPos) {
        this.turnPos = turnPos;
    }

    public ArrayList getClientSocket() {
        return clientSocket;
    }

    public ArrayList getNameClient() {
        return nameClient;
    }

    public Game.Answer setName(String name) {
        return NameManager.setName(this, nameClient, ctxTmp, name);
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

    public void setGenerale(boolean generale, ChannelHandlerContext ctx) {
        this.generale = generale;
        personWhoGenerale = getClientPosition(ctx);
    }

    public boolean getCoinche() { return this.coinche; }

    public boolean getSurCoinche() { return this.surCoinche; }

    public int getPersonWhoBet() { return personWhoBet; }

    public void setMessage(String message) {
        this.message = message;
    }
}
