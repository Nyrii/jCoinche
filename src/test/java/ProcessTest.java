import eu.epitech.jcoinche.jcoincheclient.game.LeaveGame;
import eu.epitech.jcoinche.jcoincheclient.game.SaveObject;
import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheserver.server.Server;
import eu.epitech.jcoinche.jcoincheclient.game.Process;
import eu.epitech.jcoinche.protobuf.Game;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;

import static java.lang.Thread.sleep;

/**
 * Created by noboud_n on 05/12/2016.
 */
public class ProcessTest {

    private static Connection connection = null;
    private static Thread thread;
    private static Server server = null;

    @BeforeClass
    public static void launchServer() {
        thread = new Thread() {
            public void run() {
                server = new Server();
                try {
                    if (thread != null) {
                        server.launchServer();
                    }
                } catch (Exception e) {
                    if (thread != null && !thread.isInterrupted())
                        thread.interrupt();
                }
            }
        };

        thread.start();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            if (thread != null && !thread.isInterrupted()) {
                thread.interrupt();
            }
            connection = null;
            return;
        }
        connection = new Connection();
        connection.requestHost("0");
        connection.requestPort("4242");
        try {
            connection.connect();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            connection = null;
            return;
        }
    }

    @Test
    public void testRequest() {
        Process process = new Process();
        if (connection == null)
            return;
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        try {
            Mockito.when(bufferedReader.readLine()).thenReturn("msg", "Ceci est un test qui ne passera pas");
            process.request(bufferedReader, connection.get_channel());
            Game.Answer answer = Game.Answer.newBuilder().setType(Game.Answer.Type.GAME).build();
            SaveObject.set_answer(answer);
            Mockito.when(bufferedReader.readLine()).thenReturn("msg", "Ceci est un message");
            process.request(bufferedReader, connection.get_channel());
            Mockito.when(bufferedReader.readLine()).thenReturn("play", "seven", "HEaRTs");
            process.request(bufferedReader, connection.get_channel());
            Mockito.when(bufferedReader.readLine()).thenReturn("play", "seven k k k k");
            process.request(bufferedReader, connection.get_channel());
            Mockito.when(bufferedReader.readLine()).thenReturn("argument invalide", "qwerty");
            process.request(bufferedReader, connection.get_channel());
            Mockito.when(bufferedReader.readLine()).thenReturn("", "qwerty");
            process.request(bufferedReader, connection.get_channel());
            Mockito.when(bufferedReader.readLine()).thenReturn("", null);
            process.request(bufferedReader, connection.get_channel());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @AfterClass
    public static void quitServer() {
        if (thread != null && !thread.interrupted())
            thread.interrupt();
        System.out.println("Interruption of server in Process");
    }

}
