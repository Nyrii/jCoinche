package eu.epitech.jcoinche.jcoincheserver.game;

import eu.epitech.jcoinche.protobuf.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.GAME;
import static eu.epitech.jcoinche.protobuf.Game.Answer.Type.LEAVE;
import static eu.epitech.jcoinche.protobuf.Game.Bidding.Options.SA;
import static eu.epitech.jcoinche.protobuf.Game.Bidding.Options.TA;

/**
 * Created by Saursinet on 06/12/2016.
 */
public class PartyManager {
    public static void setGm(GameManager gm) {
        PartyManager.gm = gm;
    }

    static private GameManager gm = null;

    static public Game.Answer interpreteGaming(int clientPosition, Game.GameProgress game, GameManager gm) {
        PartyManager.gm = gm;
        Game.Answer answer = Game.Answer.newBuilder()
                .setRequest("")
                .setCode(0)
                .setType(GAME)
                .build();

        switch (game.getCommand()) {

            case MSG:
                answer = gm.sendMessageToAllPersonInGame(clientPosition, game.getArguments(0));
                break;

            case NAME:
                answer = gm.changeName(game.getArguments(0), clientPosition);
                answer = Game.Answer.newBuilder()
                        .setRequest(answer.getRequest())
                        .setCode(answer.getCode())
                        .setType(GAME)
                        .build();
                break;

            case PLAY:
                answer = manageGame(clientPosition, game);
                break;

            case HAND:
                answer = sendHandToPlayer(clientPosition);
                break;

            case LAST_TRICK:
                answer = showLastTrick();
                break;

            case INVALID_COMMAND:
                answer = sendInvalidCommand();
                break;

            case QUIT:
                System.out.println("User quit");
                answer = Game.Answer.newBuilder()
                        .setRequest("user quit the game")
                        .setCode(300)
                        .setType(LEAVE)
                        .build();
                break;
        }
        return answer;
    }

    private static Game.Answer manageGame(int clientPosition, Game.GameProgress game) {
        Game.Answer answer;

        Game.Answer.Type type = GAME;
        int code = 400;
        if (gm.getTurn() != clientPosition) {
            gm.setMessage("Please, wait for your turn.");
            type = Game.Answer.Type.NONE;
        } else {
            Game.DistributionCard deck = gm.getDeck(clientPosition);
            boolean found = false;
            for (Game.Card card : deck.getCardList()) {
                if (card.getCardType() == game.getCard().getCardType() &&
                        card.getCardValue() == game.getCard().getCardValue())
                    found = true;
            }
            if (game.getCard().getCardType() == Game.Card.CardType.INVALID_TYPE || game.getCard().getCardValue() == Game.Card.CardValue.INVALID_VALUE) {
                code = 421;
                gm.setMessage("This card is not in your deck.");
            }
            else if (!found) {
                code = 422;
                gm.setMessage("This card doesn't belong to you.");
            }
            else if (!checkValidityOfMovement(clientPosition, game.getCard()))
                ;
            else {
                gm.getCurrentTrick().add(game.getCard());
                deleteCardFromDeck(game.getCard(), deck, clientPosition);
                gm.setMessage("Your turn has been taken into account.");
                code = 200;
                type = Game.Answer.Type.NONE;
            }
        }
        answer = Game.Answer.newBuilder()
                .setRequest(gm.getMessage())
                .setCode(code)
                .setType(type)
                .build();
        if (code == 200) {
            Game.Card card = game.getCard();

            if (card != null) {
                gm.sendMessageToAllPersonInGame(clientPosition, "played " + card.getCardValue() + " OF " + card.getCardType());
            } else {
                gm.sendMessageToAllPersonInGame(clientPosition, "An error occured during the turn.");
            }
        }
        return answer;
    }

    public static int getIndex() {
        int index;
        if (gm.getAtout() == Game.Bidding.Options.TA || gm.getAtout() == SA)
            index = takeIndexFromTrick();
        else if (isAtout((Game.Card) gm.getCurrentTrick().get(0))) {
            index = gm.getCurrentTrick().indexOf(biggestCardInTrickAtout(gm.getCurrentTrick()));
        } else {
            index = gm.getCurrentTrick().indexOf(biggestCardInTrick(gm.getCurrentTrick()));
            boolean isAtout = false;
            for (Object card : gm.getCurrentTrick()) {
                if (((Game.Card) card).getCardType() == Game.Card.CardType.valueOf(gm.getAtout().toString())) {
                    isAtout = true;
                }
            }
            if (isAtout)
                index = gm.getCurrentTrick().indexOf(biggestCardInTrickAtout(gm.getCurrentTrick()));
        }
        return index;
    }

