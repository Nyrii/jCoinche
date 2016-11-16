package com.jcoincheclient.app;

import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;

/**
 * Created by noboud_n on 16/11/2016.
 */


public class Client {
    static final String HOST = System.getProperty("host", "10.10.250.98");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));

    public static void main(String[] args) throws InterruptedException {
//        Player.Person.Builder person = Player.Person.newBuilder();
//        System.out.println("Enter name: ");
//        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            String mdr = reader.readLine();
//            System.out.println(mdr);
//            person.setName(mdr);
//            System.out.println(person.getName());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }




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






        try {
            // Read commands from the stdin.
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = null;
                try {
                    line = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (line == null) {
                    break;
                }

                // Sends the received line to the server.
                lastWriteFuture = connection.get_channel().writeAndFlush(line + "\n");

                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    try {
                        connection.get_channel().closeFuture().sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
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
