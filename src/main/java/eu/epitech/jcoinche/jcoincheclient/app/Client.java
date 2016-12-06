package eu.epitech.jcoinche.jcoincheclient.app;

import eu.epitech.jcoinche.jcoincheclient.game.*;
import eu.epitech.jcoinche.jcoincheclient.game.Process;
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
        } catch (ConnectException e) {
            System.err.println(e.getMessage());
            System.exit(84);
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
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    Player player = new Player();
                    Bidding bidding = new Bidding();
                    Process process = new Process();

                    try {
                        switch (answer.getType()) {

                            case PLAYER:
                                while (!player.askInformations(in, Connection.get_channel()));
                                break;

                            case BIDDING:
                                boolean isBidEffective = false;
                                boolean errorOccured = false;
                                while (!isBidEffective) {
                                    switch (bidding.biddingProcess(answer, in, errorOccured)) {
                                        case 1:
                                            isBidEffective = bidding.bid(in, Connection.get_channel());
                                            break;
                                        case 0:
                                            System.out.println("If you do not bid, you have to choose one of these options (COINCHE, SURCOINCHE, PASS) : ");
                                            Game.Bidding.Builder bidBuilder = Game.Bidding.newBuilder();
                                            isBidEffective = bidding.sendBiddingAction(bidding.askOtherOptions(in.readLine(), bidBuilder), bidBuilder, Connection.get_channel());
                                            break;
                                        case -1:
                                            isBidEffective = false;
                                            break;
                                    }
                                    errorOccured = !isBidEffective;
                                }
                                break;

                            case GAME:
                                process.request(in, connection.get_channel());
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        LeaveGame.leave();
                        System.exit(84);
                    }
                }
                if (answer != null && answer.equals(SaveObject.get_answer())) {
                    SaveObject.set_answer(null);
                }
            }
        }, 1000, 1000);
    }
}
