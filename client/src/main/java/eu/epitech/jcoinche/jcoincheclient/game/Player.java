package eu.epitech.jcoinche.jcoincheclient.game;

import com.jcoincheclient.protobuf.Game;
import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.jcoincheclient.protobuf.Game.Answer.Type.PLAYER;

/**
 * Created by noboud_n on 21/11/2016.
 */
public class Player {

    public void sendError(String error) {
        Game.Answer answer = Game.Answer.newBuilder()
                .setRequest(error)
                .setCode(-1)
                .setType(PLAYER)
                .build();
        Connection.get_channel().writeAndFlush(answer);
        try {
            Connection.get_channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                    e.printStackTrace();
                    sendError("QUIT");
                    throw new Exception("System error : Could not get the input.");
                }

                if (line != null && !line.isEmpty() && line.trim().length() > 0) {
                    // Sends the received line to the server.
                    Game.Answer answer = Game.Answer.newBuilder()
                            .setType(PLAYER)
                            .setPlayer(Game.Player.newBuilder().setName(line + "\r\n").build())
                            .build();
                    lastWriteFuture = Connection.get_channel().writeAndFlush(answer);
                    if (lastWriteFuture != null) {
                        try {
                            lastWriteFuture.sync();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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
            e.printStackTrace();
            sendError("QUIT");
            throw new Exception("Cannot get the player's informations.");
        }
    }

}
