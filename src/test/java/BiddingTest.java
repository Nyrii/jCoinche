import eu.epitech.jcoinche.jcoincheclient.game.Bidding;
import eu.epitech.jcoinche.jcoincheclient.game.Cards;
import eu.epitech.jcoinche.jcoincheclient.game.LeaveGame;
import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheserver.server.Server;
import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.Channel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Random;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.BIDDING;
import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

/**
 * Created by noboud_n on 28/11/2016.
 */
public class BiddingTest {

    private static Connection connection = null;
    private static Thread one;
    private static Server server = null;
    private static Bidding bidding = new Bidding();
    private Game.Bidding.Builder bidBuilder = Game.Bidding.newBuilder();

    @BeforeClass
    public static void launchServer() {
        one = new Thread() {
            public void run() {
                server = new Server();
                try {
                    if (one != null) {
                        server.launchServer();
                    }
                } catch (Exception e) {
                    if (one != null && !one.isInterrupted())
                        one.interrupt();
                }
            }
        };

        one.start();
        LeaveGame.leave(null);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            if (one != null && !one.isInterrupted()) {
                one.interrupt();
            }
            connection = null;
            return;
        }
        connection = new Connection();
        connection.requestHost("0");
        connection.requestPort("4242");
        try {
            connection.connect();

            BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
            Mockito.when(bufferedReader.readLine()).thenReturn("90", "ta");
            assertEquals(true, bidding.bid(bufferedReader, connection.get_channel()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            connection = null;
            return;
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
            assertEquals(1, bidding.doesUserWantToBet("y"));
            assertEquals(1, bidding.doesUserWantToBet("Y"));
            assertEquals(0, bidding.doesUserWantToBet("n"));
            assertEquals(0, bidding.doesUserWantToBet("N"));
            assertEquals(-1, bidding.doesUserWantToBet(null));
            assertEquals(-1, bidding.doesUserWantToBet(""));
            assertEquals(-1, bidding.doesUserWantToBet("        "));
            assertEquals(1, bidding.doesUserWantToBet("        y"));
            assertEquals(0, bidding.doesUserWantToBet("        n"));
            assertEquals(1, bidding.doesUserWantToBet("        y   "));
            assertEquals(0, bidding.doesUserWantToBet("        n    "));
            assertEquals(1, bidding.doesUserWantToBet("        Y    "));
            assertEquals(0, bidding.doesUserWantToBet("        N    "));
            assertEquals(1, bidding.doesUserWantToBet("        Y"));
            assertEquals(0, bidding.doesUserWantToBet("        N"));
            assertEquals(1, bidding.doesUserWantToBet("Y    "));
            assertEquals(0, bidding.doesUserWantToBet("N    "));
            assertEquals(-1, bidding.doesUserWantToBet("fdiojfij 32 n"));
            assertEquals(-1, bidding.doesUserWantToBet("@(*$)(@*"));
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
        assertEquals(false, Cards.printCards(null));
        assertEquals(true, Cards.printCards(answer));
        tmp.clear();
        cards = Game.DistributionCard.newBuilder().addAllCard(tmp).build();
        answer = Game.Answer.newBuilder().setCards(cards).build();
        assertEquals(false, Cards.printCards(answer));
    }

    @Test
    public void testSendBiddingAction() {
        if (connection == null)
            return;
        try {
            assertEquals(true, bidding.sendBiddingAction(true, bidBuilder, connection.get_channel()));
            assertEquals(false, bidding.sendBiddingAction(false, bidBuilder, connection.get_channel()));
            assertEquals(false, bidding.sendBiddingAction(true, null, connection.get_channel()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            assertEquals(false, bidding.sendBiddingAction(true, bidBuilder, null));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testSetBiddingAndSend() {
        if (connection == null)
            return;
        try {
            assertEquals(true, bidding.setBiddingAndSend(bidBuilder, connection.get_channel()));
            assertEquals(false, bidding.setBiddingAndSend(null, connection.get_channel()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            assertEquals(false, bidding.setBiddingAndSend(bidBuilder, null));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testBiddingProcess() {
        Game.Answer answer = Game.Answer.newBuilder().setType(BIDDING).build();
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        try {
            Mockito.when(bufferedReader.readLine()).thenReturn("n", "PASS");
            assertEquals(0, bidding.biddingProcess(answer, bufferedReader, true));
            Mockito.when(bufferedReader.readLine()).thenReturn("y");
            assertEquals(1, bidding.biddingProcess(answer, bufferedReader, false));
            Mockito.when(bufferedReader.readLine()).thenReturn("test");
            assertEquals(-1, bidding.biddingProcess(answer, bufferedReader, false));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
    }

    @Test
    public void testBid() {
        if (connection == null)
            return;
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        try {
            Mockito.when(bufferedReader.readLine()).thenReturn("90", "ta");
            assertEquals(true, bidding.bid(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn("capot", "test");
            assertEquals(false, bidding.bid(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn("test", "hearts");
            assertEquals(false, bidding.bid(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn("hearts", "test");
            assertEquals(false, bidding.bid(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn("test", "test");
            assertEquals(false, bidding.bid(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn("CAPOT", "SPADES");
            assertEquals(true, bidding.bid(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn("89", "DIAMONDS");
            assertEquals(true, bidding.bid(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn("110", "di a mo nds    ");
            assertEquals(true, bidding.bid(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn(null);
            assertEquals(false, bidding.bid(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn("generale", "CLUBS");
            assertEquals(false, bidding.bid(bufferedReader, null));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
    }

    @Test
    public void testLeaveGame() {
        if (connection == null)
            return;
        LeaveGame.leave(connection.get_channel());
    }

    @AfterClass
    public static void quitServer() {
        if (one != null && !one.interrupted())
            one.interrupt();
        System.out.println("Interruption of server in Bidding");
    }

}
