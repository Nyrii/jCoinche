import eu.epitech.jcoinche.jcoincheserver.game.CardManager;
import eu.epitech.jcoinche.jcoincheserver.game.GameManager;
import eu.epitech.jcoinche.jcoincheserver.game.PartyManager;
import eu.epitech.jcoinche.jcoincheserver.game.Person;
import eu.epitech.jcoinche.protobuf.Game;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static eu.epitech.jcoinche.protobuf.Game.Bidding.Options.HEARTS;
import static eu.epitech.jcoinche.protobuf.Game.Bidding.Options.SA;
import static eu.epitech.jcoinche.protobuf.Game.GameProgress.Command.PLAY;
import static org.junit.Assert.assertTrue;

/**
 * Created by Saursinet on 08/12/2016.
 */
public class ScenarioTest {
    static private GameManager gm;
    static private CardManager cm;

    Game.DistributionCard deck;
    Game.GameProgress gameProgress;
    Game.Answer answer;
    static ArrayList client;
    static boolean opt;

    @BeforeClass
    public static void initGameManager() throws Exception {
        gm = new GameManager();
        gm.testMode = true;
        client = new ArrayList();
        opt = false;
    }

    @Test
    public void testTestTwoGames() {

        initFourPlayers();

        gm.setTurn(0);
        playOneParty(0);
        gm.reinitValues();
        playOneParty(1);
        gm.reinitValues();
        playOneParty(2);
        gm.reinitValues();
        playOneParty(3);

        gm.reinitAllValues();
        opt = true;
        gm.setTurn(0);
        playOneParty(0);
        gm.reinitValues();
        playOneParty(1);
        gm.reinitValues();
        playOneParty(2);
        gm.reinitValues();
        playOneParty(3);
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
        if (!opt) {
            if (index == 0)
                cm.generateCardTest1();
            else
                cm.generateCardTest2();
        } else {
            if (index == 0)
                cm.generateCardTest2();
            else
                cm.generateCardTest1();
        }
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

}
