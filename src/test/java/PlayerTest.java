import eu.epitech.jcoinche.jcoincheclient.game.Player;
import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheserver.server.Server;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

/**
 * Created by noboud_n on 04/12/2016.
 */
public class PlayerTest {

    private static Thread serverThread;
    private static Server server = null;
    private static Connection connection = new Connection();
    private Player player = new Player();

    @BeforeClass
    public static void connectToServer() {
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

        serverThread.start();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            if (serverThread != null && !serverThread.isInterrupted()) {
                serverThread.interrupt();
            }
            connection = null;
            return;
        }
        assertEquals(true, connection.requestHost("0"));
        assertEquals(true, connection.requestPort("4242"));
        try {
            connection.connect();
        } catch (ConnectException e) {
            System.err.println(e.getMessage());
            connection = null;
        }
    }

    @Test
    public void testAskInformations() {
        if (connection == null) {
            return;
        }
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        Player player = new Player();
        try {
            Mockito.when(bufferedReader.readLine()).thenReturn("Louis XIV");
            assertEquals(true, player.askInformations(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn(null);
            assertEquals(false, player.askInformations(bufferedReader, connection.get_channel()));
            Mockito.when(bufferedReader.readLine()).thenReturn("Ash");
            assertEquals(false, player.askInformations(null, connection.get_channel()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            Mockito.when(bufferedReader.readLine()).thenReturn("Ash");
            assertEquals(false, player.askInformations(bufferedReader, null));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testSendName() {
        if (connection == null) {
            return;
        }
        try {
            assertEquals(true, player.sendName("Nykx", connection.get_channel()));
            assertEquals(true, player.sendName("", connection.get_channel()));
            assertEquals(false, player.sendName(null, connection.get_channel()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            assertEquals(false, player.sendName("Test", null));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
