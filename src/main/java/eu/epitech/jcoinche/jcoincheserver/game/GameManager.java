package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.*;
import static eu.epitech.jcoinche.protobuf.Game.Card.CardValue.*;
import static eu.epitech.jcoinche.protobuf.Game.GameProgress.Command.*;
import static eu.epitech.jcoinche.protobuf.Game.GameProgress.Command.NONE;
import static eu.epitech.jcoinche.protobuf.Game.GameProgress.Command.valueOf;

/**
 * Created by Saursinet on 22/11/2016.
 */
public class GameManager {

    ArrayList clientSocket = new ArrayList();
    ArrayList nameClient = new ArrayList();
    ArrayList lastTrick = new ArrayList();
    ArrayList currentTrick = new ArrayList();
    boolean play = false;
    private int turn = -1;
    private int turnPersonToPlay = -1;
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
    Game.Bidding.Options atout = Game.Bidding.Options.UNKNOWNOPTION;
    private int nbTurnInactive = 0;
    private int scoreTeam1 = 0;
    private int scoreTeam2 = 0;
    CardManager cm = new CardManager();
    private String message = "";
    ChannelHandlerContext ctxTmp;
    boolean end = false;


    HashMap valueCardsAtout = new HashMap();
    HashMap valueCards = new HashMap();

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

