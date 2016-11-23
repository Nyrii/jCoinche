package eu.epitech.jcoinche.jcoincheclient.network;

import eu.epitech.jcoinche.jcoincheclient.protobuf.Game;
import eu.epitech.jcoinche.jcoincheclient.game.Bidding;
import eu.epitech.jcoinche.jcoincheclient.game.Player;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.ConnectException;

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
            Player player = new Player();
            Bidding bidding = new Bidding();

            if (answer.getCode() != -1 && answer.getCode() != 0 && !answer.getRequest().isEmpty()) {
                String message = new StringBuilder()
                        .append(answer.getCode())
                        .append(" ")
                        .append(answer.getRequest())
                        .toString();
                if (answer.getCode() == 200) {
                    System.out.println(message);
                } else {
                    System.err.println(message);
                }
            }

            try {
                switch (answer.getType()) {

                    case PLAYER:
                        player.askInformations();
//                        bidding.biddingProcess(answer);
                        break;

                    case BIDDING:
                        // missing
                        bidding.biddingProcess(answer);
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
