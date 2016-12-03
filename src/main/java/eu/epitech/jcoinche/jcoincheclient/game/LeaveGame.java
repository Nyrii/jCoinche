package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.jcoincheclient.network.Connection;
import eu.epitech.jcoinche.protobuf.Game;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.LEAVE;

/**
 * Created by noboud_n on 03/12/2016.
 */
public class LeaveGame {
    public static void leave() {
        try {
            Game.Answer answer = Game.Answer.newBuilder()
                    .setRequest("")
                    .setCode(-1)
                    .setType(LEAVE)
                    .build();
            if (Connection.get_channel() == null) {
                System.err.println("Connection lost.");
                return;
            }
            Connection.get_channel().writeAndFlush(answer);
            Connection.get_channel().closeFuture().sync();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(84);
        }
    }
}