    private String getNameFromSocket(ChannelHandlerContext ctx) {
        int i = 0;
        for (Object tmp : clientSocket) {
            if (ctx.channel() == tmp)
                break ;
            ++i;
        }
        return ((String) nameClient.get(i));
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

    public void sendMessageToAllPersonInGame(String msg) {
        for (Object c: clientSocket) {
            ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest(msg).setType(Game.Answer.Type.NONE).setCode(300).build());
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
        sendMessageToAllPersonInGame(getClientPosition(ctx), message);
        return answer;
    }

    public Game.Answer interpreteGaming(int clientPosition, Game.GameProgress game) {
        Game.Answer answer;

        if (game.getCommand() == Game.GameProgress.Command.MSG) {
            answer = sendMessageToAllPersonInGame(clientPosition, game.getArguments(0));
        } else if (game.getCommand() == Game.GameProgress.Command.NAME) {
            answer = setName(game.getArguments(0));
            answer = Game.Answer.newBuilder()
                        .setRequest(answer.getRequest())
                        .setCode(answer.getCode())
                        .setCards(getDeck(clientPosition))
                        .setType(GAME)
                        .build();
        } else if (game.getCommand() == PLAY) {
            String msg;
            int code = 400;
            if (turn != clientPosition) {
                msg = "Not your turn to play";
            } else {
                Game.DistributionCard deck = getDeck(clientPosition);
                boolean found = false;
                for (Game.Card card : deck.getCardList()) {
                    if (card.getCardType() == game.getCard().getCardType() &&
                            card.getCardValue() == game.getCard().getCardValue())
                        found = true;
                }
                if (game.getCard().getCardType() == Game.Card.CardType.INVALID_TYPE)
                    msg = "Wrong Card that type doesn't exist.";
                else if (game.getCard().getCardValue() == Game.Card.CardValue.INVALID_VALUE)
                    msg = "Wrong Card that value doesn't exist.";
                else if (!found)
                    msg = "This Card doesn't belong to you.";
                else if (!checkValidityOfMovement(clientPosition, game.getCard()))
                    msg = "Invalid movement";
                else {
                    currentTrick.add(game.getCard());
                    deleteCardFromDeck(game.getCard(), deck);
                    setDeck(clientPosition, deck);
                    msg = "Turn okay";
                    code = 200;
                }
            }
            answer = Game.Answer.newBuilder()
                    .setRequest(msg)
                    .setCode(code)
                    .setCards(getDeck(clientPosition))
                    .setType(GAME)
                    .build();
        } else if (game.getCommand() == HAND) {
            answer = sendHandToPlayer(clientPosition);
        } else if (game.getCommand() == LAST_TRICK) {
            answer = showLastTrick(clientPosition);
        } else if (game.getCommand() == INVALID_COMMAND) {
            answer = sendInvalidCommand(clientPosition);
        } else {
            //user just quit
            System.out.println("User quit");
            answer = Game.Answer.newBuilder()
                    .setRequest("for now not implemented")
                    .setCode(200)
                    .setCards(getDeck(clientPosition))
                    .setType(GAME)
                    .build();
        }
        return answer;
    }

    private void deleteCardFromDeck(Game.Card card, Game.DistributionCard deck) {
        int i = 0;
        for (Object cards : deck.getCardList()) {
            if (card.getCardType() == ((Game.Card) cards).getCardType() &&
                    card.getCardValue() == ((Game.Card) cards).getCardValue()) {
                break ;
            }
            ++i;
        }
//        deck.getCardList().remove(i);
    }

    private boolean isAtout(Game.Card card) {
        return card.getCardType() == Game.Card.CardType.valueOf(atout.toString());
    }

    private boolean hasOneTypeOfCard(int clientPosition, Game.Card.CardType type) {
        Game.DistributionCard deck = getDeck(clientPosition);
        for (Game.Card card : deck.getCardList()) {
            if (card.getCardType() == type)
                return true;
        }
        return false;
    }

    private boolean checkValidityOfMovement(int clientPosition, Game.Card card) {
        if (currentTrick.size() == 0)
            return true;
        if (isAtout((Game.Card) currentTrick.get(0))) {
            if (isAtout(card)) {
                if (!isBiggerValueAtout(card.getCardValue(), biggestCardInTrickAtout(currentTrick).getCardValue()))
                    if (!checkIfPlayerCannotGoUp(biggestCardInTrickAtout(currentTrick), getDeck(clientPosition)))
                        return false;
            } else if (hasOneTypeOfCard(clientPosition, Game.Card.CardType.valueOf(atout.toString())))
                return false;
        } else {
            if (((Game.Card) currentTrick.get(0)).getCardType() != card.getCardType()) {
                if (hasOneTypeOfCard(clientPosition, ((Game.Card) currentTrick.get(0)).getCardType()))
                    return false;
                else if (isAtout(card) && isAtout(biggestCardInTrickAtout(currentTrick)) && !checkIfPlayerCannotGoUp(biggestCardInTrickAtout(currentTrick), getDeck(clientPosition)))
                    return false;
            }
        }
        return true;
    }

    private boolean checkIfPlayerCannotGoUp(Game.Card card, Game.DistributionCard deck) {
        for (Object cardPlayer : deck.getCardList()) {
            if (isAtout((Game.Card) cardPlayer) && isBiggerValueAtout(((Game.Card) cardPlayer).getCardValue(), card.getCardValue()))
                return false;
        }
        return true;
    }

    private Game.Card biggestCardInTrick(ArrayList currentTrick) {
        Game.Card firstCard = ((Game.Card) currentTrick.get(0));

        for (Object card : currentTrick) {
            if (((Game.Card) card).getCardType() == firstCard.getCardType() &&
                    isBiggerValue(((Game.Card) card).getCardValue(), firstCard.getCardValue()))
                firstCard = (Game.Card) card;
        }
        return firstCard;
    }

    private Game.Card biggestCardInTrickAtout(ArrayList currentTrick) {
        Game.Card firstCard = ((Game.Card) currentTrick.get(0));

        for (Object card : currentTrick) {
            if (isAtout((Game.Card) card) && (!isAtout(firstCard) ||
                    isBiggerValueAtout(((Game.Card) card).getCardValue(), firstCard.getCardValue())))
                firstCard = (Game.Card) card;
        }
        return firstCard;
    }

    private boolean isBiggerValue(Game.Card.CardValue cardValue, Game.Card.CardValue value) {
        return ((int) valueCards.get(cardValue)) > ((int) valueCards.get(value));
    }

    private boolean isBiggerValueAtout(Game.Card.CardValue cardValue, Game.Card.CardValue value) {
        return ((int) valueCardsAtout.get(cardValue)) > ((int) valueCardsAtout.get(value));
    }

    private Game.Answer sendInvalidCommand(int clientPosition) {
        return Game.Answer.newBuilder()
                .setRequest("The command is invalid")
                .setCode(400)
                .setCards(getDeck(clientPosition))
                .setType(GAME)
                .build();
    }

    private Game.Answer showLastTrick(int clientPosition) {
        return Game.Answer.newBuilder()
                .setRequest("There is the last trick played :" + lastTrick.toString())
                .setCode(301)
                .setCards(getDeck(clientPosition))
                .setType(GAME)
                .build();
    }

    private Game.Answer sendHandToPlayer(int clientPosition) {
        return Game.Answer.newBuilder()
                .setRequest("There is your hand" + getDeck(clientPosition).toString())
                .setCode(300)
                .setCards(getDeck(clientPosition))
                .setType(GAME)
                .build();
    }

    public boolean isFullGame() {
        return nameClient.size() == 4 ? true : false;
    }

    public boolean partyCanBegin() {
        return AnswerToClient.partyCanBegin(nameClient);
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
        System.out.println("send invite to play");
        turnPersonToPlay = turnPersonToPlay == 3 ? 0 : turnPersonToPlay + 1;
        for (Object c : clientSocket) {
            if (i == turnPersonToPlay)
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest("Your turn, you have to play.").setCode(200).setType(GAME)
                        .setCards(getDeck(i)).build());
            ++i;
        }
    }

