package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.BIDDING;
import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.GAME;

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
    public void sendError(String error) {
        Game.Answer answer = Game.Answer.newBuilder()
                .setRequest(error == null ? "" : error)
                .setCode(-1)
                .setType(GAME)
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

    public boolean sendRequest(String command, List<String> arguments) {
        try {
            Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
            Game.GameProgress.Builder gameProgress = Game.GameProgress.newBuilder();
            if (isCommandValid(command)) {
                gameProgress.setCommand(Game.GameProgress.Command.valueOf(command));
            } else {
                gameProgress.setCommand(Game.GameProgress.Command.INVALID);
            }
            gameProgress.addAllArguments(arguments);
            futureAnswer.setType(GAME)
                    .setGame(gameProgress)
                    .setCode(100)
                    .build();
            if (Connection.get_channel() == null) {
                System.err.println("Connection lost.");
                return false;
            }
            Connection.get_channel().writeAndFlush(futureAnswer);
        } catch (Exception e) {
            System.err.println("Error : " + e.getMessage());
            sendError("QUIT");
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
        String line;
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
            if (!sendRequest(command, arguments))
                System.exit(84);
        } catch (IOException e) {
            if (!sendRequest("NONE", null))
                System.exit(84);
            System.err.println("Could not get the user's input.");
        }
    }

    public void playCard(String command) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String arg;
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
                    if (!sendRequest(command, arguments))
                        System.exit(84);
                    return;
                }
            }
            if (!sendRequest(command, null))
                System.exit(84);
        } catch (IOException e) {
            if (!sendRequest("NONE", null))
                System.exit(84);
            System.err.println("Could not get the user's input.");
        }
    }

    public void request() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String command;
        int index;

        try {
            System.out.println("What do you want to do ? [MSG, NAME, PLAY, HAND, LAST_TRICK, QUIT] :");
            if (!isCommandValid((command = in.readLine()))) {
                if (!sendRequest(command, null))
                    System.exit(84);
            } else if ((index = containsCommandsWithArgs(command.toUpperCase())) != -1) {
                play(index, command);
            } else {
                if (!sendRequest(command.toUpperCase(), null))
                    System.exit(84);
            }
        } catch (IOException e) {
            System.err.println("Could not get the input.");
        }
    }
}
