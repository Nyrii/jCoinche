import eu.epitech.jcoinche.jcoincheserver.game.*;
import eu.epitech.jcoinche.protobuf.Game;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;

import static eu.epitech.jcoinche.protobuf.Game.Bidding.Options.*;
import static eu.epitech.jcoinche.protobuf.Game.GameProgress.Command.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by Saursinet on 01/12/2016.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GameManagerTest {
    static private GameManager gm;
    static private CardManager cm;

    Game.DistributionCard deck;
    Game.GameProgress gameProgress;
    Game.Answer answer;
    static ArrayList client;

    @BeforeClass
    public static void initGameManager() throws Exception {
        gm = new GameManager();
        gm.testMode = true;
        client = new ArrayList();
    }

    @Test
    public void testFirstTurnAtoutOffAllPlayer() {
        initFourPlayers();

        biddingPart(0);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .build();
        answer = gm.interpreteGaming(1, gameProgress);
        assertTrue("Error cannot have error when it's not your turn", answer.getCode() == 400);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(Game.Card.newBuilder().setCardType(Game.Card.CardType.INVALID_TYPE).setCardValue(Game.Card.CardValue.INVALID_VALUE).build())
                .build();
        answer = gm.interpreteGaming(0, gameProgress);
        assertTrue("Error cannot have error when card is invalid", answer.getCode() == 421);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(Game.Card.newBuilder().setCardType(Game.Card.CardType.HEARTS).setCardValue(Game.Card.CardValue.SEVEN).build())
                .build();
        answer = gm.interpreteGaming(0, gameProgress);
        assertTrue("Error cannot have error when card is invalid", answer.getCode() == 422);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(MSG)
                .addArguments("Petit message")
                .build();
        answer = gm.interpreteGaming(0, gameProgress);
        assertTrue("Error message didn't send", answer.getCode() <= 300);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(NAME)
                .addArguments("namechange")
                .build();
        answer = gm.interpreteGaming(0, gameProgress);
        assertTrue("Error message didn't send", answer.getCode() <= 300);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(HAND)
                .build();
        answer = gm.interpreteGaming(0, gameProgress);
        assertTrue("Error cannot have hand", answer.getCode() <= 300);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(LAST_TRICK)
                .build();
        answer = gm.interpreteGaming(0, gameProgress);
        assertTrue("Error cannot have last trick", answer.getCode() <= 300);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(INVALID_COMMAND)
                .build();
        answer = gm.interpreteGaming(0, gameProgress);
        assertTrue("Error cannot have invalid command", answer.getCode() == 300);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(QUIT)
                .build();
        answer = gm.interpreteGaming(0, gameProgress);
        assertTrue("Error cannot have quit command", answer.getCode() <= 300);
    }

    public void initFourPlayers() {
        gm.addClient("toto", null);
        gm.addClient("tata", null);
        gm.addClient("titi", null);
        gm.addClient("tutu", null);
        client.add(gm.getPersonByName("toto"));
        client.add(gm.getPersonByName("tata"));
        client.add(gm.getPersonByName("titi"));
        client.add(gm.getPersonByName("tutu"));
    }

    private void biddingPart(int index) {
        gm.setTurn(index);
        gm.setContract(-1, -1);

        answer = bid((Person) client.get(index), true, 85, HEARTS);
        assertTrue("Error bidding didn't pass for player one", answer.getCode() == 200);

        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
        answer = bid((Person) client.get((index + 1) % 4), false, true);
        assertTrue("Error bidding didn't pass for player two", answer.getCode() < 300);

        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
        answer = bid((Person) client.get((index + 2) % 4), true, 100, SA);
        assertTrue("Error bidding didn't pass for player three", answer.getCode() == 200);

        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
        answer = bid((Person) client.get((index + 3) % 4), false, true);
        assertTrue("Error bidding didn't pass for player four", answer.getCode() < 300);

        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
        answer = bid((Person) client.get(index), false, true);
        assertTrue("Error bidding didn't pass for player one again", answer.getCode() < 300);

        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
        answer = bid((Person) client.get((index + 1) % 4), false, true);
        assertTrue("Error bidding didn't pass for player two again", answer.getCode() < 300);

        cm = gm.getCardManager();
        cm.generateCardTest2();
    }

    private Game.Answer bid(Person person, boolean bid, int amount, Game.Bidding.Options option) {
        return gm.interpreteBidding(person, Game.Bidding.newBuilder()
                .setBid(bid)
                .setAmount(amount)
                .setOption(option)
                .build());
    }

    private Game.Answer bid(Person person, boolean bid, boolean pass) {
        return gm.interpreteBidding(person, Game.Bidding.newBuilder()
                .setBid(bid)
                .setPass(pass)
                .build());
    }

    @Test
    public void testGetIndex() {
        gm.setAtout(HEARTS);
        ArrayList cards = new ArrayList();
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.HEARTS).setCardValue(Game.Card.CardValue.EIGHT).build());
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.HEARTS).setCardValue(Game.Card.CardValue.KING).build());
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.HEARTS).setCardValue(Game.Card.CardValue.AS).build());
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.HEARTS).setCardValue(Game.Card.CardValue.TEN).build());
        gm.setCurrentTrick(cards);
        assertTrue("index not good", PartyManager.getIndex() == 2);
        cards = new ArrayList();
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.SPADES).setCardValue(Game.Card.CardValue.EIGHT).build());
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.SPADES).setCardValue(Game.Card.CardValue.KING).build());
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.SPADES).setCardValue(Game.Card.CardValue.AS).build());
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.SPADES).setCardValue(Game.Card.CardValue.TEN).build());
        gm.setCurrentTrick(cards);
        assertTrue("index not good", PartyManager.getIndex() == 2);
        cards = new ArrayList();
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.SPADES).setCardValue(Game.Card.CardValue.EIGHT).build());
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.HEARTS).setCardValue(Game.Card.CardValue.KING).build());
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.SPADES).setCardValue(Game.Card.CardValue.AS).build());
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.SPADES).setCardValue(Game.Card.CardValue.TEN).build());
        gm.setCurrentTrick(cards);
        assertTrue("index not good", PartyManager.getIndex() == 1);
    }

    @Test
    public void testGet() {
        gm = new GameManager();
        gm.testMode = true;
        client = new ArrayList();

        gm.setAtout(HEARTS);
        cm = gm.getCardManager();
        cm.generateCardTest2();
        gm.setTurn(0);
        testIfPlayerOneCanPlayCard(0);
        testIfPlayerTwoCanPlayCard(0);
        testIfPlayerThreeCanPlayCard(0);
        testIfPlayerTwoCannotPlayCard(0);
        testIfPlayerThreeCannotPlayCard(0);
        testIfPlayerFourCanPlayCard(0);
        cm.generateCardTest3();
        testIfPlayerOneCanNotPlayCard(0);
    }

    public void testIfPlayerOneCanNotPlayCard(int index) {
        ArrayList cards = new ArrayList();
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.HEARTS).setCardValue(Game.Card.CardValue.QUEEN).build());
        Game.Card card = Game.Card.newBuilder().setCardType(Game.Card.CardType.SPADES).setCardValue(Game.Card.CardValue.QUEEN).build();
        gm.setCurrentTrick(cards);
        deck = cm.getDeckFromPosition(0);
        assertTrue("Error first card of first player isn't accepted for first trick", PartyManager.checkValidityOfMovement(0, (Game.Card) deck.getCard(0)) == false);
        gm.setAtout(TA);
        assertTrue("Error first card of first player is accepted when second player use it", PartyManager.checkValidityOfMovement(1, (Game.Card) deck.getCard(0)) == false);
        gm.setAtout(HEARTS);
        assertTrue("Error first card of first player is accepted when second player use it", PartyManager.checkValidityOfMovement(1, (Game.Card) deck.getCard(7)) == false);
        gm.setAtout(SPADES);
        assertTrue("Error first card of first player is accepted when second player use it", PartyManager.checkValidityOfMovement(1, (Game.Card) deck.getCard(4)) == false);
        assertTrue("Error first card of first player is accepted when second player use it", PartyManager.checkToutAtout(0, card) == false);
        assertTrue("Error first card of first player is accepted when second player use it", PartyManager.checkWhithoutAtout(0, card) == false);
    }

    public void testIfPlayerOneCanPlayCard(int index) {
        deck = cm.getDeckFromPosition(0);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        answer = gm.interpreteGaming(0, gameProgress);
        assertTrue("Error first card of first player isn't accepted for first trick", answer.getCode() == 200);
        answer = gm.interpreteGaming(1, gameProgress);
        assertTrue("Error first card of first player is accepted when second player use it", answer.getCode() == 400);
        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
    }

    public void testIfPlayerTwoCanPlayCard(int index) {
        deck = cm.getDeckFromPosition(1);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        answer = gm.interpreteGaming(1, gameProgress);
        assertTrue("Error first card of second player greater than first card of first player isn't accepted", answer.getCode() == 200);
        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
    }

    public void testIfPlayerThreeCanPlayCard(int index) {
        deck = cm.getDeckFromPosition(2);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        answer = gm.interpreteGaming(2, gameProgress);
        assertTrue("Error first card of third player greater than first card of second player isn't accepted", answer.getCode() == 200);
        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
    }

    public void testIfPlayerTwoCannotPlayCard(int index) {
        deck = cm.getDeckFromPosition(1);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        answer = gm.interpreteGaming(1, gameProgress);
        assertTrue("Error first card of second player greater than first card of first player is accepted", answer.getCode() == 400);
    }

    public void testIfPlayerThreeCannotPlayCard(int index) {
        deck = cm.getDeckFromPosition(2);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        answer = gm.interpreteGaming(2, gameProgress);
        assertTrue("Error first card of third player greater than first card of second player is accepted", answer.getCode() == 400);
    }

    public void testIfPlayerFourCanPlayCard(int index) {
        deck = cm.getDeckFromPosition(3);
        gameProgress = Game.GameProgress.newBuilder()
                .setCommand(PLAY)
                .setCard(deck.getCard(index))
                .build();
        //first time he will play king atout after Jack which is the biggest so he cannot go up but the card has to be accept
        answer = gm.interpreteGaming(3, gameProgress);
        assertTrue("Error first card of fourth player greater than first card of third player isn't accepted", answer.getCode() == 200);
        gm.setTurn(gm.getTurn() == 3 ? 0 : gm.getTurn() + 1);
    }

    @Test
    public void checkContractRespected() {
        gm = new GameManager();
        gm.testMode = true;
        client = new ArrayList();

        PartyManager.setGm(gm);

        //first verif
        gm.setPersonWhoCapot(0);
        gm.setNbTrick2(0);
        gm.setNbTrick4(0);
        assertTrue("Error capot from first team isn't respected", PartyManager.checkIfConctractIsRespectedTeam1() == 250);
        gm.setPersonWhoCapot(-1);
        gm.setPersonWhoGenerale(0);
        gm.setNbTrick1(8);
        assertTrue("Error generale from first team isn't respected", PartyManager.checkIfConctractIsRespectedTeam1() == 500);
        gm.setPersonWhoGenerale(-1);
        gm.setPersonWhoBet(0);
        gm.setScoreTeamParty1(2);
        gm.setContract(1, 0);
        gm.setPersonWhoCoinche(1);
        assertTrue("Error coinche on first team isn't respected", PartyManager.checkIfConctractIsRespectedTeam1() == 4);
        gm.setPersonWhoSurCoinche(0);
        assertTrue("Error surcoinche from first team isn't respected", PartyManager.checkIfConctractIsRespectedTeam1() == 6);
        gm.setPersonWhoBet(1);
        gm.setScoreTeamParty1(1);
        gm.setContract(2, 1);
        gm.setPersonWhoCoinche(0);
        gm.setPersonWhoSurCoinche(-1);
        assertTrue("Error coinche on first team when contract isn't respected is false", PartyManager.checkIfConctractIsRespectedTeam1() == 328);
        gm.setPersonWhoSurCoinche(2);
        assertTrue("Error surcoinche from first team isn't respected", PartyManager.checkIfConctractIsRespectedTeam1() == 656);
        //second verif
        gm.reinitAllValues();
        gm.setPersonWhoCapot(1);
        gm.setNbTrick1(0);
        gm.setNbTrick3(0);
        assertTrue("Error capot from first team isn't respected", PartyManager.checkIfConctractIsRespectedTeam2() == 250);
        gm.setPersonWhoCapot(-1);
        gm.setPersonWhoGenerale(1);
        gm.setNbTrick2(8);
        assertTrue("Error generale from first team isn't respected", PartyManager.checkIfConctractIsRespectedTeam2() == 500);
        gm.setPersonWhoGenerale(-1);
        gm.setPersonWhoBet(1);
        gm.setScoreTeamParty2(2);
        gm.setContract(1, 1);
        gm.setPersonWhoCoinche(0);
        assertTrue("Error coinche on first team isn't respected", PartyManager.checkIfConctractIsRespectedTeam2() == 4);
        gm.setPersonWhoSurCoinche(1);
        assertTrue("Error surcoinche from first team isn't respected", PartyManager.checkIfConctractIsRespectedTeam2() == 6);
        gm.setPersonWhoBet(0);
        gm.setScoreTeamParty2(1);
        gm.setContract(2, 0);
        gm.setPersonWhoCoinche(1);
        gm.setPersonWhoSurCoinche(-1);
        assertTrue("Error coinche on first team when contract isn't respected is false", PartyManager.checkIfConctractIsRespectedTeam2() == 328);
        gm.setPersonWhoSurCoinche(1);
        assertTrue("Error surcoinche from first team isn't respected", PartyManager.checkIfConctractIsRespectedTeam2() == 656);


        PartyManager.updateScore(2);
        PartyManager.updateScore(1);
    }

    @Test
    public void testNumberPointTrick() {
        gm = new GameManager();
        gm.testMode = true;
        client = new ArrayList();

        PartyManager.setGm(gm);
        ArrayList cards = new ArrayList();
        cards.add(Game.Card.newBuilder().setCardType(Game.Card.CardType.HEARTS).setCardValue(Game.Card.CardValue.AS).build());
        gm.setCurrentTrick(cards);
        PartyManager.numberPointOfTrickToutAtout();
        gm.setCurrentTrick(cards);
        assertTrue("Index about more powerful card is not the good one", PartyManager.takeIndexFromTrick() == 0);
        gm.setAtout(TA);
        assertTrue("Index about more powerful card is not the good one", PartyManager.takeIndexFromTrick() == 0);
        gm.setAtout(HEARTS);
        PartyManager.numberPointOfTrick(cards);
        gm.setAtout(SPADES);
        PartyManager.numberPointOfTrick(cards);
    }

    @Test
    public void interpreteBidding() {
        gm = new GameManager();
        gm.testMode = true;
        client = new ArrayList();
        gm.giveCardToAllPlayers();
        initFourPlayers();

        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setSurcoinche(true).build());
        assertTrue("Wrong code value in sur coinche error", answer.getCode() == 453);
        gm.setPersonWhoBet(0);
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setSurcoinche(true).build());
        assertTrue("Wrong code value in sur coinche validate", answer.getCode() == 202);
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setSurcoinche(true).build());
        assertTrue("Wrong code value in sur coinche error", answer.getCode() == 452);
        gm.reinitAllValues();
        gm.setContract(-1, 0);
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setCoinche(true).build());
        assertTrue("Wrong code value in coinche error1", answer.getCode() == 443);
        gm.setContract(0, 0);
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setCoinche(true).build());
        assertTrue("Wrong code value in coinche error2", answer.getCode() == 444);
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(1), Game.Bidding.newBuilder().setCoinche(true).build());
        assertTrue("Wrong code value in coinche validate", answer.getCode() == 205);
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setCoinche(true).build());
        assertTrue("Wrong code value in coinche error3", answer.getCode() == 444);
        gm.reinitAllValues();
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setContract(Game.Bidding.Contract.CAPOT).setBid(true).setOption(TA).build());
        assertTrue("Wrong code value in capot validate ", answer.getCode() == 203);
        gm.reinitAllValues();
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setContract(Game.Bidding.Contract.GENERALE).setBid(true).setOption(TA).build());
        assertTrue("Wrong code value in generale validate", answer.getCode() == 204);
        gm.reinitAllValues();
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setContract(Game.Bidding.Contract.AMOUNT).setBid(true).setOption(TA).setAmount(70).build());
        assertTrue("Wrong code value in bidding error1", answer.getCode() == 432);
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setContract(Game.Bidding.Contract.AMOUNT).setBid(true).setOption(TA).setAmount(170).build());
        assertTrue("Wrong code value in bidding error2", answer.getCode() == 433);
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setContract(Game.Bidding.Contract.AMOUNT).setBid(true).setOption(TA).setAmount(100).build());
        assertTrue("Wrong code value in bidding validate", answer.getCode() == 200);
        answer = BiddingManager.interpreteBidding(gm, (Person) client.get(0), Game.Bidding.newBuilder().setContract(Game.Bidding.Contract.AMOUNT).setBid(true).setOption(TA).setAmount(100).build());
        assertTrue("Wrong code value in bidding error3", answer.getCode() == 434);
    }

    @Test
    public void testNameManager() {
        gm = new GameManager();
        gm.testMode = true;
        client = new ArrayList();
        assertTrue("party has to not begin", !NameManager.partyCanBegin(client));
        assertTrue("name already in use", !NameManager.nameAlreadyInUse(client, "tata"));
        gm.giveCardToAllPlayers();
        initFourPlayers();

        assertTrue("party has to begin", NameManager.partyCanBegin(client));
        assertTrue("party has to not begin", !NameManager.partyCanBegin(null));
        ((Person) client.get(0)).setName("0xtoto");
        assertTrue("party has to not begin", !NameManager.partyCanBegin(client));
        assertTrue("name already in use", NameManager.nameAlreadyInUse(client, "tata"));
        assertTrue("name already in use", !NameManager.nameAlreadyInUse(null, "tata"));
        assertTrue(!NameManager.addName(client, "", ""));
        assertTrue(!NameManager.addName(client, "", ";toto"));
        assertTrue(NameManager.addName(client, "tata", "toto"));
        client = new ArrayList();
        initFourPlayers();
        assertTrue(NameManager.setName(gm, client, "toto", "tata").getCode() == 403);
        assertTrue(NameManager.setName(gm, client, "toto", "plage").getCode() == 200);
        assertTrue(NameManager.setName(gm, client, "toto", ";toto").getCode() == 402);

        assertTrue(NameManager.changeName(gm, client, "tata", 0).getCode() == 403);
        assertTrue(NameManager.changeName(gm, client, "plage", 0).getCode() == 200);
        assertTrue(NameManager.changeName(gm, client, ";toto", 0).getCode() == 402);
    }
}
