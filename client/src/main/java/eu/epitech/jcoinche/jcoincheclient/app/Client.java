package eu.epitech.jcoinche.jcoincheclient.app;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;

import java.net.ConnectException;

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

//        Answer person = Answer.newBuilder()
//                            .setRequest("Coucou")
//                            .setType(GAME)
//                        .build();
//        connection.get_channel().writeAndFlush(person);


//        try {
//            // Read commands from the stdin.
//            ChannelFuture lastWriteFuture = null;
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            String line = null;
//
//            for (;;) {
//                line = null;
//                try {
//                    line = in.readLine();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (line == null) {
//                    break;
//                }
//
//                // Sends the received line to the server.
//                lastWriteFuture = connection.get_channel().writeAndFlush(line + "\r\n");
//
//                // If user typed the 'bye' command, wait until the server closes
//                // the connection.
////                if ("bye".equals(line.toLowerCase())) {
////                    try {
////                        connection.get_channel().closeFuture().sync();
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                    break;
////                }
//            }
//
//            // Wait until all messages are flushed before closing the channel.
//            if (lastWriteFuture != null) {
//                try {
//                    lastWriteFuture.sync();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        } finally {
//            // The connection is closed automatically on shutdown.
//            connection.get_group().shutdownGracefully();
//        }
    }
}