import eu.epitech.jcoinche.jcoincheclient.game.Cards;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by noboud_n on 04/12/2016.
 */
public class CardsSortTest {
    @Test
    public void testSortingNullCards() {
        Cards sort = new Cards();
        assertEquals(null, sort.sortCardsByTypeAndValue(null));
    }
}
