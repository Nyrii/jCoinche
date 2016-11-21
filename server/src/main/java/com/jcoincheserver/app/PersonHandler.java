package com.jcoincheserver.app;

import com.jcoincheserver.protobuf.Game.Answer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by noboud_n on 20/11/2016.
 */
public class PersonHandler extends SimpleChannelInboundHandler<Answer>{

    @Override
    public void channelRead0(ChannelHandlerContext arg0, Answer answer) throws Exception {
        switch (answer.getType()) {

            case PLAYER:
                System.out.println(answer.getPlayer().getTest());
                break;

            case BIDDING:
                System.out.println(answer.getBid().getTest());
                // do the processing
                break;

            case GAME:
                System.out.println(answer.getGame().getTest());
                // do the processing
                break;

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