    private static void checkEndOfGameOrParty() {
        if (gm.getDeck(0).getCardList().size() == 0) {
            int amount1;
            int amount2;
            if ((amount1 = checkIfConctractIsRespectedTeam1()) != 0) {
                gm.sendMessageToAllPersonInGame("Team one won the party with " + amount1);
                System.out.println("Team one won the party with " + amount1);
            }
            if ((amount2 = checkIfConctractIsRespectedTeam2()) != 0) {
                gm.sendMessageToAllPersonInGame("Team two won the party with " + amount1);
                System.out.println("Team two won the party with " + amount2);
            }
            gm.setScoreTeam1(amount1 + gm.getScoreTeam1());
            gm.setScoreTeam2(amount2 + gm.getScoreTeam2());
            System.out.println("The score is now team 1 = " + gm.getScoreTeam1() + " , team 2 = " + gm.getScoreTeam2());
            gm.sendMessageToAllPersonInGame("The score is now team 1 = " + gm.getScoreTeam1() + " , team 2 = " + gm.getScoreTeam2());

            if (gm.getScoreTeam1() >= 700) {
                gm.sendMessageToAllPersonInGame(((Person) gm.getClient().get(0)).getName() + " et " + ((Person) gm.getClient().get(2)).getName() + " won the game with " + gm.getScoreTeam1() + " points");
                System.out.println(((Person) gm.getClient().get(0)).getName() + " et " + ((Person) gm.getClient().get(2)).getName() + " won the game with " + gm.getScoreTeam1() + " points");
                gm.setEnd(true);
            } else if (gm.getScoreTeam2() >= 700) {
                gm.sendMessageToAllPersonInGame(((Person) gm.getClient().get(1)).getName() + " et " + ((Person) gm.getClient().get(3)).getName() + " won the game with " + gm.getScoreTeam2() + " points");
                System.out.println(((Person) gm.getClient().get(1)).getName() + " et " + ((Person) gm.getClient().get(3)).getName() + " won the game with " + gm.getScoreTeam2() + " points");
                gm.setEnd(true);
            }

            gm.setGame(false);
            gm.setBidding(true);
            gm.giveCardToAllPlayers();
        }
    }

    public static void endLastTrick() {
        int index = getIndex();
        int posPlayer = (index + gm.getTurn()) % 4;
        gm.sendMessageToAllPersonInGame(((Person) gm.getClient().get(posPlayer)).getName() + ": won the last trick");
        gm.setTurn(posPlayer);
        gm.setTurnPos(gm.getTurn());
        updateScore(posPlayer);
        gm.setLastTrick(gm.getCurrentTrick());
        gm.setCurrentTrick(new ArrayList());
        if (gm.getEnd() && !gm.isTestMode()) {
            gm.sendMessageToAllPersonInGame("I delete the room for now because game is over"); // ?
            for (Object person : gm.client) {
                ((Person) person).getCtx().close();
            }
        }
        checkEndOfGameOrParty();
    }

    public static int checkIfConctractIsRespectedTeam1() {
        if (((gm.getPersonWhoCapot() == 0 || gm.getPersonWhoCapot() == 2) && gm.getNbTrick2() + gm.getNbTrick4() == 0) ||
                ((gm.getPersonWhoCapot() == 1 || gm.getPersonWhoCapot() == 3) && gm.getNbTrick1() + gm.getNbTrick3() != 0))
            return 250;
        if ((gm.getPersonWhoGenerale() == 0 && gm.getNbTrick1() == 8) || (gm.getPersonWhoGenerale() == 2 && gm.getNbTrick3() == 8) ||
                (gm.getPersonWhoGenerale() == 1 && gm.getNbTrick2() != 8) || (gm.getPersonWhoGenerale() == 3 && gm.getNbTrick4() != 8))
            return 500;
        if ((gm.getPersonWhoBet() == 0 || gm.getPersonWhoBet() == 2) && gm.getScoreTeamParty1() >= gm.getContract()) {
            if (gm.getPersonWhoCoinche() != -1 && gm.getPersonWhoSurCoinche() != -1)
                return gm.getContract() * 4 + gm.getScoreTeamParty1();
            else if (gm.getPersonWhoCoinche() != -1)
                return gm.getContract() * 2 + gm.getScoreTeamParty1();
            return gm.getContract() + gm.getScoreTeamParty1();
        }
        if ((gm.getPersonWhoBet() == 1 || gm.getPersonWhoBet() == 3) && gm.getScoreTeamParty2() < gm.getContract()) {
            if (gm.getPersonWhoCoinche() != -1 && gm.getPersonWhoSurCoinche() != -1)
                return (162 + gm.getContract()) * 4;
            else if (gm.getPersonWhoCoinche() != -1)
                return (162 + gm.getContract()) * 2;
            return 162 + gm.getContract();
        }
        return 0;
    }

