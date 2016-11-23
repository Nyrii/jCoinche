package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.jcoincheserver.protobuf.Game;
import static eu.epitech.jcoinche.jcoincheserver.protobuf.Game.Answer.Type.*;
import io.netty.channel.ChannelHandlerContext;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;


/**
 * Created by Saursinet on 20/11/2016.
 */
public class AnswerToClient {

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
                    .setType(STANDBY)
                    .build();
        } else {
            return Game.Answer.newBuilder()
                    .setRequest("nickname contains invalid characters or is too short.")
                    .setCode(402)
                    .setType(PLAYER)
                    .build();
        }
    };

    private static Game.Answer setResponseifBid(GameManager gm, ChannelHandlerContext ctx, Game.Bidding bidding, int contract) {
        int code;
        String str;

        if (bidding.getContract() == Game.Bidding.Contract.CAPOT) {
            code = 203;
            str = "Just annonce capot";
            gm.setCapot(true, ctx);
        } else if (bidding.getAmount() < 80) {
            code = 400;
            str = "Bidding to low, minimum is 81";
        } else if (bidding.getAmount() > 160) {
            code = 400;
            str = "Bidding to high, maximum is 160 or capot";
        } else if (bidding.getAmount() < contract) {
            code = 400;
            str = "Bidding to low, a previous bidding was " + contract;
        } else {
            code = 200;
            str = "Bidding okay";
            gm.setContract(bidding.getAmount(), ctx);
        }
        return Game.Answer.newBuilder()
                .setRequest(str)
                .setCode(code)
                .setType(BIDDING)
                .build();
    }

    private static Game.Answer setResponseIfCoinche(GameManager gm, int contract, ChannelHandlerContext ctx) {
        int code;
        String str;

        if (gm.getCoinche()) {
            code = 400;
            str = "A person has already coinche";
        } else if (contract == -1) {
            code = 400;
            str = "There is no contract to coinche";
        } else if (gm.getPersonWhoBet() == gm.getClientPosition(ctx)) {
            code = 400;
            str = "You cannot coinche yourself";
        } else if (gm.arePartner(gm.getPersonWhoBet(), gm.getClientPosition(ctx))) {
            code = 400;
            str = "You cannot coinche your partner";
        } else {
            gm.setCoinche(true, ctx);
            code = 201;
            str = "You just coinched the other player";
        }
        return Game.Answer.newBuilder()
                .setRequest(str)
                .setCode(code)
                .setType(BIDDING)
                .build();
    }

    private static Game.Answer setResponseIfSurCoinche(GameManager gm, ChannelHandlerContext ctx) {
        int code;
        String str;

        if (gm.getSurCoinche()) {
            code = 400;
            str = "A person has already surcoinche";
        } else if (gm.getPersonWhoBet() != gm.getClientPosition(ctx)) {
            code = 400;
            str = "You cannot surcoinche if it's not you who bet at first";
        } else {
            gm.setSurCoinche(true, ctx);
            code = 201;
            str = "You just surcoinched the other player";
        }
        return Game.Answer.newBuilder()
                .setRequest(str)
                .setCode(code)
                .setType(BIDDING)
                .build();
    }

    public static Game.Answer interpreteBidding(GameManager gm, ChannelHandlerContext ctx, Game.Bidding bidding, int contract) {
        Game.Answer answer;
        boolean pastInElse = false;

        if (bidding.getBid()) {
            answer = setResponseifBid(gm, ctx, bidding, contract);
        } else if (bidding.getCoinche()) {
            answer = setResponseIfCoinche(gm, contract, ctx);
        } else if (bidding.getSurcoinche()) {
            answer = setResponseIfSurCoinche(gm, ctx);
        } else {
            answer = Game.Answer.newBuilder()
                    .setRequest("You just pass your turn")
                    .setCode(200)
                    .setType(BIDDING)
                    .build();
            gm.addInactiveTurn(gm.getNbTurnInactive() + 1);
            pastInElse = true;
        }
        if (!pastInElse)
            gm.addInactiveTurn(0);
        gm.checkIfPartyCanRun();
        return answer;
    }
}
