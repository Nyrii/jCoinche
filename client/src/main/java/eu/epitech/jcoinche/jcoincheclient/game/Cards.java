package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.protobuf.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by noboud_n on 23/11/2016.
 */
public class Cards {
    public static List<Game.Card> sortCardsByTypeAndValue(Game.DistributionCard cards) {
        List<Game.Card> deck = new ArrayList<Game.Card>(cards.getCardList());
        Collections.sort(deck, new Comparator<Game.Card>() {
            @Override
            public int compare(Game.Card left, Game.Card right) {
                int stringComparison = left.getCardType().toString().compareTo(right.getCardType().toString());
                if (stringComparison == 0) {
                    return (left.getCardValue().compareTo(right.getCardValue()));
                }
                return stringComparison;
            }
        });
        return deck;
    }
}
