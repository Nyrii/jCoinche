package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.jcoincheserver.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

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

    public static void giveCardToAllPlayers(ArrayList clients) {
        Random rand = new Random();
        int index;
        if (cardGames.size() != 0) {
            cardGames.clear();
            deckObjects.clear();
        }
        for (Object c: clients) {
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
            deckObjects.add(cards);
            Game.Answer answer = Game.Answer.newBuilder()
                    .setCards(cards)
                    .setType(Game.Answer.Type.SETTINGS)
                    .setCode(200)
                    .setRequest("Cards have been distributed!")
                    .build();
            ((Channel) c).writeAndFlush(answer);
            cardGames.add(tmp);
        }
    }

    public static Game.DistributionCard getDeckFromPosition(int pos) {
        return (Game.DistributionCard) deckObjects.get(pos);
    }

}