    public void getNextPlayerChannel(Game.Answer.Type type, String msg) {
        turn = turn == 3 ? 0 : turn + 1;
        if (type == GAME) {
            if (turn == 0) {
                endLastTrick();
            }
        }
        int i = 0;
        for (Object c : clientSocket) {
            if (i == turn)
                ((Channel) c).writeAndFlush(Game.Answer.newBuilder().setRequest("Your turn, " + msg).setCode(200).setType(type).setCards(getDeck(i)).build());
            ++i;
        }
    }

    private void endLastTrick() {
        int index;
        if (isAtout((Game.Card) currentTrick.get(0))) {
            index = currentTrick.indexOf(biggestCardInTrickAtout(currentTrick));
        } else {
            index = currentTrick.indexOf(biggestCardInTrick(currentTrick));
        }
        int posPlayer = turn;
        posPlayer -= index;
        posPlayer = posPlayer < 0 ? posPlayer + 4 : posPlayer;
        sendMessageToAllPersonInGame(nameClient.get(posPlayer) + ": won the last trick");
        if (posPlayer == 0 || posPlayer == 3)
            scoreTeam1 += numberPointOfTrick(currentTrick);
        else
            scoreTeam2 += numberPointOfTrick(currentTrick);
        if (scoreTeam1 == 700) {
            sendMessageToAllPersonInGame(nameClient.get(0) + " et " + nameClient.get(2) + " won the game with " + scoreTeam1);
            end = true;
        } else if (scoreTeam2 == 700) {
            sendMessageToAllPersonInGame(nameClient.get(1) + " et " + nameClient.get(3) + " won the game with " + scoreTeam2);
            end = true;
        }
        lastTrick = currentTrick;
        currentTrick = new ArrayList();
        if (end) {
            sendMessageToAllPersonInGame("I delete the room for now because game is over");
            for (Object channel : clientSocket) {
                ((ChannelHandlerContext) channel).close();
            }
        }
    }

    private int numberPointOfTrick(ArrayList currentTrick) {
        int score = 0;
        for (Object card : currentTrick) {
            if (isAtout((Game.Card) card)) {
                System.out.println(valueCardsAtout.get(((Game.Card) card).getCardType()));
                if (valueCardsAtout.get(((Game.Card) card).getCardType()) != null)
                    score += (int) valueCardsAtout.get(((Game.Card) card).getCardType());
            } else {
                System.out.println(valueCards.get(((Game.Card) card).getCardType()));
                if (valueCards.get(((Game.Card) card).getCardType()) != null)
                    score += (int) valueCards.get(((Game.Card) card).getCardType());
            }
        }
        return score;
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

    public Game.Answer setName(String name) {
        return AnswerToClient.setName(this, nameClient, ctxTmp, name);
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
        System.out.println("enter checkif party can run");
        if ((contract != -1 && nbTurnInactive == 4) ||
                capot || surCoinche) {
            bidding = false;
            game = true;
            System.out.println("just put game to true");
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

    private void setDeck(int pos, Game.DistributionCard deck) {
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
}
