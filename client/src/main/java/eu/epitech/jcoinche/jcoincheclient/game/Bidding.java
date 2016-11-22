package eu.epitech.jcoinche.jcoincheclient.game;

import com.jcoincheclient.protobuf.Game;
import com.jcoincheclient.protobuf.Game.Answer;
import eu.epitech.jcoinche.jcoincheclient.network.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.jcoincheclient.protobuf.Game.Answer.Type.BIDDING;

/**
 * Created by noboud_n on 21/11/2016.
 */
public class Bidding {

    public void sendError(String error) {
        Game.Answer answer = Game.Answer.newBuilder()
                            .setRequest(error)
                            .setCode(-1)
                            .setType(BIDDING)
                            .build();
        Connection.get_channel().writeAndFlush(answer);
        try {
            Connection.get_channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void biddingProcess(Answer answer) throws Exception {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            boolean askAgain = false;
            Integer i = 0;

            while (!askAgain) {
                line = null;
                while (line == null || line.isEmpty() || (line.toLowerCase() != "y" && line.toLowerCase() != "n")) {
                    try {
                        if (i == 0) {
                            System.out.println("Would you like to bet ? (y/n) ");
                        } else if (i > 0) {
                            System.out.println("An error occured : you have to do something or at least PASS. Would you like to bet then ? (y/n) ");
                        }
                        line = in.readLine();
                        if (line != null && !line.isEmpty() && (line.toLowerCase().equals("y") || line.toLowerCase().equals("n"))) {
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendError("QUIT");
                        throw new Exception("System error : Could not get the input.");
                    }
                }

                switch (line.toLowerCase()) {
                    case "y":
                        askAgain = bid(answer);
                        break;
                    case "n":
                        askAgain = otherOptions(answer);
                        break;
                }
                i = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError("QUIT");
            throw new Exception("Cannot get the player's informations.");
        }
    }


    public boolean bid(Answer answer) throws Exception {
        try {
            Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
            Game.Bidding.Builder bidding = Game.Bidding.newBuilder();

            if (askContract(bidding) == false || askCardSuit(bidding)) {
                return false;
            }
            bidding.setCoinche(false);
            bidding.setSurcoinche(false);
            futureAnswer.setType(BIDDING)
                        .setBidding(bidding)
                        .build();
            Connection.get_channel().writeAndFlush(futureAnswer);
            System.out.println(bidding);
        } catch (Exception e) {
            e.printStackTrace();
            sendError("QUIT");
            throw new Exception("Cannot get the player's bidding wishes.");
        }
        return true;
    }


    public boolean askContract(Game.Bidding.Builder bidding) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = null;

        // Get the contract of the bid
        try {
            System.out.println("Choose an option (x as an integer to announce the value of your contract, \"CAPOT\", \"GENERALE\") :");
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            sendError("QUIT");
            throw new Exception("System error : Could not get the input.");
        }
        if (line != null && !line.isEmpty()) {
            bidding.setBid(true);
            if (line.toUpperCase().equals("CAPOT")) {
                bidding.setContract(Game.Bidding.Contract.CAPOT);
                bidding.setAmount(-1);
            } else if (line.toUpperCase().equals("GENERALE")) {
                bidding.setContract(Game.Bidding.Contract.GENERALE);
                bidding.setAmount(-1);
            } else {
                try {
                    Integer amount = Integer.parseInt(line);
                    bidding.setAmount(amount);
                    bidding.setContract(Game.Bidding.Contract.AMOUNT);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public boolean askCardSuit(Game.Bidding.Builder bidding) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = null;

        // Get the card suit of the contract
        try {
            System.out.println("Choose a card suit (HEARTS, SPADES, CLUBS, DIAMONDS) :");
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            sendError("QUIT");
            throw new Exception("System error : Could not get the input.");
        }
        if (line != null && !line.isEmpty()) {
            switch (line.toUpperCase()) {
                case "HEARTS":
                    bidding.setOption(Game.Bidding.Options.HEARTS);
                    break;
                case "SPADES":
                    bidding.setOption(Game.Bidding.Options.SPADES);
                    break;
                case "CLUBS":
                    bidding.setOption(Game.Bidding.Options.CLUBS);
                    break;
                case "DIAMONDS":
                    bidding.setOption(Game.Bidding.Options.DIAMONDS);
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public boolean otherOptions(Answer answer) throws Exception {
        Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
        Game.Bidding.Builder bidding = Game.Bidding.newBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = null;

        bidding.setBid(false);

        // Get the other options
        try {
            System.out.println("If you do not bid, you have to choose one of these options (COINCHE, SURCOINCHE, PASS) : ");
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            sendError("QUIT");
            throw new Exception("System error : Could not get the input.");
        }
        if (line != null && !line.isEmpty()) {
            switch (line.toUpperCase()) {
                case "COINCHE":
                    bidding.setCoinche(true);
                    bidding.setSurcoinche(false);
                    bidding.setPass(false);
                    break;
                case "SURCOINCHE":
                    bidding.setCoinche(false);
                    bidding.setSurcoinche(true);
                    bidding.setPass(false);
                    break;
                case "PASS":
                    bidding.setCoinche(false);
                    bidding.setSurcoinche(false);
                    bidding.setPass(true);
                    break;
                default:
                    return false;
            }
        }
        futureAnswer.setBidding(bidding)
                    .setType(BIDDING)
                    .build();
        Connection.get_channel().writeAndFlush(futureAnswer);
        return true;
    }

}
