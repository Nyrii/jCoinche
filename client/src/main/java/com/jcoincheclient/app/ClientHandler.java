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
//import static com.jcoincheclient.protobuf.Game.Answer.Type.STANDBY;


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
            Player player = new Player();
            Bidding bidding = new Bidding();

            if (answer.getCode() != -1 && answer.getCode() != 0 && answer.getCode() != 200) {
                System.err.println(answer.getCode() + " " + answer.getRequest());
            } else if (answer.getCode() == 200) {
                System.out.println(answer.getCode() + " " + answer.getRequest());
            }

            try {
                switch (answer.getType()) {

                    case PLAYER:
                        player.askInformations();
//                        bidding.bid();
                        break;

                    case BIDDING:
                        System.out.println(answer.getBidding());
                        // do the processing
                        break;

                    case GAME:
                        System.out.println(answer.getGame());
                        // do the processing
                        break;

                    case STANDBY:
                        System.out.println("Please, wait for your turn.");
                        break;

                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(84);
            }
        }
//                // The connection is closed automatically on shutdown.
//                Connection.get_group().shutdownGracefully();
}
