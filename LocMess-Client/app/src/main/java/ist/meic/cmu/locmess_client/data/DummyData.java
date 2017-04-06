package ist.meic.cmu.locmess_client.data;

import android.content.Context;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by lads on 05/04/2017.
 */

public class DummyData {

    public static List<Message> createDummyMessages(int size, Context appContext) {
        List<Message> list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            Message msg = new Message("catarina" + i, "Free pizza",
                    appContext.getString(R.string.lorem_ipsum),
                    new Date(),
                    "Arco do Cego"
            );
            list.add(msg);
        }
        return list;
    }
}
