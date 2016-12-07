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
            code = 400;
            str = "A person has already coinche";
        } else if (contract == -1) {
            code = 400;
            str = "There is no contract to coinche";
        } else if (gm.getPersonWhoBet() == person.getPos()) {
            code = 400;
            str = "You cannot coinche yourself";
        } else if (gm.arePartner(gm.getPersonWhoBet(), person.getPos())) {
            code = 400;
            str = "You cannot coinche your partner";
        } else {
            gm.setCoinche(true, person.getPos());
            code = 201;
            str = "You just coinched the other player";
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
            code = 400;
            str = "A person has already surcoinche";
        } else if (gm.getPersonWhoBet() != person.getPos()) {
            code = 400;
            System.out.println("person who bet = " + gm.getPersonWhoBet() + "  et pos = " + person.getPos() + " name :" + person.getName());
            str = "You cannot surcoinche if you didn't bet at first";
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
                    .setRequest("You just pass your turn!")
                    .setCode(205)
                    .setType(GAME)
                    .setCards(gm.getDeck(person.getPos()))
                    .build();
            gm.addInactiveTurn(gm.getNbTurnInactive() + 1);
            pastInElse = true;
            gm.setMessage("just pass it's turn.");
        }
        if (!pastInElse)
            gm.addInactiveTurn(0);
        System.out.println(gm.getNbTurnInactive());
        gm.checkIfPartyCanRun();
        return answer;
    }
}
