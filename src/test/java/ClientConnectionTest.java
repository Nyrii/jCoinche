import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheserver.server.Server;
import org.junit.AfterClass;
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
    private static Connection connection = new Connection();

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
            connection = null;
            return;
        }

        serverThread = new Thread() {
            public void run() {
                server = new Server();
                try {
                    if (serverThread != null) {
                        server.launchServer();
                    }
                } catch (Exception e) {
                    if (serverThread != null && !serverThread.isInterrupted())
                        serverThread.interrupt();
                }
            }
        };

//        serverThread.start();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            if (serverThread != null && !serverThread.isInterrupted()) {
                serverThread.interrupt();
            }
            connection = null;
            return;
        }

        try {
            assertEquals(true, connection.requestHost("0"));
            assertEquals(true, connection.requestPort("4242"));
            connection.connect();
        } catch (ConnectException e) {
            System.err.println(e.getMessage());
        }
    }

    @AfterClass
    public static void quitServer() {
        if (serverThread != null && !serverThread.interrupted())
            serverThread.interrupt();
        System.out.println("Interruption of server in ClientConnectionTest");
    }
}
