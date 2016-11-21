package com.jcoincheclient.app;

import com.jcoincheclient.protobuf.Game.GameProgress;
import com.jcoincheclient.protobuf.Game.Answer;
import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;

import static com.jcoincheclient.protobuf.Game.Answer.Type.GAME;

/**
 * Created by noboud_n on 16/11/2016.
 */


public class Client {
    public static void main(String[] args) throws InterruptedException {
        Connection connection = new Connection();
        try {
            connection.requestHostAndPort();
            System.out.println("Waiting for the server's answer...");
            connection.connect();
        } catch (ConnectException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(84);
        }


        Answer person = Answer.newBuilder()
                            .setRequest("Coucou")
                            .setType(GAME)
                            .setGame(GameProgress.newBuilder().setTest("Un test ma gueule"))
                        .build();
        System.out.println(person.getGame().getTest());
        connection.get_channel().writeAndFlush(person);


        try {
            // Read commands from the stdin.
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            for (;;) {
                try {
                    line = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (line != null) {
//                    Player lol = person.setName(line).build();
                    lastWriteFuture = connection.get_channel().writeAndFlush(person);
//                    person.build().writeTo(connection.get_channel());
                    break;
                }
            }

            for (;;) {
                line = null;
                try {
                    line = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (line == null) {
                    break;
                }

                // Sends the received line to the server.
                lastWriteFuture = connection.get_channel().writeAndFlush(line + "\r\n");

                // If user typed the 'bye' command, wait until the server closes
                // the connection.
//                if ("bye".equals(line.toLowerCase())) {
//                    try {
//                        connection.get_channel().closeFuture().sync();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                try {
                    lastWriteFuture.sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            // The connection is closed automatically on shutdown.
            connection.get_group().shutdownGracefully();
        }
    }
}
