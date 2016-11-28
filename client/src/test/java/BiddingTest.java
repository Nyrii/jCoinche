import eu.epitech.jcoinche.jcoincheclient.protobuf.Game;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

/**
 * Created by noboud_n on 28/11/2016.
 */
public class BiddingTest {

    @Test
    public boolean askContract() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = null;

        // Get the contract of the bid
        try {
            System.out.println("Choose an option (x as an integer to announce the value of your contract, \"CAPOT\", \"GENERALE\") :");
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
//        assertEquals(line, );
        if (line != null && !line.isEmpty()) {
            for (Game.Bidding.Contract contract : Game.Bidding.Contract.values()) {
                if (contract.name().equals(line.toUpperCase()) && !line.toUpperCase().equals("AMOUNT")) {
                    return true;
                }
            }
            try {
                Integer amount = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }

}
