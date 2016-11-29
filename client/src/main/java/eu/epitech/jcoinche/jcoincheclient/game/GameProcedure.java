package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheclient.protobuf.Game;
import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by noboud_n on 28/11/2016.
 */
public class GameProcedure {

    interface PlayAction {
        void play(String command);
    }

    private PlayAction[] playActions = new PlayAction[] {
            new PlayAction() { public void play(String command) { sendMessage(command); } },
            new PlayAction() { public void play(String command) { sendMessage(command); } },
            new PlayAction() { public void play(String command) { playCard(command); } },
    };

    public void play(int index, String command) {
        playActions[index].play(command);
    }

    public boolean sendRequest(String command, List<String> arguments) {
        ChannelFuture lastWriteFuture = null;

        try {
            Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
            futureAnswer.setType(Game.Answer.Type.GAME)
                    .setRequest(command)
                    .addAllArguments(arguments)
                    .setCode(100)
                    .build();
            lastWriteFuture = Connection.get_channel().writeAndFlush(futureAnswer);
            if (lastWriteFuture != null) {
                try {
                    lastWriteFuture.sync();
                } catch (Exception e) {
                    System.err.println("Could not send the last request");
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isCommandValid(String command) {
        String[] commandArray = {"MSG", "NAME", "PLAY", "HAND", "LAST_TRICK", "QUIT"};

        if (command == null || command.isEmpty()) {
            return false;
        }
        for (String tmpCommand : commandArray) {
            if (command.equals(tmpCommand)) {
                return true;
            }
        }
        return false;
    }

    public boolean isArgumentValid(String argument) {
        if (argument == null || argument.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isCardValid(String card) {
        if (card != null && !card.isEmpty()) {
            for (Game.Card.CardValue cardValue : Game.Card.CardValue.values()) {
                if (cardValue.name().equals(card.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCardSuitValid(String cardSuit) {
        if (cardSuit != null && !cardSuit.isEmpty()) {
            for (Game.Bidding.Options opt : Game.Bidding.Options.values()) {
                if (opt.name().equals(cardSuit.toUpperCase()) && (!opt.name().equals("TA") || !opt.name().equals("SA"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public int containsCommandsWithArgs(String command) {
        String[] commands = {"MSG, NAME, PLAY"};
        int i = 0;

        if (command == null || command.isEmpty()) {
            return -1;
        }
        for (String tmpCommand : commands) {
            if (command.equals(tmpCommand)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public void sendMessage(String command) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        List<String> arguments = new ArrayList<>();

        try {
            if (command.equals("MSG")) {
                System.out.println("Enter the message you want to display to the other users :");
            } else {
                System.out.println("Enter your new name :");
            }
            line = in.readLine();
            if (isArgumentValid(line)) {
                arguments.add(line);
            }
            sendRequest(command, arguments);
        } catch (IOException e) {
            sendRequest("NONE", null);
            System.err.println("Could not get the user's input.");
        }
    }

    public void playCard(String command) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String arg = null;
        List<String> arguments = new ArrayList<>();

        try {
            System.out.println("Choose a card you want to play [SEVEN, EIGHT, NINE, TEN, JACK...] :");
            arg = in.readLine();
            if (isCardValid(arg)) {
                arguments.add(arg);
                System.out.println("Precise the card suit of your card [HEARTS, SPADES, CLUBS, DIAMONDS] :");
                arg = in.readLine();
                if (isCardSuitValid(arg)) {
                    arguments.add(arg);
                    sendRequest(command, arguments);
                    return;
                }
            }
            sendRequest(command, null);
        } catch (IOException e) {
            sendRequest("NONE", null);
            System.err.println("Could not get the user's input.");
        }
    }

    public boolean request() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String command = null;
        int index = -1;

        try {
            System.out.println("What do you want to do ? [MSG, NAME, PLAY, HAND, LAST_TRICK, QUIT] :");
            if (!isCommandValid((command = in.readLine()))) {
                sendRequest(command, null);
                return true;
            } else if ((index = containsCommandsWithArgs(command.toUpperCase())) != -1) {
                play(index, command);
            } else {
                sendRequest(command.toUpperCase(), null);
            }
        } catch (IOException e) {
            System.err.println("Could not get the input.");
            return false;
        } catch (Exception e) {
            System.err.println("Could not send the request to the server.");
            return false;
        }
        return true;
    }
}
