package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.protobuf.Game;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.BIDDING;
import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.GAME;

/**
 * Created by Saursinet on 06/12/2016.
 */
public class BiddingManager {
    private static Game.Answer setResponseifBid(GameManager gm, Person person, Game.Bidding bidding, int contract) {
        int code;
        String str;
        Game.Answer.Type typeAnswer = BIDDING;

        if (bidding.getContract() == Game.Bidding.Contract.CAPOT) {
            code = 203;
            gm.setAtout(bidding.getOption());
            str = "Just annonced capot";
            gm.setCapot(true, person.getPos());
            typeAnswer = GAME;
            gm.setMessage(person.getName() + " announced capot at " + bidding.getOption());
        } else if (bidding.getContract() == Game.Bidding.Contract.GENERALE) {
            code = 204;
            gm.setAtout(bidding.getOption());
            str = "Just annonced generale";
            gm.setGenerale(true, person.getPos());
            typeAnswer = GAME;
            gm.setMessage(person.getName() + " announced capot at " + bidding.getOption());
        } else if (bidding.getAmount() < 80) {
            code = 432;
            str = "The minimum bidding value is 80.";
        } else if (bidding.getAmount() > 160) {
            code = 433;
            str = "The maximum bidding value is 160.";
        } else if (bidding.getAmount() <= contract) {
            code = 434;
            str = "A player already bidded " + contract;
        } else {
            code = 200;
            str = "Bidding okay";
            gm.setMessage(person.getName() + " announced a bidding with " + bidding.getAmount() + " at " + bidding.getOption());
            gm.setContract(bidding.getAmount(), person.getPos());
            gm.setAtout(bidding.getOption());
            typeAnswer = GAME;
        }
        return Game.Answer.newBuilder()
                .setRequest(str)
                .setCode(code)
                .setCards(gm.getDeck(person.getPos()))
                .setType(typeAnswer)
                .build();
    }

    private static Game.Answer setResponseIfCoinche(GameManager gm, int contract, Person person) {
        int code;
        String str;
        Game.Answer.Type typeAnswer = BIDDING;

        if (gm.getCoinche()) {
            code = 442;
            str = "A player already coinched";
        } else if (contract == -1) {
            code = 443;
            str = "Coinche cannot be announced until a contract is made";
        } else if (gm.getPersonWhoBet() == person.getPos() || gm.arePartner(gm.getPersonWhoBet(), person.getPos())) {
            code = 444;
            str = "you cannot coinche a contract that you or your partner already coinched";
        } else {
            gm.setCoinche(true, person.getPos());
            code = 200;
            str = "Coinche has been counted";
            gm.setMessage(person.getName() + " coinched the other team");
            typeAnswer = GAME;
        }
        return Game.Answer.newBuilder()
                .setRequest(str)
                .setCode(code)
                .setCards(gm.getDeck(person.getPos()))
                .setType(typeAnswer)
                .build();
    }

    private static Game.Answer setResponseIfSurCoinche(GameManager gm, Person person) {
        int code;
        String str;
        Game.Answer.Type typeAnswer = BIDDING;

        if (gm.getSurCoinche()) {
            code = 452;
            str = "A player already surcoinched";
        } else if (gm.getPersonWhoBet() != person.getPos()) {
            code = 453;
            System.out.println("person who bet = " + gm.getPersonWhoBet() + "  et pos = " + person.getPos() + " name :" + person.getName());
            str = "Surcoinche cannot be announced until coinche is.";
        } else {
            gm.setSurCoinche(true, person.getPos());
            code = 202;
            str = "You just surcoinched the other player";
            gm.setMessage(person.getName() + " surcoinched");
            typeAnswer = GAME;
        }
        return Game.Answer.newBuilder()
                .setRequest(str)
                .setCode(code)
                .setCards(gm.getDeck(person.getPos()))
                .setType(typeAnswer)
                .build();
    }

    public static Game.Answer interpreteBidding(GameManager gm, Person person, Game.Bidding bidding, int contract) {
        Game.Answer answer;
        boolean pastInElse = false;

        if (bidding.getBid()) {
            answer = setResponseifBid(gm, person, bidding, contract);
        } else if (bidding.getCoinche()) {
            answer = setResponseIfCoinche(gm, contract, person);
        } else if (bidding.getSurcoinche()) {
            answer = setResponseIfSurCoinche(gm, person);
        } else {
            answer = Game.Answer.newBuilder()
                    .setRequest("You passed your turn.")
                    .setCode(200)
                    .setType(GAME)
                    .setCards(gm.getDeck(person.getPos()))
                    .build();
            gm.addInactiveTurn(gm.getNbTurnInactive() + 1);
            pastInElse = true;
            gm.setMessage("just pass his/her turn."); // nom ?
        }
        if (!pastInElse)
            gm.addInactiveTurn(0);
        gm.checkIfPartyCanRun();
        return answer;
    }
}
