package com.jcoincheclient.app;

import com.jcoincheclient.protobuf.Game;
import com.jcoincheclient.protobuf.Game.Answer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.jcoincheclient.protobuf.Game.Answer.Type.GAME;

/**
 * Created by noboud_n on 16/11/2016.
 */

/**
 * Handles a client-side channel.
 */
public class ClientHandler extends SimpleChannelInboundHandler<Answer> {

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

        @Override
        public void channelRead0(ChannelHandlerContext arg0, Answer answer) throws Exception {
            System.out.println(answer);
            if (answer.getCode() != -1 && answer.getCode() != 0 && answer.getCode() != 200) {
                System.err.println(answer.getCode() + " " + answer.getRequest());
            } else if (answer.getCode() == 200) {
                System.out.println(answer.getCode() + " " + answer.getRequest());
            }

            try {
                switch (answer.getType()) {

                    case PLAYER:
                        Player.askInformations();
                        break;

                    case BIDDING:
                        System.out.println(answer.getBidding());
                        // do the processing
                        break;

                    case GAME:
                        System.out.println(answer.getGame());
                        // do the processing
                        break;

                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(84);
            }
//            try {
//                // Read commands from the stdin.
//                ChannelFuture lastWriteFuture = null;
//                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//                String line = null;
//
//                for (;;) {
//                    line = null;
//                    try {
//                        line = in.readLine();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    if (line == null) {
//                        break;
//                    }
//
//                    // Sends the received line to the server.
//                    Answer person = Answer.newBuilder()
//                            .setRequest("Coucou")
//                            .setType(GAME)
//                            .build();
//                    lastWriteFuture = Connection.get_channel().writeAndFlush(person);
//                    break;

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
//                if (lastWriteFuture != null) {
//                    try {
//                        lastWriteFuture.sync();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            finally {
//                // The connection is closed automatically on shutdown.
//                Connection.get_group().shutdownGracefully();
//            }
//        }
}
