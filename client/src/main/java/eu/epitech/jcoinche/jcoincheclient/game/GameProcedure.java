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

    public boolean sendRequest(String command, List<String> arguments, Game.Answer.Type type) {
        ChannelFuture lastWriteFuture = null;

        try {
            Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
            futureAnswer.setType(type)
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

    public boolean request(Game.Answer.Type gameStep) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        int i = 0;
        NonblockingBufferedReader reader = null;

        try {
            reader = new NonblockingBufferedReader(new BufferedReader(new InputStreamReader(System.in)));
            while ((line = reader.readLine()) != null);
            if (line != null && !line.isEmpty()) {
                String[] commandLine = line.split("\\s+");
                List<String> arguments = new ArrayList<>();
                String command = "";
                for (String args : commandLine) {
                    if (i == 0) {
                        command = args.toUpperCase();
                    } else if (command != null && !command.isEmpty() && command.equals("MSG") && i == 1) {
                        arguments.add(args);
                    } else if (command != null && !command.isEmpty() && command.equals("MSG") && i > 1) {
                        arguments.set(0, new StringBuilder()
                                .append(arguments.get(0))
                                .append(" ")
                                .append(args).toString());
                    } else {
                        arguments.add(args);
                    }
                    ++i;
                }
                sendRequest(command, arguments, gameStep);
            } else {
                sendRequest("NONE", null, gameStep);
            }
        } catch (Exception e) {
            System.err.println("Could not send the request to the server.");
            return false;
        } finally {
            reader.close();
        }
        return true;
    }
}
