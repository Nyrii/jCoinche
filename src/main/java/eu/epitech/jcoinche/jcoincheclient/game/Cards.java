package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.protobuf.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by noboud_n on 23/11/2016.
 */
public class Cards {
    public static List<Game.Card> sortCardsByTypeAndValue(Game.DistributionCard cards) {
        if (cards == null) {
            return null;
        }
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

    public static boolean printCards(Game.Answer answer) {
        if (answer == null) {
            System.err.println("Cannot get the informations contained in the answer.");
            return false;
        }
        List<Game.Card> deck = Cards.sortCardsByTypeAndValue(answer.getCards());
        if (deck == null || deck.isEmpty()) {
            System.err.println("Your hand is empty");
            return false;
        }
        System.out.println("\033[36m" + "Here are your cards :" + "\033[0m");
        for (Object card : deck) {
            String entireCard = new StringBuilder()
                    .append(((Game.Card) card).getCardValue())
                    .append(" OF ")
                    .append(((Game.Card) card).getCardType())
                    .toString();
            System.out.println("\033[36m" + entireCard + "\033[0m");
        }
        return true;
    }
}
