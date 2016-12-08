import eu.epitech.jcoinche.jcoincheserver.game.CardManager;
import eu.epitech.jcoinche.jcoincheserver.game.GameManager;
import eu.epitech.jcoinche.jcoincheserver.game.PartyManager;
import eu.epitech.jcoinche.jcoincheserver.game.Person;
import eu.epitech.jcoinche.protobuf.Game;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;

import static eu.epitech.jcoinche.protobuf.Game.Bidding.Options.HEARTS;
import static eu.epitech.jcoinche.protobuf.Game.Bidding.Options.SA;
import static eu.epitech.jcoinche.protobuf.Game.GameProgress.Command.PLAY;
import static org.junit.Assert.assertTrue;

/**
 * Created by Saursinet on 01/12/2016.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GameManagerTest {
    static private GameManager gm;
    static private CardManager cm;

    Game.DistributionCard deck;
    Game.GameProgress gameProgress;
    Game.Answer answer;
    static ArrayList client;

    @BeforeClass
    public static void initGameManager() throws Exception {
        gm = new GameManager();
        gm.testMode = true;
//        gm.setAtout(HEARTS);
//        gm.setTurn(0);
        System.out.println("beforeClass");
        client = new ArrayList();
    }

    @Test
    public void testFirstTurnAtoutOffAllPlayer() {

        initFourPlayers();

        gm.setTurn(0);
        playOneParty(0);
        gm.reinitValues();
        playOneParty(1);
        gm.reinitValues();
        playOneParty(2);

//        //play atout
//        testIfPlayerOneCanPlayCard(0);
//        //play atout less important but has a biggest one
//        testIfPlayerTwoCannotPlayCard(0);
//        //play biggest atout
//        testIfPlayerTwoCanPlayCard(1);
//        //play biggest atout
//        testIfPlayerThreeCanPlayCard(0);
//        //play atout less important than prev but doesn't have better
//        testIfPlayerFourCanPlayCard(0);
//        gm.setAtout(SPADES);
//        cm.generateCardTest2();
//        gm.setCurrentTrick(new ArrayList());
//        //play non atout
//        testIfPlayerOneCanPlayCard(0);
//        //play atout but has colour so it's not okay
//        testIfPlayerTwoCannotPlayCard(2);
//        //prev player didn't play so he cannot play
//        testIfPlayerThreeCannotPlayCard(0);
    }

    private void playOneParty(int index) {
        biddingPart(index);

        int i = 0;
        while (i < 8) {
            gm.setTurn(index);
            allPlayerPlayTheirCards(index);
            PartyManager.endLastTrick();
            ++i;
        }
    }

    private void allPlayerPlayTheirCards(int index) {
        int tmp = 0;
        while (tmp < 4) {
            deck = cm.getDeckFromPosition(index);
            gameProgress = Game.GameProgress.newBuilder()
                    .setCommand(PLAY)
                    .setCard(deck.getCard(0))
                    .build();
            answer = gm.interpreteGaming(index, gameProgress);
            assertTrue("Error card of player number " + index + " isn't accepted!", answer.getCode() < 300);
            gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
            ++tmp;
            ++index;
            index %= 4;
        }
    }

    private void biddingPart(int index) {
        gm.setTurn(index);

        answer = bid((Person) client.get(index), true, 85, HEARTS);
        assertTrue("Error bidding didn't pass for player one", answer.getCode() == 200);

        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
        answer = bid((Person) client.get((index + 1) % 4), false, true);
        assertTrue("Error bidding didn't pass for player two", answer.getCode() < 300);

        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
        answer = bid((Person) client.get((index + 2) % 4), true, 100, SA);
        assertTrue("Error bidding didn't pass for player three", answer.getCode() == 200);

        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
        answer = bid((Person) client.get((index + 3) % 4), false, true);
        assertTrue("Error bidding didn't pass for player four", answer.getCode() < 300);

        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
        answer = bid((Person) client.get(index), false, true);
        assertTrue("Error bidding didn't pass for player one again", answer.getCode() < 300);

        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
        answer = bid((Person) client.get((index + 1) % 4), false, true);
        assertTrue("Error bidding didn't pass for player two again", answer.getCode() < 300);

        cm = gm.getCardManager();
        cm.generateCardTest2();
    }

    private Game.Answer bid(Person person, boolean bid, int amount, Game.Bidding.Options option) {
        return gm.interpreteBidding(person, Game.Bidding.newBuilder()
                .setBid(bid)
                .setAmount(amount)
                .setOption(option)
                .build());
    }

    private Game.Answer bid(Person person, boolean bid, boolean pass) {
        return gm.interpreteBidding(person, Game.Bidding.newBuilder()
                .setBid(bid)
                .setPass(pass)
                .build());
    }

    public void initFourPlayers() {
        gm.addClient("toto", null);
        gm.addClient("tata", null);
        gm.addClient("titi", null);
        gm.addClient("tutu", null);
        client.add(gm.getPersonByName("toto"));
        client.add(gm.getPersonByName("tata"));
        client.add(gm.getPersonByName("titi"));
        client.add(gm.getPersonByName("tutu"));
    }

//    public void testIfPlayerOneCanPlayCard(int index) {
//        System.out.println("test player one");
//        deck = cm.getDeckFromPosition(0);
//        gameProgress = Game.GameProgress.newBuilder()
//                .setCommand(PLAY)
////                .setCard(deck.getCard(index))
//                .build();
//        answer = gm.interpreteGaming(0, gameProgress);
////        System.out.println(deck.getCard(0));
////        assertTrue("Error first card of first player isn't accepted for first trick", answer.getCode() == 200);
//        answer = gm.interpreteGaming(1, gameProgress);
////        assertTrue("Error first card of first player is accepted when second player use it", answer.getCode() == 400);
//        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
//    }
//
//    public void testIfPlayerTwoCanPlayCard(int index) {
//        System.out.println("test player two");
//        deck = cm.getDeckFromPosition(1);
//        gameProgress = Game.GameProgress.newBuilder()
//                .setCommand(PLAY)
////                .setCard(deck.getCard(index))
//                .build();
////        System.out.println(deck.getCard(0));
//        answer = gm.interpreteGaming(1, gameProgress);
////        assertTrue("Error first card of second player greater than first card of first player isn't accepted", answer.getCode() == 200);
//        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
//    }
//
//    public void testIfPlayerThreeCanPlayCard(int index) {
//        System.out.println("test player three");
//        deck = cm.getDeckFromPosition(2);
//        gameProgress = Game.GameProgress.newBuilder()
//                .setCommand(PLAY)
////                .setCard(deck.getCard(index))
//                .build();
////        System.out.println(deck.getCard(0));
//        answer = gm.interpreteGaming(2, gameProgress);
////        assertTrue("Error first card of third player greater than first card of second player isn't accepted", answer.getCode() == 200);
//        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
//    }
//
//    public void testIfPlayerTwoCannotPlayCard(int index) {
//        System.out.println("test player two cannot");
//        deck = cm.getDeckFromPosition(1);
//        gameProgress = Game.GameProgress.newBuilder()
//                .setCommand(PLAY)
////                .setCard(deck.getCard(index))
//                .build();
////        System.out.println(deck.getCard(0));
//        answer = gm.interpreteGaming(1, gameProgress);
////        assertTrue("Error first card of second player greater than first card of first player is accepted", answer.getCode() == 400);
//    }
//
//    public void testIfPlayerThreeCannotPlayCard(int index) {
//        System.out.println("test player three cannot");
//        deck = cm.getDeckFromPosition(2);
//        gameProgress = Game.GameProgress.newBuilder()
//                .setCommand(PLAY)
////                .setCard(deck.getCard(index))
//                .build();
////        System.out.println(deck.getCard(0));
//        answer = gm.interpreteGaming(2, gameProgress);
////        assertTrue("Error first card of third player greater than first card of second player is accepted", answer.getCode() == 400);
//    }
//
//    public void testIfPlayerFourCanPlayCard(int index) {
//        System.out.println("test player Four");
//        deck = cm.getDeckFromPosition(3);
//        gameProgress = Game.GameProgress.newBuilder()
//                .setCommand(PLAY)
////                .setCard(deck.getCard(index))
//                .build();
//        //first time he will play king atout after Jack which is the biggest so he cannot go up but the card has to be accept
////        System.out.println(deck.getCard(0));
//        answer = gm.interpreteGaming(3, gameProgress);
////        assertTrue("Error first card of fourth player greater than first card of third player isn't accepted", answer.getCode() == 200);
//        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
//    }
}
