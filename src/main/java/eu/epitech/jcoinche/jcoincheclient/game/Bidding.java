package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.protobuf.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.BIDDING;

/**
 * Created by noboud_n on 21/11/2016.
 */
public class Bidding {

    public void sendError(String error) {
        Game.Answer answer = Game.Answer.newBuilder()
                            .setRequest(error == null ? "" : error)
                            .setCode(-1)
                            .setType(BIDDING)
                            .build();
        if (Connection.get_channel() == null) {
            System.err.println("Connection lost.");
            return;
        }
        Connection.get_channel().writeAndFlush(answer);
        try {
            Connection.get_channel().closeFuture().sync();
        } catch (Exception e) {
            System.err.println("Could not close the socket properly... exiting the client...");
            return;
        }
    }


    public boolean printCards(Game.Answer answer) {
        if (answer == null) {
            System.err.println("Cannot get the informations contained in the answer.");
            return false;
        }
        List<Game.Card> deck = Cards.sortCardsByTypeAndValue(answer.getCards());
        if (deck == null || deck.isEmpty()) {
            System.err.println("Your hand is empty");
            return false;
        }
        System.out.println("Here are your cards : ");
        for (Object card : deck) {
            String entireCard = new StringBuilder()
                            .append(((Game.Card) card).getCardValue())
                            .append(" OF ")
                            .append(((Game.Card) card).getCardType())
                            .toString();
            System.out.println(entireCard);
        }
        return true;
    }

    public boolean doesUserWantToBet(String line) {
        if (line != null && !line.isEmpty()) {
            line = line.replaceAll("\\s+","");
        }
        if (line != null && !line.isEmpty() && (line.toLowerCase().equals("y") || line.toLowerCase().equals("n"))) {
            return true;
        }
        return false;
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
                        if (doesUserWantToBet((line = in.readLine())) == true) {
                            break;
                        }
                    } catch (IOException e) {
                        System.err.println("Could not get the player's input.");
                    }
                }
                switch (line.toLowerCase()) {
                    case "y":
                        askAgain = bid();
                        break;
                    case "n":
                        System.out.println("If you do not bid, you have to choose one of these options (COINCHE, SURCOINCHE, PASS) : ");
                        Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
                        Game.Bidding.Builder bidding = Game.Bidding.newBuilder();
                        askAgain = askOtherOptions(in.readLine(), bidding);
                        if (askAgain == true) {
                            futureAnswer.setBidding(bidding)
                                    .setType(BIDDING)
                                    .build();
                            if (Connection.get_channel() == null) {
                                System.err.println("Connection lost.");
                                System.exit(84);
                            }
                            Connection.get_channel().writeAndFlush(futureAnswer);
                        }
                        break;
                }
                    i = 1;
                }
        } catch (IOException e) {
            System.err.println("Could not get the player's input.");
        } catch (Exception e) {
            sendError("QUIT");
            throw new Exception(e.getMessage());
        }
    }


    public boolean bid() throws Exception {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
            Game.Bidding.Builder bidding = Game.Bidding.newBuilder();

            System.out.println("Choose an option (x as an integer to announce the value of your contract, \"CAPOT\", \"GENERALE\") :");
            if (askContract(in.readLine(), bidding) == false) {
                return false;
            }
            System.out.println("Choose a card suit (HEARTS, SPADES, CLUBS, DIAMONDS, TA or SA) :");
            if (askCardSuit(in.readLine(), bidding) == false) {
                return false;
            }
            bidding.setCoinche(false);
            bidding.setSurcoinche(false);
            futureAnswer.setType(BIDDING)
                    .setBidding(bidding)
                    .build();
            if (Connection.get_channel() == null) {
                throw new Exception("Connection lost");
            }
            Connection.get_channel().writeAndFlush(futureAnswer);
        } catch (IOException e) {
            System.err.println("Cannot get the player's input.");
            return false;
        } catch (Exception e) {
            throw new Exception("Cannot get the player's bidding wishes.");
        }
        return true;
    }


    public boolean askContract(String line, Game.Bidding.Builder bidding) throws Exception {
        // Get the contract of the bid
        if (line != null && !line.isEmpty()) {
            line = line.replaceAll("\\s+","");
            bidding.setBid(true);
            for (Game.Bidding.Contract contract : Game.Bidding.Contract.values()) {
                if (contract.name().equals(line.toUpperCase()) && !line.toUpperCase().equals("AMOUNT")) {
                    bidding.setContract(Game.Bidding.Contract.valueOf(line.toUpperCase()));
                    bidding.setAmount(-1);
                    return true;
                }
            }
            try {
                Integer amount = Integer.parseInt(line);
                bidding.setAmount(amount);
                bidding.setContract(Game.Bidding.Contract.AMOUNT);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }


    public boolean askCardSuit(String line, Game.Bidding.Builder bidding) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // Get the card suit of the contract
        if (line != null && !line.isEmpty()) {
            line = line.replaceAll("\\s+","");
            for (Game.Bidding.Options opt : Game.Bidding.Options.values()) {
                if (opt.name().equals(line.toUpperCase())) {
                    bidding.setOption(Game.Bidding.Options.valueOf(line.toUpperCase()));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean askOtherOptions(String line, Game.Bidding.Builder bidding) throws Exception {
        bidding.setBid(false);
        // Get the other options
        if (line != null && !line.isEmpty()) {
            line = line.replaceAll("\\s+","");
            switch (line.toUpperCase()) {
                case "COINCHE":
                    bidding.setCoinche(true);
                    bidding.setSurcoinche(false);
                    bidding.setPass(false);
                    return true;
                case "SURCOINCHE":
                    bidding.setCoinche(false);
                    bidding.setSurcoinche(true);
                    bidding.setPass(false);
                    return true;
                case "PASS":
                    bidding.setCoinche(false);
                    bidding.setSurcoinche(false);
                    bidding.setPass(true);
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

}
