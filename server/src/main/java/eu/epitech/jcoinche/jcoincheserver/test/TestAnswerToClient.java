package eu.epitech.jcoinche.jcoincheserver.test;

import eu.epitech.jcoinche.jcoincheserver.game.AnswerToClient;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Saursinet on 28/11/2016.
 */
public class TestAnswerToClient extends AnswerToClient {
    @Test
    public void partyCanBeginTest() {
        boolean var = partyCanBegin(null);
        System.out.println("test partyCanBegin with null value...");
        assertEquals(false, var);
        System.out.println("OK");

        System.out.println("test partyCanBegin with empty list...");
        ArrayList list = new ArrayList();
        var = partyCanBegin(list);
        assertEquals(false, var);
        System.out.println("OK");

        System.out.println("test partyCanBegin with not 4 people in it...");
        list.add("toto");
        list.add("tata");
        list.add("titi");
        var = partyCanBegin(list);
        assertEquals(false, var);
        System.out.println("OK");

        System.out.println("test partyCanBegin with 4 people in it but one have wrong name...");
        list.add("0xtotomangedupain");
        var = partyCanBegin(list);
        assertEquals(false, var);
        System.out.println("OK");

        System.out.println("test partyCanBegin with 4 people in it with good names...");
        list.set(list.indexOf("0xtotomangedupain"), "tutu");
        var = partyCanBegin(list);
        assertEquals(true, var);
        System.out.println("OK");

        System.out.println("test partyCanBegin with more than 4 people in it with good names...");
        list.add("heytoi");
        var = partyCanBegin(list);
        assertEquals(false, var);
        System.out.println("OK");
    }

    @Test
    public void nameAlreadyInUseTest() {
        System.out.println("test nameAlreadyInUse with list = null and good name...");
        boolean var;
        var = nameAlreadyInUse(null, "toto");
        assertEquals(false, var);
        System.out.println("OK");

        System.out.println("test nameAlreadyInUse with list empty and good name...");
        ArrayList list = new ArrayList();
        var = nameAlreadyInUse(list, "toto");
        assertEquals(false, var);
        System.out.println("OK");

        System.out.println("test nameAlreadyInUse with list non empty and name different...");
        list.add("toto");
        var = nameAlreadyInUse(list, "plage");
        assertEquals(false, var);
        System.out.println("OK");

        System.out.println("test nameAlreadyInUse with list non empty and name in list...");
        list.add("tata");
        list.add("titi");
        var = nameAlreadyInUse(list, "titi");
        assertEquals(true, var);
        System.out.println("OK");
    }
}
