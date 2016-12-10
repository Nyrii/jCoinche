package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.Channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.GAME;

/**
 * Created by noboud_n on 04/12/2016.
 */
public class Process {

    private enum Commands {MSG, NAME, PLAY, HAND, LAST_TRICK, QUIT}
    private enum CommandsWithArgs {MSG, NAME, PLAY}

    interface PlayAction {
        void play(String command, BufferedReader in, Channel channel);
    }

    private Process.PlayAction[] playActions = new Process.PlayAction[] {
            new Process.PlayAction() { public void play(String command, BufferedReader in, Channel channel) { sendMessage(command, in, channel); } },
            new Process.PlayAction() { public void play(String command, BufferedReader in, Channel channel) { sendMessage(command, in, channel); } },
            new Process.PlayAction() { public void play(String command, BufferedReader in, Channel channel) { playCard(command, in, channel); } },
    };

    public void play(int index, String command, BufferedReader in, Channel channel) {
        playActions[index].play(command, in, channel);
    }

    public boolean sendRequest(String command, List<String> arguments, Channel channel) {
        if (channel == null) {
            return false;
        }
        try {
            Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
            Game.GameProgress.Builder gameProgress = Game.GameProgress.newBuilder();
            if (isCommandValid(command)) {
                gameProgress.setCommand(Game.GameProgress.Command.valueOf(command));
            } else {
                gameProgress.setCommand(Game.GameProgress.Command.INVALID_COMMAND);
            }
            gameProgress.addAllArguments(arguments);
            futureAnswer.setType(GAME)
                    .setGame(gameProgress.build())
                    .setCode(100)
                    .build();
            channel.writeAndFlush(futureAnswer);
        } catch (Exception e) {
            System.err.println("Error : " + e.getMessage());
            LeaveGame.leave(channel);
            return false;
        }
        return true;
    }

    public boolean sendRequestWithCard(String command, List<String> arguments, Game.Card card, Channel channel) {
        if (channel == null) {
            return false;
        }
        try {
            Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
            Game.GameProgress.Builder gameProgress = Game.GameProgress.newBuilder();
            if (isCommandValid(command)) {
                gameProgress.setCommand(Game.GameProgress.Command.valueOf(command));
            } else {
                gameProgress.setCommand(Game.GameProgress.Command.INVALID_COMMAND);
            }
            gameProgress.addAllArguments(arguments)
                    .setCard(card)
                    .build();
            futureAnswer.setType(GAME)
                    .setGame(gameProgress)
                    .setCode(100)
                    .build();
            channel.writeAndFlush(futureAnswer);
        } catch (Exception e) {
            System.err.println("Error : " + e.getMessage());
            LeaveGame.leave(channel);
            return false;
        }
        return true;
    }

