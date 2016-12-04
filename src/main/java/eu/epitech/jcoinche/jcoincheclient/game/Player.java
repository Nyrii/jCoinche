package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.PLAYER;

/**
 * Created by noboud_n on 21/11/2016.
 */
public class Player {

    public boolean sendName(String name, Channel channel) throws Exception {
        if (channel == null) {
            throw new Exception("Connection lost.");
        }
        try {
            Game.Answer answer = Game.Answer.newBuilder()
                    .setType(PLAYER)
                    .setPlayer(Game.Player.newBuilder().setName(name).build())
                    .build();
            channel.writeAndFlush(answer);
        } catch (Exception e) {
            System.err.println("Could not send the player's name to the server.");
            return false;
        }
        return true;
    }

    public boolean askInformations(BufferedReader in, Channel channel) throws Exception {
        if (channel == null) {
            throw new Exception("Connection lost");
        }
        try {
            String line = null;

            try {
                line = in.readLine();
            } catch (IOException e) {
                System.err.println("Could not get the player's input.");
                return false;
            }

            if (line != null && !line.isEmpty() && line.trim().length() > 0) {
                return (sendName(line, channel));
            } else {
                System.err.println("Your name is invalid, please enter a new one : ");
            }
        } catch (Exception e) {
            throw new Exception("An error occured during the name asking process");
        }
        return false;
    }

}
