package com.jcoincheserver.app;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;

/**
 * Created by Saursinet on 20/11/2016.
 */
public class AnswerToClient {

    static ArrayList cmd = new ArrayList();
    static ArrayList func = new ArrayList();

    private static void initCmdAndFunctionsList() {
        cmd.add("name");
        func.add(setName);
    }

    private static void addName(ArrayList nameClient, String id, String name) {
        for (int i = 0; i < nameClient.size(); i++) {
            if (id.contains((String) nameClient.get(i))) {
                nameClient.set(i, name);
            }
        }
    }

    public interface Function {
        String answer(SecureChatServerHandler secChatServer, ArrayList nameClient, ChannelHandlerContext ctx, String msg);
    }

    public static final Function setName = new Function() {
        public String answer(SecureChatServerHandler secChatServer, ArrayList nameClient, ChannelHandlerContext ctx, String msg) {
            addName(nameClient, ctx.toString(), msg);
            secChatServer.setNameClient(nameClient);
            return ("200: nickname changed.\r\n");
        }
    };

    public static boolean interprete(SecureChatServerHandler secChatServer, ArrayList nameClient, ChannelHandlerContext ctx, String msg) {
        if (cmd.size() == 0)
            initCmdAndFunctionsList();

        String[] tokens = msg.toLowerCase().split(" ");
        for (int i = 0; i < cmd.size(); i++) {
            if (cmd.get(i) == tokens[0]) {
                Function f = (Function) func.get(i);
                f.answer(secChatServer, nameClient, ctx, msg);
            }
        }
        if (msg.toLowerCase().contains("name")) {
            addName(nameClient, ctx.toString(), msg);
            secChatServer.setNameClient(nameClient);
        }
        return (false);
    }

}
