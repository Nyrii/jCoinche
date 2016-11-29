import eu.epitech.jcoinche.jcoincheclient.game.Bidding;
import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheclient.protobuf.Game;
import org.junit.Test;

import java.io.*;
import java.util.Scanner;

import static eu.epitech.jcoinche.jcoincheclient.protobuf.Game.Answer.Type.BIDDING;
import static org.junit.Assert.*;

/**
 * Created by noboud_n on 28/11/2016.
 */
public class BiddingTest {

    public boolean askCardSuit(String cardSuit) {
        if (cardSuit != null && !cardSuit.isEmpty()) {
            for (Game.Bidding.Options opt : Game.Bidding.Options.values()) {
                if (opt.name().equals(cardSuit.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean askOtherOptions(String option) {
        if (option != null && !option.isEmpty()) {
            switch (option.toUpperCase()) {
                case "COINCHE":
                    return true;
                case "SURCOINCHE":
                    return true;
                case "PASS":
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    @Test
    public void testBiddingContract() {
        boolean returnValue;

//        try {
            Bidding bidding = new Bidding();
            Game.Bidding.Builder bidBuilder = Game.Bidding.newBuilder();

//            String data = "CAPOT";
//            System.setIn(new ByteArrayInputStream(data.getBytes()));
//            Scanner scanner = new Scanner(System.in);
//            bidding.askContract(scanner.nextLine(), bidBuilder);
//        } catch (Exception e) {
//            return;
//        }

        // ASK CONTRACT
        try {
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
            e.printStackTrace();
        }

        // ASK CARD SUIT
        returnValue = askCardSuit("HEARTS");
        assertEquals(true, returnValue);
        returnValue = askCardSuit("SPADES");
        assertEquals(true, returnValue);
        returnValue = askCardSuit("CLUBS");
        assertEquals(true, returnValue);
        returnValue = askCardSuit("DIAMONDS");
        assertEquals(true, returnValue);
        returnValue = askCardSuit("hearts");
        assertEquals(true, returnValue);
        returnValue = askCardSuit("spades");
        assertEquals(true, returnValue);
        returnValue = askCardSuit("clubs");
        assertEquals(true, returnValue);
        returnValue = askCardSuit("diamonds");
        assertEquals(true, returnValue);
        returnValue = askCardSuit("39282");
        assertEquals(false, returnValue);
        returnValue = askCardSuit("(*(*&@(*@");
        assertEquals(false, returnValue);
        returnValue = askCardSuit("       ");
        assertEquals(false, returnValue);
        returnValue = askCardSuit("");
        assertEquals(false, returnValue);
        returnValue = askCardSuit(null);
        assertEquals(false, returnValue);
        returnValue = askCardSuit("q w e r t y");
        assertEquals(false, returnValue);
        returnValue = askCardSuit("qwertyuiop asdfghjkl zxcvbn");
        assertEquals(false, returnValue);

        // ASK OTHER OPTIONS
        returnValue = askOtherOptions("COINCHE");
        assertEquals(true, returnValue);
        returnValue = askOtherOptions("SURCOINCHE");
        assertEquals(true, returnValue);
        returnValue = askOtherOptions("PASS");
        assertEquals(true, returnValue);
        returnValue = askOtherOptions("coinche");
        assertEquals(true, returnValue);
        returnValue = askOtherOptions("surcoinche");
        assertEquals(true, returnValue);
        returnValue = askOtherOptions("pass");
        assertEquals(true, returnValue);
        returnValue = askOtherOptions("-19832");
        assertEquals(false, returnValue);
        returnValue = askOtherOptions("39804938");
        assertEquals(false, returnValue);
        returnValue = askOtherOptions("190839238324");
        assertEquals(false, returnValue);
        returnValue = askOtherOptions("q w e r t y");
        assertEquals(false, returnValue);
        returnValue = askOtherOptions("      ");
        assertEquals(false, returnValue);
        returnValue = askOtherOptions("");
        assertEquals(false, returnValue);
        returnValue = askOtherOptions(")(*@()#)@*@!");
        assertEquals(false, returnValue);
        returnValue = askOtherOptions(null);
        assertEquals(false, returnValue);
    }

}
