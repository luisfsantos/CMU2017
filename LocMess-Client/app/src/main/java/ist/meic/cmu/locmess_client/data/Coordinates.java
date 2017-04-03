package ist.meic.cmu.locmess_client.data;

import android.content.Context;
import android.os.Parcelable;

/**
 * Created by Catarina on 03/04/2017.
 */

public abstract class Coordinates implements Parcelable {

    public abstract String toString(Context context);
}
