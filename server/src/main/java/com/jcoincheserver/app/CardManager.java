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
        for (int i = 0; i < typeCardList.size(); ++i) {
            for (int j = 0; j < valueCardList.size(); ++j) {
                Game.Card card = Game.Card.newBuilder()
                        .setCardType(Game.Card.CardType.valueOf((String) typeCardList.get(i)))
                        .setCardValue(Game.Card.CardValue.valueOf((String) valueCardList.get(j)))
                        .build();
                cardList.add(card);
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
                cardList.remove(index);
            }
            Game.DistributionCard cards = Game.DistributionCard
                    .newBuilder()
                    .addAllCard(tmp)
                    .build();
            Game.Answer answer = Game.Answer.newBuilder()
                    .setCards(cards)
                    .setType(Game.Answer.Type.BIDDING)
                    .setCode(200)
                    .setRequest("Here is your cards!")
                    .build();
            c.writeAndFlush(answer);
        }
    }

    public static ArrayList getCardList() {
        return cardList;
    }
}
