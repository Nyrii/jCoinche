package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.PLAYER;

/**
 * Created by noboud_n on 21/11/2016.
 */
public class Player {

    private void sendError(String error) {
        try {
            Game.Answer answer = Game.Answer.newBuilder()
                    .setRequest(error == null ? "" : error)
                    .setCode(-1)
                    .setType(PLAYER)
                    .build();
            if (Connection.get_channel() == null) {
                return;
            }
            Connection.get_channel().writeAndFlush(answer);
            Connection.get_channel().closeFuture().sync();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(84);
        }
    }

    public boolean sendName(String name) throws Exception {
        try {
            Game.Answer answer = Game.Answer.newBuilder()
                    .setType(PLAYER)
                    .setPlayer(Game.Player.newBuilder().setName(name).build())
                    .build();
            if (Connection.get_channel() == null) {
                throw new Exception("Connection lost.");
            }
            Connection.get_channel().writeAndFlush(answer);
        } catch (Exception e) {
            System.err.println("Could not send the player's name to the server.");
            return false;
        }
        return true;
    }

    public boolean askInformations() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = null;

            try {
                line = in.readLine();
            } catch (IOException e) {
                return false;
            }

            if (line != null && !line.isEmpty() && line.trim().length() > 0) {
                return (sendName(line));
            } else {
                System.out.println("Your name is invalid, please enter a new one : ");
            }
        } catch (Exception e) {
            sendError("QUIT");
            System.err.println(e.getMessage());
            System.exit(84);
        }
        return false;
    }

}
