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

    public void askInformations() throws Exception {
        try {
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = null;

            for (;;) {
                line = null;
                try {
                    line = in.readLine();
                } catch (IOException e) {
                    sendError("QUIT");
                    throw new Exception("System error : Could not get the input.");
                }

                if (line != null && !line.isEmpty() && line.trim().length() > 0) {
                    // Sends the received line to the server.
                    Game.Answer answer = Game.Answer.newBuilder()
                                        .setType(PLAYER)
                                        .setPlayer(Game.Player.newBuilder().setName(line).build())
                                        .build();
                    if (Connection.get_channel() == null) {
                        System.err.println("Connection lost.");
                        System.exit(84);
                    }
                    lastWriteFuture = Connection.get_channel().writeAndFlush(answer);
                    // Wait until all messages are flushed.
                    if (lastWriteFuture != null) {
                        try {
                            lastWriteFuture.sync();
                        } catch (InterruptedException e) {
                            sendError("QUIT");
                            throw new Exception("Could not send the player's informations to the server.");
                        }
                    }
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