    public static int checkIfConctractIsRespectedTeam2() {
        if (((gm.getPersonWhoCapot() == 0 || gm.getPersonWhoCapot() == 2) && gm.getNbTrick2() + gm.getNbTrick4() != 0) ||
                ((gm.getPersonWhoCapot() == 1 || gm.getPersonWhoCapot() == 3) && gm.getNbTrick1() + gm.getNbTrick3() == 0))
            return 250;
        if ((gm.getPersonWhoGenerale() == 0 && gm.getNbTrick1() != 8) || (gm.getPersonWhoGenerale() == 2 && gm.getNbTrick3() != 8) ||
                (gm.getPersonWhoGenerale() == 1 && gm.getNbTrick2() == 8) || (gm.getPersonWhoGenerale() == 3 && gm.getNbTrick4() == 8))
            return 500;
        if ((gm.getPersonWhoBet() == 1 || gm.getPersonWhoBet() == 3) && gm.getScoreTeamParty2() >= gm.getContract()) {
            if (gm.getPersonWhoCoinche() != -1 && gm.getPersonWhoSurCoinche() != -1)
                return gm.getContract() * 4 + gm.getScoreTeamParty2();
            else if (gm.getPersonWhoCoinche() != -1)
                return gm.getContract() * 2 + gm.getScoreTeamParty2();
            return gm.getContract() + gm.getScoreTeamParty2();
        }
        if ((gm.getPersonWhoBet() == 0 || gm.getPersonWhoBet() == 2) && gm.getScoreTeamParty1() < gm.getContract()) {
            if (gm.getPersonWhoCoinche() != -1 && gm.getPersonWhoSurCoinche() != -1)
                return (162 + gm.getContract()) * 4;
            else if (gm.getPersonWhoCoinche() != -1)
                return (162 + gm.getContract()) * 2;
            return 162 + gm.getContract();
        }
        return 0;
    }

    public static void updateScore(int posPlayer) {
        int count = gm.getAtout() == TA ? numberPointOfTrickToutAtout() : gm.getAtout() == SA ? numberPointOfTrickSansAtout() : numberPointOfTrick(gm.getCurrentTrick());

        if (posPlayer == 0 || posPlayer == 2) {
            gm.setScoreTeamParty1(count + gm.getScoreTeamParty1());
            if (posPlayer == 0)
                gm.setNbTrick1(gm.getNbTrick1() + 1);
            else
                gm.setNbTrick3(gm.getNbTrick3() + 1);
            if (gm.getDeck(0).getCardList().size() == 0)
                gm.setScoreTeamParty1(10 + gm.getScoreTeamParty1());
        }
        else {
            gm.setScoreTeamParty2(gm.getScoreTeamParty2() + count);
            if (posPlayer == 1)
                gm.setNbTrick2(gm.getNbTrick2() + 1);
            else
                gm.setNbTrick4(gm.getNbTrick4() + 1);
            if (gm.getDeck(0).getCardList().size() == 0)
                gm.setScoreTeamParty2(10 + gm.getScoreTeamParty2());
        }
    }

    public static int numberPointOfTrickToutAtout() {
        int score = 0;
        for (Object card : gm.getCurrentTrick()) {
            score += (int) gm.getValueCardsAtout().get(((Game.Card) card).getCardValue());
        }
        return score;
    }

    private static int numberPointOfTrickSansAtout() {
        int score = 0;
        for (Object card : gm.getCurrentTrick()) {
            score += (int) gm.getValueCards().get(((Game.Card) card).getCardValue());
        }
        return score;
    }

    public static int takeIndexFromTrick() {
        if (gm.getAtout() == Game.Bidding.Options.TA)
            return gm.getCurrentTrick().indexOf(biggestCardInTrickToutAtout(gm.getCurrentTrick()));
        else
            return gm.getCurrentTrick().indexOf(biggestCardInTrickSansAtout(gm.getCurrentTrick()));
    }

    public static int numberPointOfTrick(ArrayList currentTrick) {
        int score = 0;
        for (Object card : currentTrick) {
            if (isAtout((Game.Card) card)) {
                score += (int) gm.getValueCardsAtout().get(((Game.Card) card).getCardValue());
            } else {
                score += (int) gm.getValueCards().get(((Game.Card) card).getCardValue());
            }
        }
        return score;
    }

