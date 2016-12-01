package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.protobuf.Game;
import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.*;
import io.netty.channel.ChannelHandlerContext;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by Saursinet on 20/11/2016.
 */
public class AnswerToClient {

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
                    .setType(STANDBY)
                    .build();
        } else {
            return Game.Answer.newBuilder()
                    .setRequest("Nickname contains invalid characters or is too short.")
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
            gm.setAtout(bidding.getOption());
            str = "Just annonced capot";
            gm.setCapot(true, ctx);
            gm.setMessage(gm.getNameFromClient(ctx) + " announced capot at " + bidding.getOption());
        } else if (bidding.getAmount() < 80) {
            code = 400;
            str = "Bidding to low, minimum is 80";
        } else if (bidding.getAmount() > 160) {
            code = 400;
            str = "Bidding to high, maximum is 160 or capot";
        } else if (bidding.getAmount() <= contract) {
            code = 400;
            str = "Bidding to low, a previous bidding was " + contract;
        } else {
            code = 200;
            str = "Bidding okay";
            gm.setMessage(gm.getNameFromClient(ctx) + " announced a bidding with " + bidding.getAmount() + " at " + bidding.getOption());
            gm.setContract(bidding.getAmount(), ctx);
            gm.setAtout(bidding.getOption());
        }
        return Game.Answer.newBuilder()
                .setRequest(str)
                .setCode(code)
                .setCards(gm.getDeck(gm.getClientPosition(ctx)))
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
            gm.setMessage(gm.getNameFromClient(ctx) + " coinched the other team");
        }
        return Game.Answer.newBuilder()
                .setRequest(str)
                .setCode(code)
                .setCards(gm.getDeck(gm.getClientPosition(ctx)))
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
            System.out.println("person who bet = " + gm.getPersonWhoBet() + "  et pos = " + gm.getClientPosition(ctx) + " name :" + gm.getNameFromClient(ctx));
            str = "You cannot surcoinche if you didn't bet at first";
        } else {
            gm.setSurCoinche(true, ctx);
            code = 202;
            str = "You just surcoinched the other player";
            gm.setMessage(gm.getNameFromClient(ctx) + " surcoinched");
        }
        return Game.Answer.newBuilder()
                .setRequest(str)
                .setCode(code)
                .setCards(gm.getDeck(gm.getClientPosition(ctx)))
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
                    .setRequest("You just pass your turn!")
                    .setCode(205)
                    .setType(STANDBY)
                    .setCards(gm.getDeck(gm.getClientPosition(ctx)))
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
