package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.protobuf.Game;
import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.*;
import io.netty.channel.ChannelHandlerContext;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by Saursinet on 20/11/2016.
 */
public class NameManager {

    public static boolean partyCanBegin(ArrayList client) {
        if (client == null || client.size() != 4)
            return false;
        for (Object tmp : client) {
            if (((Person) tmp).getName().charAt(0) == '0' && ((String) tmp).charAt(1) == 'x')
                return false;
        }
        return true;
    }

    protected static boolean nameAlreadyInUse(ArrayList client, String name) {
        if (client == null)
            return false;
        for (int i = 0; i < client.size(); i++) {
            if (name.toLowerCase().equals(((Person) client.get(i)).getName().toLowerCase()))
                return true;
        }
        return false;
    }

    private static boolean addName(ArrayList client, String id, String name) {
        if (name.length() < 4 || !StringUtils.isAlphanumeric(name))
            return false;
        for (int i = 0; i < client.size(); i++) {
            if (id.contains(((Person) client.get(i)).getName())) {
                client.set(i, name);
            }
        }
        return true;
    }

    public static Game.Answer setName(GameManager answerClient, ArrayList client, ChannelHandlerContext ctx, String msg) {
        if (nameAlreadyInUse(client, msg)) {
            return Game.Answer.newBuilder()
                    .setRequest("Nickname is already in use")
                    .setCode(403)
                    .setType(PLAYER)
                    .build();
        } else if (addName(client, ctx.toString(), msg)) {
            answerClient.setClient(client);
            return Game.Answer.newBuilder()
                    .setRequest("Nickname changed")
                    .setCode(200)
                    .setType(NONE)
                    .build();
        } else {
            return Game.Answer.newBuilder()
                    .setRequest("Nickname contains invalid characters or is too short.")
                    .setCode(402)
                    .setType(PLAYER)
                    .build();
        }
    }
}
