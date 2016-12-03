import eu.epitech.jcoinche.jcoincheserver.game.CardManager;
import eu.epitech.jcoinche.jcoincheserver.game.GameManager;
import eu.epitech.jcoinche.protobuf.Game;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;

import static eu.epitech.jcoinche.protobuf.Game.Bidding.Options.HEARTS;
import static eu.epitech.jcoinche.protobuf.Game.Bidding.Options.SPADES;
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

    @BeforeClass
    public static void initGameManager() throws Exception {
        gm = new GameManager();
        gm.setAtout(HEARTS);
        cm = gm.getCardManager();
        gm.setTurn(0);
        cm.generateCardTest1();
        System.out.println("beforeClass");
    }

    @Test
    public void testFirstTurnAtoutOffAllPlayer() {
        //play atout
        testIfPlayerOneCanPlayCard(0);
        //play atout less important but has a biggest one
        testIfPlayerTwoCannotPlayCard(0);
        //play biggest atout
        testIfPlayerTwoCanPlayCard(1);
        //play biggest atout
        testIfPlayerThreeCanPlayCard(0);
        //play atout less important than prev but doesn't have better
        testIfPlayerFourCanPlayCard(0);
        gm.setAtout(SPADES);
        cm.generateCardTest2();
        gm.setCurrentTrick(new ArrayList());
        //play non atout
        testIfPlayerOneCanPlayCard(0);
        //play atout but has colour so it's not okay
        testIfPlayerTwoCannotPlayCard(2);
        //prev player didn't play so he cannot play
        testIfPlayerThreeCannotPlayCard(0);
    }

    public void testIfPlayerOneCanPlayCard(int index) {
        System.out.println("test player one");
        deck = cm.getDeckFromPosition(0);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        answer = gm.interpreteGaming(0, gameProgress);
        System.out.println(deck.getCard(0));
        assertTrue("Error first card of first player isn't accepted for first trick", answer.getCode() == 200);
        answer = gm.interpreteGaming(1, gameProgress);
        assertTrue("Error first card of first player is accepted when second player use it", answer.getCode() == 400);
        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
    }

    public void testIfPlayerTwoCanPlayCard(int index) {
        System.out.println("test player two");
        deck = cm.getDeckFromPosition(1);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        System.out.println(deck.getCard(0));
        answer = gm.interpreteGaming(1, gameProgress);
        assertTrue("Error first card of second player greater than first card of first player isn't accepted", answer.getCode() == 200);
        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
    }

    public void testIfPlayerThreeCanPlayCard(int index) {
        System.out.println("test player three");
        deck = cm.getDeckFromPosition(2);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        System.out.println(deck.getCard(0));
        answer = gm.interpreteGaming(2, gameProgress);
        assertTrue("Error first card of third player greater than first card of second player isn't accepted", answer.getCode() == 200);
        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
    }

    public void testIfPlayerTwoCannotPlayCard(int index) {
        System.out.println("test player two cannot");
        deck = cm.getDeckFromPosition(1);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        System.out.println(deck.getCard(0));
        answer = gm.interpreteGaming(1, gameProgress);
        assertTrue("Error first card of second player greater than first card of first player is accepted", answer.getCode() == 400);
    }

    public void testIfPlayerThreeCannotPlayCard(int index) {
        System.out.println("test player three cannot");
        deck = cm.getDeckFromPosition(2);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        System.out.println(deck.getCard(0));
        answer = gm.interpreteGaming(2, gameProgress);
        assertTrue("Error first card of third player greater than first card of second player is accepted", answer.getCode() == 400);
    }

    public void testIfPlayerFourCanPlayCard(int index) {
        System.out.println("test player Four");
        deck = cm.getDeckFromPosition(3);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        //first time he will play king atout after Jack which is the biggest so he cannot go up but the card has to be accept
        System.out.println(deck.getCard(0));
        answer = gm.interpreteGaming(3, gameProgress);
        assertTrue("Error first card of fourth player greater than first card of third player isn't accepted", answer.getCode() == 200);
        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
    }
}