    public boolean isCommandValid(String command) {
        if (command == null || command.isEmpty()) {
            return false;
        }
        for (Commands tmpCommand : Commands.values()) {
            if (tmpCommand.name().equals(command)) {
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
            for (Game.Card.CardType cardType : Game.Card.CardType.values()) {
                if (cardType.name().equals(cardSuit.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Game.Card setCard(String cardValue, String cardSuit) {
        Game.Card.Builder card = Game.Card.newBuilder();
        boolean set = false;

        if (cardValue != null && !cardValue.isEmpty()) {
            for (Game.Card.CardValue cardType : Game.Card.CardValue.values()) {
                if (cardType.name().equals(cardValue.toUpperCase())) {
                    card.setCardValue(cardType);
                    set = true;
                    break;
                }
            }
        }
        if (!set)
            card.setCardValue(Game.Card.CardValue.INVALID_VALUE);

        set = false;
        if (cardSuit != null && !cardSuit.isEmpty()) {
            for (Game.Card.CardType cardType : Game.Card.CardType.values()) {
                if (cardType.name().equals(cardSuit.toUpperCase())) {
                    card.setCardType(cardType);
                    set = true;
                    break;
                }
            }
        }
        if (!set)
            card.setCardType(Game.Card.CardType.INVALID_TYPE);

        return card.build();
    }

    public int containsCommandsWithArgs(String command) {
        int i = 0;

        if (command == null || command.isEmpty()) {
            return -1;
        }
        for (CommandsWithArgs tmpCommand : CommandsWithArgs.values()) {
            if (tmpCommand.name().equals(command)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public void sendMessage(String command, BufferedReader in, Channel channel) {
        String line;
        List<String> arguments = new ArrayList<>();

        try {
            if (SaveObject.get_answer().getType() != Game.Answer.Type.GAME) {
                return;
            }
            if (command.equals("MSG")) {
                System.out.println("Enter the message you want to display to the other users or any key if you've been notified of the game's end or the bidding's end :");
            } else {
                System.out.println("Enter your new name or type any key if you've been notified of the game's end or the bidding's end");
            }
            line = in.readLine();
            if (isArgumentValid(line)) {
                arguments.add(line);
            }
            if (!sendRequest(command, arguments, channel))
                System.exit(84);
        } catch (IOException e) {
            if (!sendRequest("NONE", new ArrayList<String>(), channel))
                System.exit(84);
            System.err.println("Could not get the user's input.");
        }
    }

    public void playCard(String command, BufferedReader in, Channel channel) {
        String cardValue = "", cardSuit = "";
        List<String> arguments = new ArrayList<>();

        try {
            if (SaveObject.get_answer().getType() != Game.Answer.Type.GAME) {
                return;
            }
            System.out.println("Type SEVEN, EIGHT, NINE, TEN, JACK... or any key if you've been notified of the game's end or the bidding's end :");
            cardValue = in.readLine();
            if (cardValue != null) {
                cardValue = cardValue.replaceAll("\\s", "");
                cardValue = cardValue.toUpperCase();
            }
            if (isCardValid(cardValue)) {
                if (SaveObject.get_answer().getType() != Game.Answer.Type.GAME) {
                    return;
                }
                System.out.println("Type HEARTS, SPADES, CLUBS, DIAMONDS or any key if you've been notified of the game's end or the bidding's end :");
                cardSuit = in.readLine();
                if (cardSuit != null) {
                    cardSuit = cardSuit.replaceAll("\\s", "");
                    cardSuit = cardSuit.toUpperCase();
                }
                if (isCardSuitValid(cardSuit)) {
                    if (!sendRequestWithCard(command, arguments, setCard(cardValue, cardSuit), channel))
                        System.exit(84);
                    return;
                }
            }
            if (!sendRequestWithCard(command, new ArrayList<String>(), setCard(cardValue, cardSuit), channel))
                System.exit(84);
        } catch (IOException e) {
            if (!sendRequest("NONE", new ArrayList<String>(), channel))
                System.exit(84);
            System.err.println("Could not get the user's input.");
        }
    }

    public void request(BufferedReader in, Channel channel) {
        String command;
        int index;

        try {
            if (SaveObject.get_answer().getType() != Game.Answer.Type.GAME) {
                return;
            }
            System.out.println("Type your COMMAND or any key if you've been notified of the game's end or the bidding's end :");
            command = in.readLine();
            command = command.replaceAll("\\s", "");
            command = command.toUpperCase();
            if (!isCommandValid(command) && containsCommandsWithArgs(command.toUpperCase()) == -1) {
                if (!sendRequest(command, new ArrayList<String>(), channel))
                    System.exit(84);
            } else if ((index = containsCommandsWithArgs(command.toUpperCase())) != -1) {
                play(index, command, in, channel);
            } else {
                if (!sendRequest(command.toUpperCase(), new ArrayList<String>(), channel))
                    System.exit(84);
            }
        } catch (IOException e) {
            if (!sendRequest("NONE", new ArrayList<String>(), channel))
                System.exit(84);
            System.err.println("Could not get the input.");
        }
    }
}
