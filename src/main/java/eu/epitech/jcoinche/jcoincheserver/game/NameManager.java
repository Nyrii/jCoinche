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

    public static boolean partyCanBegin(ArrayList nameClient) {
        if (nameClient == null || nameClient.size() != 4)
            return false;
        for (Object tmp : nameClient) {
            if (((String) tmp).charAt(0) == '0' && ((String) tmp).charAt(1) == 'x')
                return false;
        }
        return true;
    }

    protected static boolean nameAlreadyInUse(ArrayList nameClient, String name) {
        if (nameClient == null)
            return false;
        for (int i = 0; i < nameClient.size(); i++) {
            if (name.toLowerCase().equals(((String) nameClient.get(i)).toLowerCase()))
                return true;
        }
        return false;
    }

    private static boolean addName(ArrayList nameClient, String id, String name) {
        if (name.length() < 4 || !StringUtils.isAlphanumeric(name))
            return false;
        for (int i = 0; i < nameClient.size(); i++) {
            if (id.contains((String) nameClient.get(i))) {
                System.out.println("replace " + nameClient.get(i) + " by " + name);
                nameClient.set(i, name);
            }
        }
        return true;
    }

    public static Game.Answer setName(GameManager answerClient, ArrayList nameClient, ChannelHandlerContext ctx, String msg) {
        if (nameAlreadyInUse(nameClient, msg)) {
            return Game.Answer.newBuilder()
                    .setRequest("nickname is already in use")
                    .setCode(403)
                    .setType(PLAYER)
                    .build();
        } else if (addName(nameClient, ctx.toString(), msg)) {
            answerClient.setNameClient(nameClient);
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
