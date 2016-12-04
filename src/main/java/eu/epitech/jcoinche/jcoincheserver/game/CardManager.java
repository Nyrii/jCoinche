package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Saursinet on 21/11/2016.
 */
public class CardManager {
    static ArrayList cardList = new ArrayList();

    static ArrayList cardGames = new ArrayList();

    static ArrayList deckObjects = new ArrayList();

    public CardManager() {
        ArrayList typeCardList = new ArrayList();
        ArrayList valueCardList = new ArrayList();

        typeCardList.add("HEARTS");
        typeCardList.add("SPADES");
        typeCardList.add("CLUBS");
        typeCardList.add("DIAMONDS");
        valueCardList.add("SEVEN");
        valueCardList.add("EIGHT");
        valueCardList.add("NINE");
        valueCardList.add("TEN");
        valueCardList.add("JACK");
        valueCardList.add("QUEEN");
        valueCardList.add("KING");
        valueCardList.add("AS");
        for (int i = 0; i < typeCardList.size(); ++i) {
            for (int j = 0; j < valueCardList.size(); ++j) {
                Game.Card card = Game.Card.newBuilder()
                        .setCardType(Game.Card.CardType.valueOf((String) typeCardList.get(i)))
                        .setCardValue(Game.Card.CardValue.valueOf((String) valueCardList.get(j)))
                        .build();
                cardList.add(card);
            }
        }
    }

    public static void generateCardForAllPlayer() {
        Random rand = new Random();
        int index;
        if (cardGames.size() != 0) {
            cardGames.clear();
            deckObjects.clear();
        }
        for (int j = 0; j < 4; j++) {
            ArrayList tmp = new ArrayList();
            for (int i = 0; i < 8; i++) {
                index = rand.nextInt(cardList.size());
                tmp.add(cardList.get(index));
                cardList.remove(index);
            }
            cardGames.add(tmp);
        }
        for (int j = 0; j < 4; j++) {
            Game.DistributionCard cards = Game.DistributionCard
                    .newBuilder()
                    .addAllCard((ArrayList) cardGames.get(j))
                    .build();
            deckObjects.add(cards);
        }
    }

    public static void generateCardTest1() {
        if (cardGames.size() != 0) {
            cardGames.clear();
            deckObjects.clear();
        }
        ArrayList tmp = new ArrayList();
        tmp.add(cardList.get(0));
        tmp.add(cardList.get(2));
        tmp.add(cardList.get(8));
        tmp.add(cardList.get(9));
        tmp.add(cardList.get(16));
        tmp.add(cardList.get(17));
        tmp.add(cardList.get(24));
        tmp.add(cardList.get(25));
        cardGames.add(tmp);
        tmp = new ArrayList();
        tmp.add(cardList.get(1));
        tmp.add(cardList.get(3));
        tmp.add(cardList.get(10));
        tmp.add(cardList.get(11));
        tmp.add(cardList.get(18));
        tmp.add(cardList.get(19));
        tmp.add(cardList.get(26));
        tmp.add(cardList.get(27));
        cardGames.add(tmp);
        tmp = new ArrayList();
        tmp.add(cardList.get(4));
        tmp.add(cardList.get(5));
        tmp.add(cardList.get(12));
        tmp.add(cardList.get(13));
        tmp.add(cardList.get(20));
        tmp.add(cardList.get(21));
        tmp.add(cardList.get(28));
        tmp.add(cardList.get(29));
        cardGames.add(tmp);
        tmp = new ArrayList();
        tmp.add(cardList.get(6));
        tmp.add(cardList.get(7));
        tmp.add(cardList.get(14));
        tmp.add(cardList.get(15));
        tmp.add(cardList.get(22));
        tmp.add(cardList.get(23));
        tmp.add(cardList.get(30));
        tmp.add(cardList.get(31));
        cardGames.add(tmp);
        for (int j = 0; j < 4; j++) {
            Game.DistributionCard cards = Game.DistributionCard
                    .newBuilder()
                    .addAllCard((ArrayList) cardGames.get(j))
                    .build();
            deckObjects.add(cards);
        }
    }

    public static void generateCardTest2() {
        if (cardGames.size() != 0) {
            cardGames.clear();
            deckObjects.clear();
        }
        ArrayList tmp = new ArrayList();
        tmp.add(cardList.get(6));
        tmp.add(cardList.get(7));
        tmp.add(cardList.get(14));
        tmp.add(cardList.get(15));
        tmp.add(cardList.get(22));
        tmp.add(cardList.get(23));
        tmp.add(cardList.get(30));
        tmp.add(cardList.get(31));
        cardGames.add(tmp);
        tmp = new ArrayList();
        tmp.add(cardList.get(4));
        tmp.add(cardList.get(5));
        tmp.add(cardList.get(12));
        tmp.add(cardList.get(13));
        tmp.add(cardList.get(20));
        tmp.add(cardList.get(21));
        tmp.add(cardList.get(28));
        tmp.add(cardList.get(29));
        cardGames.add(tmp);
        tmp = new ArrayList();
        tmp.add(cardList.get(2));
        tmp.add(cardList.get(3));
        tmp.add(cardList.get(10));
        tmp.add(cardList.get(11));
        tmp.add(cardList.get(18));
        tmp.add(cardList.get(19));
        tmp.add(cardList.get(26));
        tmp.add(cardList.get(27));
        cardGames.add(tmp);
        tmp = new ArrayList();
        tmp.add(cardList.get(0));
        tmp.add(cardList.get(1));
        tmp.add(cardList.get(8));
        tmp.add(cardList.get(9));
        tmp.add(cardList.get(16));
        tmp.add(cardList.get(17));
        tmp.add(cardList.get(24));
        tmp.add(cardList.get(25));
        cardGames.add(tmp);
        for (int j = 0; j < 4; j++) {
            Game.DistributionCard cards = Game.DistributionCard
                    .newBuilder()
                    .addAllCard((ArrayList) cardGames.get(j))
                    .build();
            deckObjects.add(cards);
        }
    }

    public static void giveCardToAllPlayers(ArrayList clients) {
        int i = 0;
        deckObjects.clear();
        for (Object c : clients) {
            Game.DistributionCard cards = Game.DistributionCard
                    .newBuilder()
                    .addAllCard((ArrayList) cardGames.get(i))
                    .build();
            deckObjects.add(cards);
            Game.Answer answer = Game.Answer.newBuilder()
                    .setCards(cards)
                    .setType(Game.Answer.Type.NONE)
                    .setCode(200)
                    .setRequest("Cards have been distributed!")
                    .build();
            ((Channel) c).writeAndFlush(answer);
            ++i;
        }
    }

    public static Game.DistributionCard getDeckFromPosition(int pos) {
        if (deckObjects.size() > pos)
            return (Game.DistributionCard) deckObjects.get(pos);
        return Game.DistributionCard.newBuilder().build();
    }

    public static void setDeckWithPosition(int pos, Game.DistributionCard deck) {
        deckObjects.set(pos, deck);
    }

    public static ArrayList getCardGames() {
        return cardList;
    }
}
