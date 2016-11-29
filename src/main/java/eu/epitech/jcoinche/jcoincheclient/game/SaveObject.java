package eu.epitech.jcoinche.jcoincheclient.game;

import eu.epitech.jcoinche.protobuf.Game;

/**
 * Created by noboud_n on 29/11/2016.
 */
public class SaveObject {

    static Game.Answer _answer = null;

    public static Game.Answer get_answer() {
        return _answer;
    }

    public static void set_answer(Game.Answer answer) {
        _answer = answer;
    }
}
