package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.jcoincheclient.protobuf.Game;
import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static eu.epitech.jcoinche.jcoincheclient.protobuf.Game.Answer.Type.STANDBY;

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
                    for (String args : commandLine) {
                        System.out.println(args);
                        ++i;
                    }
                    System.out.println();
                    Game.Answer.Builder futureAnswer = Game.Answer.newBuilder();
                    futureAnswer.setType(STANDBY)
                            .setRequest(line)
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
                return false;
            }
        }
        return true;
    }
}
