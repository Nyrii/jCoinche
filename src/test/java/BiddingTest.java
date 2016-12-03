import eu.epitech.jcoinche.jcoincheclient.game.Bidding;
import eu.epitech.jcoinche.jcoincheclient.game.LeaveGame;
import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheserver.server.Server;
import eu.epitech.jcoinche.protobuf.Game;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by noboud_n on 28/11/2016.
 */
public class BiddingTest {

    private static Connection connection;
    private static Thread one;
    private static Server server = null;
    private static Bidding bidding = new Bidding();
    Game.Bidding.Builder bidBuilder = Game.Bidding.newBuilder();

    @BeforeClass
    public static void launchServer() {
        one = new Thread() {
            public void run() {
                server = new Server();
                try {
                    if (!one.isInterrupted()) {
                        server.launchServer();
                    }
                } catch (Exception e) {
                    one.interrupt();
                    e.printStackTrace();
                }
            }
        };

        one.start();
        LeaveGame.leave();
        connection = new Connection();
        connection.requestHost("0");
        connection.requestPort("4242");
        try {
            connection.connect();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testBiddingContract() {

        try {
            // ASK CONTRACT
            assertEquals(true, bidding.askContract("capot", bidBuilder));
            assertEquals(true, bidding.askContract("generale", bidBuilder));
            assertEquals(true, bidding.askContract("CAPOT", bidBuilder));
            assertEquals(true, bidding.askContract("GENERALE", bidBuilder));
            assertEquals(true, bidding.askContract("80", bidBuilder));
            assertEquals(true, bidding.askContract("160", bidBuilder));
            assertEquals(true, bidding.askContract("99", bidBuilder));
            assertEquals(true, bidding.askContract("153", bidBuilder));
            assertEquals(true, bidding.askContract("121", bidBuilder));
            assertEquals(true, bidding.askContract("-1", bidBuilder));
            assertEquals(true, bidding.askContract("161", bidBuilder));
            assertEquals(true, bidding.askContract("19892", bidBuilder));
            assertEquals(false, bidding.askContract("AMOUNT", bidBuilder));
            assertEquals(false, bidding.askContract("AMOUNT", bidBuilder));
            assertEquals(false, bidding.askContract("test", bidBuilder));
            assertEquals(false, bidding.askContract("q w e r t y", bidBuilder));
            assertEquals(false, bidding.askContract("894798798759273", bidBuilder));
            assertEquals(false, bidding.askContract("-43982794217482718902", bidBuilder));
            assertEquals(false, bidding.askContract("       ", bidBuilder));
            assertEquals(false, bidding.askContract("", bidBuilder));
            assertEquals(false, bidding.askContract(null, bidBuilder));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testCardSuit() {

        try {
            // ASK CARD SUIT
            assertEquals(true, bidding.askCardSuit("HEARTS", bidBuilder));
            assertEquals(true, bidding.askCardSuit("HEARTS      ", bidBuilder));
            assertEquals(true, bidding.askCardSuit("SPADES      ", bidBuilder));
            assertEquals(true, bidding.askCardSuit("     SPADES  ", bidBuilder));
            assertEquals(true, bidding.askCardSuit("     CLUBS", bidBuilder));
            assertEquals(true, bidding.askCardSuit("CLUBS", bidBuilder));
            assertEquals(true, bidding.askCardSuit("DIAMONDS", bidBuilder));
            assertEquals(true, bidding.askCardSuit("hearts", bidBuilder));
            assertEquals(true, bidding.askCardSuit("spades", bidBuilder));
            assertEquals(true, bidding.askCardSuit("clubs", bidBuilder));
            assertEquals(true, bidding.askCardSuit("diamonds", bidBuilder));
            assertEquals(false, bidding.askCardSuit("39282", bidBuilder));
            assertEquals(false, bidding.askCardSuit("(*(*&@(*@", bidBuilder));
            assertEquals(false, bidding.askCardSuit("       ", bidBuilder));
            assertEquals(false, bidding.askCardSuit("", bidBuilder));
            assertEquals(false, bidding.askCardSuit(null, bidBuilder));
            assertEquals(false, bidding.askCardSuit("q w e r t y", bidBuilder));
            assertEquals(false, bidding.askCardSuit("qwertyuiop asdfghjkl zxcvbn", bidBuilder));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testOtherOptions() {

        try {
            // ASK OTHER OPTIONS
            assertEquals(true, bidding.askOtherOptions("COINCHE", bidBuilder));
            assertEquals(true, bidding.askOtherOptions("COINCHE     ", bidBuilder));
            assertEquals(true, bidding.askOtherOptions("    SURCOINCHE     ", bidBuilder));
            assertEquals(true, bidding.askOtherOptions("SURCOINCHE", bidBuilder));
            assertEquals(true, bidding.askOtherOptions("PASS", bidBuilder));
            assertEquals(true, bidding.askOtherOptions("coinche", bidBuilder));
            assertEquals(true, bidding.askOtherOptions("surcoinche", bidBuilder));
            assertEquals(true, bidding.askOtherOptions("pass", bidBuilder));
            assertEquals(false, bidding.askOtherOptions("-19832", bidBuilder));
            assertEquals(false, bidding.askOtherOptions("39804938", bidBuilder));
            assertEquals(false, bidding.askOtherOptions("190839238324", bidBuilder));
            assertEquals(false, bidding.askOtherOptions("q w e r t y", bidBuilder));
            assertEquals(false, bidding.askOtherOptions("      ", bidBuilder));
            assertEquals(false, bidding.askOtherOptions("", bidBuilder));
            assertEquals(false, bidding.askOtherOptions(",)(*@()#)@*@!", bidBuilder));
            assertEquals(false, bidding.askOtherOptions(null, bidBuilder));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testUserBet() {
        try {
            // DOES USER WANT TO BET
            assertEquals(true, bidding.doesUserWantToBet("y"));
            assertEquals(true, bidding.doesUserWantToBet("Y"));
            assertEquals(true, bidding.doesUserWantToBet("n"));
            assertEquals(true, bidding.doesUserWantToBet("N"));
            assertEquals(false, bidding.doesUserWantToBet(null));
            assertEquals(false, bidding.doesUserWantToBet(""));
            assertEquals(false, bidding.doesUserWantToBet("        "));
            assertEquals(true, bidding.doesUserWantToBet("        y"));
            assertEquals(true, bidding.doesUserWantToBet("        n"));
            assertEquals(true, bidding.doesUserWantToBet("        y   "));
            assertEquals(true, bidding.doesUserWantToBet("        n    "));
            assertEquals(true, bidding.doesUserWantToBet("        Y    "));
            assertEquals(true, bidding.doesUserWantToBet("        N    "));
            assertEquals(true, bidding.doesUserWantToBet("        Y"));
            assertEquals(true, bidding.doesUserWantToBet("        N"));
            assertEquals(true, bidding.doesUserWantToBet("Y    "));
            assertEquals(true, bidding.doesUserWantToBet("N    "));
            assertEquals(false, bidding.doesUserWantToBet("fdiojfij 32 n"));
            assertEquals(false, bidding.doesUserWantToBet("@(*$)(@*"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testBidBeginning() {
        // PRINT CARDS
        ArrayList cardList = new ArrayList();
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
        ArrayList tmp = new ArrayList();
        Random rand = new Random();
        int index;
        for (int i = 0; i < 8; i++) {
            index = rand.nextInt(cardList.size());
            tmp.add(cardList.get(index));
            cardList.remove(index);
        }
        Game.DistributionCard cards = Game.DistributionCard.newBuilder().addAllCard(tmp).build();
        Game.Answer answer = Game.Answer.newBuilder().setCards(cards).build();
        bidding.bidBeginning(true, answer);
        bidding.bidBeginning(false, answer);
        bidding.bidBeginning(true, null);
        bidding.bidBeginning(false, answer);
        assertEquals(false, bidding.printCards(null));
        assertEquals(true, bidding.printCards(answer));
        tmp.clear();
        cards = Game.DistributionCard.newBuilder().addAllCard(tmp).build();
        answer = Game.Answer.newBuilder().setCards(cards).build();
        assertEquals(false, bidding.printCards(answer));
    }

    @Test
    public void testLeaveGame() {
        LeaveGame.leave();
    }

}
