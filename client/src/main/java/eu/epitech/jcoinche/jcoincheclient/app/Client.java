package eu.epitech.jcoinche.jcoincheclient.app;

import eu.epitech.jcoinche.jcoincheclient.game.Bidding;
import eu.epitech.jcoinche.jcoincheclient.game.GameProcedure;
import eu.epitech.jcoinche.jcoincheclient.game.Player;
import eu.epitech.jcoinche.jcoincheclient.game.SaveObject;
import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheclient.protobuf.Game;

import java.net.ConnectException;
import java.util.TimerTask;

/**
 * Created by noboud_n on 16/11/2016.
 */


public class Client {
    public static void main(String[] args) {
        Connection connection = new Connection();
        try {
            connection.requestHostAndPort();
            System.out.println("Waiting for the server's answer...");
            connection.connect();
        } catch (ConnectException e) {
            System.err.println(e.getMessage());
            System.exit(84);
        }

        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                Game.Answer answer = SaveObject.get_answer();
                if (answer != null) {
                    Player player = new Player();
                    Bidding bidding = new Bidding();
                    GameProcedure procedure = new GameProcedure();

                    try {
                        switch (answer.getType()) {

                            case PLAYER:
                                player.askInformations();
                                break;

                            case BIDDING:
                                bidding.biddingProcess(answer);
                                break;

                            case GAME:
                                System.out.println("You can use the following commands : NAME, MSG, PLAY, DECK, LAST and QUIT. Please check the documentation for more informations.");
                                procedure.request();
                                break;

                            case STANDBY:
                                System.out.println("Please, wait for your turn.");
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(84);
                    }
                }
                SaveObject.set_answer(null);
            }
        }, 500, 500);
    }
}
