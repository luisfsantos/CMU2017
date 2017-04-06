package ist.meic.cmu.locmess_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ist.meic.cmu.locmess_client.messages.inbox.InboxActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class LoginActivity extends AppCompatActivity {

    EditText mPassword;
    EditText mUsername;

    private static final String USERNAME_TAG = "USERNAME";
    private static final String PASSWORD_TAG = "PASSWORD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.login_username);
        mPassword = (EditText) findViewById(R.id.login_password);

        Intent intent = getIntent();
        String username = intent.getStringExtra(USERNAME_TAG);
        if (username != null) {
            mUsername.setText(username);
        }
    }

    public void switchToSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra(USERNAME_TAG, mUsername.getText().toString());
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        finish();
    }

    public void doLogin(View view) {
        if (TextUtils.isEmpty(mUsername.getText().toString())) {
            mUsername.setError(getString(R.string.username_missing));
            return;
        }
        if (TextUtils.isEmpty(mPassword.getText().toString())) {
            mPassword.setError(getString(R.string.password_missing));
            return;
        }
        Toast.makeText(this, "All ok. Now I can start AsyncTask", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, InboxActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USERNAME_TAG, mUsername.getText().toString());
        outState.putString(PASSWORD_TAG, mPassword.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUsername.setText(savedInstanceState.getString(USERNAME_TAG));
        mPassword.setText(savedInstanceState.getString(PASSWORD_TAG));
    }
}
