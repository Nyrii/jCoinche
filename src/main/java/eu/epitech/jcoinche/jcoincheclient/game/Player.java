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
        Game.Answer answer = Game.Answer.newBuilder()
                .setRequest(error == null ? "" : error)
                .setCode(-1)
                .setType(PLAYER)
                .build();
        if (Connection.get_channel() == null) {
            System.err.println("Connection lost.");
            return;
        }
        Connection.get_channel().writeAndFlush(answer);
        try {
            Connection.get_channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.err.println("Could not close the socket properly... exiting the client...");
            return;
        }
    }

    public void sendName(String name) {
        Game.Answer answer = Game.Answer.newBuilder()
                .setType(PLAYER)
                .setPlayer(Game.Player.newBuilder().setName(name).build())
                .build();
        if (Connection.get_channel() == null) {
            System.err.println("Connection lost.");
            System.exit(84);
        }
        Connection.get_channel().writeAndFlush(answer);
    }

    public void askInformations() throws Exception {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = null;

            for (;;) {
                try {
                    line = in.readLine();
                } catch (IOException e) {
                    sendError("QUIT");
                    throw new Exception("System error : Could not get the input.");
                }

                if (line != null && !line.isEmpty() && line.trim().length() > 0) {
                    sendName(line);
                    break;
                } else {
                    System.out.println("Your name is invalid, please enter a new one : ");
                }
            }
        } catch (Exception e) {
            sendError("QUIT");
            throw new Exception("Cannot get the player's informations.");
        }
    }

}
