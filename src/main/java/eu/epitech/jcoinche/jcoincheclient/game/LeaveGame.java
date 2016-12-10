package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.LEAVE;

/**
 * Created by noboud_n on 03/12/2016.
 */
public class LeaveGame {
    public static void leave(Channel channel) {
        try {
            Game.Answer answer = Game.Answer.newBuilder()
                    .setRequest("")
                    .setCode(-1)
                    .setType(LEAVE)
                    .build();
            if (channel != null) {
                channel.writeAndFlush(answer);
                channel.closeFuture();
            } else {
                System.err.println("Connection lost.");
                return;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(84);
        }
    }
}
