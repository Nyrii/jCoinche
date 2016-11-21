package com.jcoincheserver.app;

import com.jcoincheserver.protobuf.Game;

import java.util.ArrayList;

/**
 * Created by Saursinet on 21/11/2016.
 */
public class CardManager {
    static ArrayList cardList = new ArrayList();

    static ArrayList typeCardList = new ArrayList();
    static ArrayList valueCardList = new ArrayList();

    public static void initCardList() {
        if (cardList.size() != 0)
            return ;
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
        System.out.println("number of cards = " + cardList.size());
    }
}
