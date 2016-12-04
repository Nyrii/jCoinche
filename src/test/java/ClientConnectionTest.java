import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheserver.server.Server;
import org.junit.Ignore;
import org.junit.Test;

import java.net.ConnectException;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

/**
 * Created by noboud_n on 30/11/2016.
 */
public class ClientConnectionTest {

    private static Thread serverThread;
    private static Server server = null;
    Connection connection = new Connection();

    @Test
    public void testRequestHost() {
        assertEquals(true, connection.requestHost("4242"));
        assertEquals(true, connection.requestHost("4242     "));
        assertEquals(true, connection.requestHost("     4242"));
        assertEquals(true, connection.requestHost("OK"));
        assertEquals(true, connection.requestHost("q w e r t y"));
        assertEquals(true, connection.requestHost("     "));
        assertEquals(false, connection.requestHost(""));
        assertEquals(false, connection.requestHost(null));
    }

    @Test
    public void testRequestPort() {
        assertEquals(true, connection.requestPort("4242"));
        assertEquals(true, connection.requestPort("4242     "));
        assertEquals(true, connection.requestPort("     4242"));
        assertEquals(true, connection.requestPort("OK"));
        assertEquals(true, connection.requestPort("q w e r t y"));
        assertEquals(true, connection.requestPort("     "));
        assertEquals(false, connection.requestPort(""));
        assertEquals(false, connection.requestPort(null));
    }

    @Test
    public void testConnect() {
        assertEquals(true, connection.requestHost("0"));
        assertEquals(true, connection.requestPort("4242"));
        try {
            connection.connect();
        } catch (ConnectException e) {
            System.err.println(e.getMessage());
        }

        serverThread = new Thread() {
            public void run() {
                server = new Server();
                try {
                    if (!serverThread.isInterrupted()) {
                        server.launchServer();
                    }
                } catch (Exception e) {
                    serverThread.interrupt();
                    e.printStackTrace();
                }
            }
        };

        serverThread.start();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            assertEquals(true, connection.requestHost("0"));
            assertEquals(true, connection.requestPort("4242"));
            connection.connect();
        } catch (ConnectException e) {
            System.err.println(e.getMessage());
        }
        serverThread.interrupt();
    }
}
