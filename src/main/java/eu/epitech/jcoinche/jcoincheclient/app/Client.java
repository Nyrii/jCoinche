package eu.epitech.jcoinche.jcoincheclient.app;

import eu.epitech.jcoinche.jcoincheclient.game.Bidding;
import eu.epitech.jcoinche.jcoincheclient.game.GameProcedure;
import eu.epitech.jcoinche.jcoincheclient.game.Player;
import eu.epitech.jcoinche.jcoincheclient.game.SaveObject;
import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.protobuf.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.TimerTask;

/**
 * Created by noboud_n on 16/11/2016.
 */


public class Client {
    public static void main(String[] args) {
        Connection connection = new Connection();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean isValid = false;

        try {
            while (!isValid) {
                System.out.println("Please type the server's host :");
                if ((isValid = connection.requestHost(in.readLine())) == false)
                    System.err.println("An error occured, please type a valid host :");
            }
            isValid = false;
            while (!isValid) {
                System.out.println("Please type the server's port :");
                if ((isValid = connection.requestPort(in.readLine())) == false)
                    System.err.println("An error occured, please type a valid port :");
            }
            System.out.println("Waiting for the server's answer...");
            connection.connect();
        } catch (IOException e) {
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
                                while (!player.askInformations());
                                break;

                            case BIDDING:
                                bidding.biddingProcess(answer);
                                break;

                            case GAME:
                                System.out.println("You can use the following commands : NAME, MSG, PLAY, DECK, LAST and QUIT. Please check the documentation for more informations.");
                                procedure.request();
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(84);
                    }
                }
                if (answer != null && answer.equals(SaveObject.get_answer())) {
                    SaveObject.set_answer(null);
                }
            }
        }, 500, 500);
    }
}
