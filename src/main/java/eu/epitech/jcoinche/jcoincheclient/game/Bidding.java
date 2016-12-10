package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.BIDDING;
import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.PLAYER;

/**
 * Created by noboud_n on 21/11/2016.
 */
public class Bidding {

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

    public int doesUserWantToBet(String line) {
        if (line != null && !line.isEmpty()) {
            line = line.replaceAll("\\s+","");
        }
        if (line != null && !line.isEmpty() && (line.toLowerCase().equals("y"))) {
            return 1;
        } else if (line != null && !line.isEmpty() && (line.toLowerCase().equals("n"))) {
            return 0;
        }
        return -1;
    }

    public void bidBeginning(boolean errorOccured, Game.Answer answer) {
        if (!errorOccured) {
            printCards(answer);
            System.out.println("Would you like to bet ? (y/n)");
        } else if (errorOccured) {
            System.out.println("An error occured : you have to do something or at least PASS. Would you like to bet then ? (y/n)");
        }
    }

    public boolean sendBiddingAction(boolean stopAsking, Game.Bidding.Builder bidding, Channel channel) throws Exception {
        Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();

        if (channel == null) {
            throw new Exception("Connection lost.");
        }
        try {
            if (stopAsking == true) {
                futureAnswer.setBidding(bidding)
                        .setType(BIDDING)
                        .build();
                channel.writeAndFlush(futureAnswer);
                return true;
            }
        } catch (Exception e) {
            throw new Exception("Could not send the non-bidding action to the server");
        }
        return false;
    }

    public int biddingProcess(Game.Answer answer, BufferedReader in, boolean errorOccured) {
        try {
            bidBeginning(errorOccured, answer);
            return doesUserWantToBet(in.readLine());
        } catch (IOException e) {
            System.err.println("Could not get the player's input.");
        }
        return -1;
    }


    public boolean setBiddingAndSend(Game.Bidding.Builder bidding, Channel channel) throws Exception {
        Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();

        if (channel == null) {
            throw new Exception("Connection lost.");
        }
        try {
            bidding.setCoinche(false);
            bidding.setSurcoinche(false);
            futureAnswer.setType(BIDDING)
                    .setBidding(bidding)
                    .build();
            channel.writeAndFlush(futureAnswer);
        } catch (Exception e) {
            throw new Exception("Could not send the bidding action to the server.");
        }
        return true;
    }

    public boolean bid(BufferedReader in, Channel channel) throws Exception {
        if (channel == null) {
            throw new Exception("Connection lost.");
        }
        try {
            Game.Bidding.Builder bidding = Game.Bidding.newBuilder();

            System.out.println("Choose an option (x as an integer to announce the value of your contract, \"CAPOT\", \"GENERALE\") :");
            if (!askContract(in.readLine(), bidding)) {
                return false;
            }
            System.out.println("Choose a card suit (HEARTS, SPADES, CLUBS, DIAMONDS, TA or SA) :");
            if (!askCardSuit(in.readLine(), bidding)) {
                return false;
            }
            setBiddingAndSend(bidding, channel);

        } catch (IOException e) {
            System.err.println("Cannot get the player's input.");
            return false;
        } catch (Exception e) {
            LeaveGame.leave(channel);
            System.err.println(e.getMessage());
            System.exit(84);
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
