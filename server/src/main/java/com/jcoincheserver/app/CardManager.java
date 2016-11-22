package com.jcoincheserver.app;

import com.jcoincheserver.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Saursinet on 21/11/2016.
 */
public class CardManager {
    static ArrayList cardList = new ArrayList();

    public static boolean initCardList() {
        if (cardList.size() != 0)
            return false;
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

        Game.Card carde = Game.Card.newBuilder()
                .setCardType(Game.Card.CardType.valueOf((String) typeCardList.get(0)))
                .setCardValue(Game.Card.CardValue.valueOf((String) valueCardList.get(0)))
                .build();
        System.out.println("1 = {" + carde + "}");
        Game.Card cardet = Game.Card.newBuilder()
                .setCardType(Game.Card.CardType.HEARTS)
                .setCardValue(Game.Card.CardValue.SEVEN)
                .build();
        System.out.println("1 = {" + cardet + "}");
        for (int i = 0; i < typeCardList.size(); ++i) {
            for (int j = 0; j < valueCardList.size(); ++j) {
                Game.Card card = Game.Card.newBuilder()
                        .setCardType(Game.Card.CardType.valueOf((String) typeCardList.get(i)))
                        .setCardValue(Game.Card.CardValue.valueOf((String) valueCardList.get(j)))
                        .build();
                cardList.add(card);
                System.out.println(Game.Card.CardType.valueOf((String) typeCardList.get(i)));
                System.out.println(Game.Card.CardValue.valueOf((String) valueCardList.get(j)));
                System.out.println("at i = " + i + " et j = " + j + " ->{" + card + "}");
            }
        }
        return true;
    }

    public static void giveCardToAllPlayers(ChannelGroup clients) {
        Random rand = new Random();
        int index;
        for (Channel c: clients) {
            ArrayList tmp = new ArrayList();
            for (int i = 0; i < 8; i++) {
                index = rand.nextInt(cardList.size());
                tmp.add(cardList.get(index));
                System.out.println("I just add " + cardList.get(index));
                cardList.remove(index);
            }
            Game.DistributionCard cards = Game.DistributionCard
                    .newBuilder()
                    .addAllCard(tmp)
                    .build();
            System.out.println("player = " + cards.getAllFields() + "\nthere is " + tmp.size() + " cards");
            c.writeAndFlush(cards);
        }
    }

    public static ArrayList getCardList() {
        return cardList;
    }
}
