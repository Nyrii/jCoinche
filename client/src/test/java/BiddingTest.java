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

    public boolean askContract(String askedContract) {
        if (askedContract != null && !askedContract.isEmpty()) {
            for (Game.Bidding.Contract contract : Game.Bidding.Contract.values()) {
                if (contract.name().equals(askedContract.toUpperCase()) && !askedContract.toUpperCase().equals("AMOUNT")) {
                    return true;
                }
            }
            try {
                Integer amount = Integer.parseInt(askedContract);
                if (amount < 80 || amount > 160) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }

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

        try {
            Bidding bidding = new Bidding();
            Game.Bidding.Builder bidBuilder = Game.Bidding.newBuilder();

            String data = "CAPOT";
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            Scanner scanner = new Scanner(System.in);
            bidding.askContract(scanner.nextLine(), bidBuilder);
        } catch (Exception e) {
            return;
        }
        // ASK CONTRACT
        returnValue = askContract("capot");
        assertEquals(true, returnValue);
        returnValue = askContract("generale");
        assertEquals(true, returnValue);
        returnValue = askContract("CAPOT");
        assertEquals(true, returnValue);
        returnValue = askContract("GENERALE");
        assertEquals(true, returnValue);
        returnValue = askContract("80");
        assertEquals(true, returnValue);
        returnValue = askContract("160");
        assertEquals(true, returnValue);
        returnValue = askContract("99");
        assertEquals(true, returnValue);
        returnValue = askContract("153");
        assertEquals(true, returnValue);
        returnValue = askContract("121");
        assertEquals(true, returnValue);
        returnValue = askContract("AMOUNT");
        assertEquals(false, returnValue);
        returnValue = askContract("AMOUNT");
        assertEquals(false, returnValue);
        returnValue = askContract("test");
        assertEquals(false, returnValue);
        returnValue = askContract("q w e r t y");
        assertEquals(false, returnValue);
        returnValue = askContract("894798798759273");
        assertEquals(false, returnValue);
        returnValue = askContract("-43982794217482718902");
        assertEquals(false, returnValue);
        returnValue = askContract("-1");
        assertEquals(false, returnValue);
        returnValue = askContract("161");
        assertEquals(false, returnValue);
        returnValue = askContract("19892");
        assertEquals(false, returnValue);
        returnValue = askContract("       ");
        assertEquals(false, returnValue);
        returnValue = askContract("");
        assertEquals(false, returnValue);
        returnValue = askContract(null);
        assertEquals(false, returnValue);

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
        returnValue = askContract("q w e r t y");
        assertEquals(false, returnValue);
        returnValue = askContract("qwertyuiop asdfghjkl zxcvbn");
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
