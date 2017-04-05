package ist.meic.cmu.locmess_client.inbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.Message;
import ist.meic.cmu.locmess_client.navigation.BaseNavigationActivity;

/**
 * Created by Catarina on 31/03/2017.
 */

public class ShowMessageActivity extends AppCompatActivity {

    Message mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_msg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMessage = getIntent().getParcelableExtra("message");
        TextView postAuthor = (TextView)findViewById(R.id.post_author);
        TextView postTitle = (TextView)findViewById(R.id.post_title);
        TextView postText = (TextView)findViewById(R.id.post_text);
        TextView postLocation = (TextView)findViewById(R.id.post_location);
        TextView postTime = (TextView)findViewById(R.id.post_time);

        postAuthor.setText(mMessage.author);
        postTitle.setText(mMessage.title);
        postText.setText(mMessage.text);
        postLocation.setText(mMessage.location);
        postTime.setText(SimpleDateFormat.getInstance().format(mMessage.time));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("message", mMessage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMessage = savedInstanceState.getParcelable("message");
    }
}
