import eu.epitech.jcoinche.jcoincheclient.game.SaveObject;
import eu.epitech.jcoinche.protobuf.Game;
import org.junit.Test;

/**
 * Created by noboud_n on 04/12/2016.
 */
public class SaveObjectTest {

    @Test
    public void testSaveObject() {
        SaveObject save = new SaveObject();
        Game.Answer answer = Game.Answer.newBuilder().build();

        save.set_answer(answer);
        System.out.println("Answer = " + save.get_answer());
        save.set_answer(null);
        System.out.println("Answer = " + save.get_answer());
    }
}
