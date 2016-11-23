package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheclient.protobuf.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static eu.epitech.jcoinche.jcoincheclient.protobuf.Game.Answer.Type.BIDDING;

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
            System.err.println("Could not close the socket properly... exiting the client...");
            System.exit(84);
        }
    }


    public void printCards(Game.Answer answer) {
        List<Game.Card> deck = Cards.sortCardsByTypeAndValue(answer.getCards());
        System.out.println("Here are your cards : ");
        for (Object card : deck) {
            String entireCard = new StringBuilder()
                            .append(((Game.Card) card).getCardValue())
                            .append(" OF ")
                            .append(((Game.Card) card).getCardType())
                            .toString();
            System.out.println(entireCard);
        }
    }

    public void biddingProcess(Game.Answer answer) throws Exception {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            boolean askAgain = false;
            int i = 0;

            while (!askAgain) {
                line = null;
                while (line == null || line.isEmpty() || (line.toLowerCase() != "y" && line.toLowerCase() != "n")) {
                    try {
                        if (i == 0) {
                            printCards(answer);
                            System.out.println("Would you like to bet ? (y/n)");
                        } else if (i > 0) {
                            System.out.println("An error occured : you have to do something or at least PASS. Would you like to bet then ? (y/n)");
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


    public boolean bid(Game.Answer answer) throws Exception {
        try {
            Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
            Game.Bidding.Builder bidding = Game.Bidding.newBuilder();

            if (askContract(bidding) == false || askCardSuit(bidding) == false) {
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
            return true;
        }
        return false;
    }

    public boolean otherOptions(Game.Answer answer) throws Exception {
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
