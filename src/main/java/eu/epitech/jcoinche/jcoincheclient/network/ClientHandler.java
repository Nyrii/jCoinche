package eu.epitech.jcoinche.jcoincheclient.network;

import eu.epitech.jcoinche.jcoincheclient.game.GameProcedure;
import eu.epitech.jcoinche.jcoincheclient.game.SaveObject;
import eu.epitech.jcoinche.protobuf.Game;
import eu.epitech.jcoinche.jcoincheclient.game.Bidding;
import eu.epitech.jcoinche.jcoincheclient.game.Player;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by noboud_n on 16/11/2016.
 */

/**
 * Handles a client-side channel.
 */
public class ClientHandler extends SimpleChannelInboundHandler<Game.Answer> {

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            System.out.println(cause.getMessage());
            ctx.close();
            System.exit(84);
        }

        @Override
        public void channelRead0(ChannelHandlerContext arg0, Game.Answer answer) throws Exception {
            if (answer.getCode() != -1 && answer.getCode() != 0 && !answer.getRequest().isEmpty()) {
                String message = new StringBuilder()
                        .append(answer.getCode())
                        .append(" ")
                        .append(answer.getRequest())
                        .toString();
                if (answer.getCode() >= 200 && answer.getCode() <= 300) {
                    System.out.println(message);
                } else {
                    System.err.println(message);
                }
            }
            SaveObject.set_answer(answer);
        }
//                // The connection is closed automatically on shutdown.
//                Connection.get_group().shutdownGracefully();
}