    public static boolean checkValidityOfMovement(int clientPosition, Game.Card card) {
        if (gm.getCurrentTrick().size() == 0)
            return true;
        if (gm.getAtout() == Game.Bidding.Options.TA)
            return checkToutAtout(clientPosition, card);
        if (gm.getAtout() == SA)
            return checkWhithoutAtout(clientPosition, card);
        if (isAtout((Game.Card) gm.getCurrentTrick().get(0))) {
            if (isAtout(card)) {
                if (!isBiggerValueAtout(card.getCardValue(), biggestCardInTrickAtout(gm.getCurrentTrick()).getCardValue()))
                    if (!checkIfPlayerCannotGoUp(biggestCardInTrickAtout(gm.getCurrentTrick()), gm.getDeck(clientPosition))) {
                        System.err.println("player can put atout bigger than others");
                        gm.setMessage("player can put atout bigger than others");
                        return false;
                    }
            } else if (hasOneTypeOfCard(clientPosition, Game.Card.CardType.valueOf(gm.getAtout().toString()))) {
                System.err.println("player has atout but put other colour");
                gm.setMessage("player has atout but put other colour");
                return false;
            }
        } else {
            if (((Game.Card) gm.getCurrentTrick().get(0)).getCardType() != card.getCardType()) {
                if (hasOneTypeOfCard(clientPosition, ((Game.Card) gm.getCurrentTrick().get(0)).getCardType())) {
                    System.err.println("player did not put good colour");
                    gm.setMessage("player did not put good colour");
                    return false;
                }
                else if (isAtout(card) && isAtout(biggestCardInTrickAtout(gm.getCurrentTrick())) && !checkIfPlayerCannotGoUp(biggestCardInTrickAtout(gm.getCurrentTrick()), gm.getDeck(clientPosition))) {
                    System.err.println("colour isn't atout but player can cut with biggest card");
                    gm.setMessage("colour isn't atout but player can cut with biggest card");
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkToutAtout(int clientPosition, Game.Card card) {
        if (((Game.Card) gm.getCurrentTrick().get(0)).getCardType() != card.getCardType() &&
                hasOneTypeOfCard(clientPosition, ((Game.Card) gm.getCurrentTrick().get(0)).getCardType())) {
            System.err.println("player did not put good colour");
            gm.setMessage("player did not put good colour");
            return false;
        }
        if (!isBiggerValueAtout(card.getCardValue(), biggestCardInTrickToutAtout(gm.getCurrentTrick()).getCardValue()) &&
                !checkIfPlayerCannotGoUpToutAtout(biggestCardInTrickToutAtout(gm.getCurrentTrick()), gm.getDeck(clientPosition))) {
            System.err.println("With tout atout you have to play biggest card than the other" + biggestCardInTrickToutAtout(gm.getCurrentTrick()));
            gm.setMessage("With tout atout you have to play biggest card than the other");
            return false;
        }
        return true;
    }

    public static boolean checkWhithoutAtout(int clientPosition, Game.Card card) {
        if (((Game.Card) gm.getCurrentTrick().get(0)).getCardType() != card.getCardType()) {
            if (hasOneTypeOfCard(clientPosition, ((Game.Card) gm.getCurrentTrick().get(0)).getCardType())) {
                System.err.println("player did not put good colour");
                gm.setMessage("player did not put good colour");
                return false;
            }
        }
        return true;
    }

    private static boolean checkIfPlayerCannotGoUp(Game.Card card, Game.DistributionCard deck) {
        for (Object cardPlayer : deck.getCardList()) {
            if (isBiggerValueAtout(((Game.Card) cardPlayer).getCardValue(), card.getCardValue()))
                return false;
        }
        return true;
    }

    private static boolean checkIfPlayerCannotGoUpToutAtout(Game.Card card, Game.DistributionCard deck) {
        for (Object cardPlayer : deck.getCardList()) {
            if (((Game.Card) cardPlayer).getCardType() == card.getCardType() &&
                    isBiggerValueAtout(((Game.Card) cardPlayer).getCardValue(), card.getCardValue()))
                return false;
        }
        return true;
    }

    private static Game.Card biggestCardInTrick(ArrayList currentTrick) {
        Game.Card firstCard = ((Game.Card) currentTrick.get(0));

        for (Object card : currentTrick) {
            if (((Game.Card) card).getCardType() == firstCard.getCardType() &&
                    (isBiggerValue(((Game.Card) card).getCardValue(), firstCard.getCardValue()) ||
                            isAtout((Game.Card) card)))
                firstCard = (Game.Card) card;
        }
        return firstCard;
    }

    private static Game.Card biggestCardInTrickAtout(ArrayList currentTrick) {
        Game.Card firstCard = ((Game.Card) currentTrick.get(0));

        for (Object card : currentTrick) {
            if (isAtout((Game.Card) card) && (!isAtout(firstCard) ||
                    isBiggerValueAtout(((Game.Card) card).getCardValue(), firstCard.getCardValue())))
                firstCard = (Game.Card) card;
        }
        return firstCard;
    }

    private static Game.Card biggestCardInTrickToutAtout(ArrayList currentTrick) {
        Game.Card firstCard = ((Game.Card) currentTrick.get(0));

        for (Object card : currentTrick) {
            if (((Game.Card) card).getCardType() == firstCard.getCardType() &&
                    isBiggerValueAtout(((Game.Card) card).getCardValue(), firstCard.getCardValue()))
                firstCard = (Game.Card) card;
        }
        return firstCard;
    }

    private static Object biggestCardInTrickSansAtout(ArrayList currentTrick) {
        Game.Card firstCard = ((Game.Card) currentTrick.get(0));

        for (Object card : currentTrick) {
            if (((Game.Card) card).getCardType() == firstCard.getCardType() &&
                    isBiggerValue(((Game.Card) card).getCardValue(), firstCard.getCardValue()))
                firstCard = (Game.Card) card;
        }
        return firstCard;
    }

    private static boolean isBiggerValue(Game.Card.CardValue cardValue, Game.Card.CardValue value) {
        return ((int) gm.getValueCards().get(cardValue)) > ((int) gm.getValueCards().get(value));
    }

    private static boolean isBiggerValueAtout(Game.Card.CardValue cardValue, Game.Card.CardValue value) {
        return ((int) gm.getValueCardsAtout().get(cardValue)) > ((int) gm.getValueCardsAtout().get(value));
    }

    private static void deleteCardFromDeck(Game.Card card, Game.DistributionCard deck, int clientPosition) {
        ArrayList tmp = new ArrayList();
        for (Object cards : deck.getCardList()) {
            if (!(card.getCardType() == ((Game.Card) cards).getCardType() &&
                    card.getCardValue() == ((Game.Card) cards).getCardValue()) ){
                tmp.add(cards);
            }
        }
        gm.setDeck(clientPosition, deck);
        Game.DistributionCard cards = Game.DistributionCard
                .newBuilder()
                .addAllCard(tmp)
                .build();
        gm.setDeck(clientPosition, cards);
    }

    private static boolean isAtout(Game.Card card) {
        return card.getCardType() == Game.Card.CardType.valueOf(gm.getAtout().toString());
    }

    private static boolean hasOneTypeOfCard(int clientPosition, Game.Card.CardType type) {
        Game.DistributionCard deck = gm.getDeck(clientPosition);
        for (Game.Card card : deck.getCardList()) {
            if (card.getCardType() == type)
                return true;
        }
        return false;
    }

    private static Game.Answer sendInvalidCommand() {
        return Game.Answer.newBuilder()
                .setRequest("Invalid command")
                .setCode(300)
                .setType(GAME)
                .build();
    }

    private static Game.Answer showLastTrick() {
        return Game.Answer.newBuilder()
                .setRequest("There is the last trick played :" + gm.getLastTrick().toString())
                .setCode(300)
                .setType(GAME)
                .build();
    }

    private static Game.Answer sendHandToPlayer(int clientPosition) {
        if (gm.getDeck(clientPosition) != null) {
            List<Game.Card> deck = new ArrayList<Game.Card>(gm.getDeck(clientPosition).getCardList());
            Collections.sort(deck, new Comparator<Game.Card>() {
                @Override
                public int compare(Game.Card left, Game.Card right) {
                    int stringComparison = left.getCardType().toString().compareTo(right.getCardType().toString());
                    if (stringComparison == 0) {
                        return (left.getCardValue().compareTo(right.getCardValue()));
                    }
                    return stringComparison;
                }
            });
            String hand = "";
            for (Object card : deck) {
                String entireCard = new StringBuilder()
                        .append(((Game.Card) card).getCardValue())
                        .append(" OF ")
                        .append(((Game.Card) card).getCardType())
                        .toString();
                hand += "\n" + entireCard;
            }
            return Game.Answer.newBuilder()
                    .setRequest("Here are your cards :" + hand)
                    .setCode(300)
                    .setType(GAME)
                    .build();
        } else {
            return Game.Answer.newBuilder()
                    .setRequest("Your hand is currently empty.")
                    .setCode(300)
                    .setType(GAME)
                    .build();
        }
    }
}

