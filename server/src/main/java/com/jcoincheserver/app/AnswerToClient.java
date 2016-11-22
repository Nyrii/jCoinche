package com.jcoincheserver.app;

import com.jcoincheserver.protobuf.Game;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;

import static com.jcoincheserver.protobuf.Game.Answer.Type.*;

/**
 * Created by Saursinet on 20/11/2016.
 */
public class AnswerToClient {

    static ArrayList cmd = new ArrayList();
    static ArrayList func = new ArrayList();

    private static void initCmdAndFunctionsList() {
        cmd.add("name");
//        func.add(setName);
    }

    public interface Function {
        String answer(PersonHandler answerClient, ArrayList nameClient, ChannelHandlerContext ctx, String msg);
    }

    public static boolean partyCanBegin(ArrayList nameClient) {
        if (nameClient.size() != 4)
            return false;
        for (Object tmp : nameClient) {
            if (((String) tmp).charAt(0) == '0' && ((String) tmp).charAt(1) == 'x')
                return false;
        }
        return true;
    }

    private static boolean nameAlreadyInUse(ArrayList nameClient, String id, String name) {
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
                nameClient.set(i, name);
            }
        }
        return true;
    }

    public static Game.Answer setName(GameManager answerClient, ArrayList nameClient, ChannelHandlerContext ctx, String msg) {
        if (nameAlreadyInUse(nameClient, ctx.toString(), msg)) {
            Game.Answer answer = Game.Answer.newBuilder()
                    .setRequest("nickname is already in use")
                    .setCode(403)
                    .setType(PLAYER)
                    .build();
            return answer;
        } else if (addName(nameClient, ctx.toString(), msg)) {
            Game.Answer answer = Game.Answer.newBuilder()
                    .setRequest("Nickname changed")
                    .setCode(200)
                    .setType(STANDBY)
                    .build();
            answerClient.setNameClient(nameClient);
            return answer;
        } else {
            Game.Answer answer = Game.Answer.newBuilder()
                    .setRequest("nickname contains invalid characters or is too short.")
                    .setCode(402)
                    .setType(PLAYER)
                    .build();
            return answer;
        }
    };

    public static Game.Answer interpreteBidding(ArrayList nameClient, ChannelHandlerContext ctx, Game.Bidding bidding) {
        if (cmd.size() == 0)
            initCmdAndFunctionsList();



        Game.Answer answer = Game.Answer.newBuilder()
                .setRequest("Bind not implemented yet")
                .setCode(400)
                .setType(BIDDING)
                .build();
        return answer;
    }

}
