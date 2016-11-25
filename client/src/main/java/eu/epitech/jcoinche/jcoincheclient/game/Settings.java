package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheclient.protobuf.Game;
import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static eu.epitech.jcoinche.jcoincheclient.protobuf.Game.Answer.Type.SETTINGS;

/**
 * Created by noboud_n on 23/11/2016.
 */
public class Settings {
    public boolean request() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        ChannelFuture lastWriteFuture = null;
        int i = 0;

        for (;;) {
            i = 0;
            try {
                line = in.readLine();
                if (line == null) {
                    break;
                } else if (!line.isEmpty()) {
                    String[] commandLine = line.split("\\s+");
                    ArrayList<String> arguments = new ArrayList<String>();
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
                    Game.Answer request = Game.Answer.newBuilder().addAllArguments(arguments).build();
                    Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
                    futureAnswer.setType(SETTINGS)
                            .setRequest(command)
                            .setCode(100)
                            .build();
                    lastWriteFuture = Connection.get_channel().writeAndFlush(futureAnswer);
                    if (lastWriteFuture != null) {
                        try {
                            lastWriteFuture.sync();
                        } catch (Exception e) {
                            System.err.println("Could not send the last requests");
                            return false;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Could not get your request.");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Could not send the request to the server.");
            }
        }
        return true;
    }
}
