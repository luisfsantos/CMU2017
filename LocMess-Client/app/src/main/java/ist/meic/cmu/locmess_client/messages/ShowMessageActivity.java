package ist.meic.cmu.locmess_client.messages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.Serializable;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 31/03/2017.
 */

public class ShowMessageActivity extends AppCompatActivity {

    public static final String INTENT_MESSAGE = "message";
    Message mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_msg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMessage = (Message)getIntent().getSerializableExtra(INTENT_MESSAGE);
        TextView postAuthor = (TextView)findViewById(R.id.post_author);
        TextView postTitle = (TextView)findViewById(R.id.post_title);
        TextView postText = (TextView)findViewById(R.id.post_text);
        TextView postLocation = (TextView)findViewById(R.id.post_location);
        TextView postTime = (TextView)findViewById(R.id.post_time);

        if (mMessage != null) {
            postAuthor.setText(mMessage.author);
            postTitle.setText(mMessage.title);
            postText.setText(mMessage.text);
            postLocation.setText(mMessage.location);
            postTime.setText(mMessage.time);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INTENT_MESSAGE, mMessage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMessage = (Message)savedInstanceState.getSerializable(INTENT_MESSAGE);
    }

    public static class Message implements Serializable {
        public final String author;
        public final String title;
        public final String text;
        public final String time;
        public final String location;

        public Message(String author, String title, String text, String time, String location) {
            this.author = author;
            this.title = title;
            this.text = text;
            this.time = time;
            this.location = location;
        }
    }
}
