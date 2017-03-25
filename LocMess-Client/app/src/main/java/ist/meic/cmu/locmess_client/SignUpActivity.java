package ist.meic.cmu.locmess_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SignUpActivity extends AppCompatActivity {

    EditText mUsername;
    EditText mPassword;
    EditText mConfirmPassword;

    private static final int MIN_USERNAME_LENGTH = 6;
    private static final String USERNAME_TAG = "USERNAME";
    private static final String PASSWORD_TAG = "PASSWORD";
    private static final String CONFIRM_PASSWORD_TAG = "CONFIRM_PASSWORD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText) findViewById(R.id.signup_username);
        mPassword = (EditText) findViewById(R.id.signup_password);
        mConfirmPassword = (EditText) findViewById(R.id.signup_confirm_password);
        Intent intent = getIntent();
        String username = intent.getStringExtra(USERNAME_TAG);

        if (username != null) {
            mUsername.setText(username);
        }
    }

    public void switchToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(USERNAME_TAG, mUsername.getText().toString());
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        finish();
    }

    private boolean isValidUsername$Local(String username) {
        return username.length() >= MIN_USERNAME_LENGTH;
    }

    private boolean passwordsMatch(String password, String confirm) {
        return password != null && confirm != null && TextUtils.equals(password, confirm);
    }

    public void doSignUp(View view) {
        if(!isValidUsername$Local(mUsername.getText().toString())) {
            mUsername.setError(getString(R.string.username_too_short));
            return;
        }
        if (TextUtils.isEmpty(mPassword.getText().toString())) {
            mPassword.setError(getString(R.string.password_missing));
            return;
        }
        if (TextUtils.isEmpty(mConfirmPassword.getText().toString())) {
            mConfirmPassword.setError(getString(R.string.confirm_password_missing));
            return;
        }
        if (!passwordsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())) {
            mConfirmPassword.setError(getString(R.string.password_mismatch));
            return;
        }

        Toast.makeText(this, "All ok. Now I can start AsyncTask", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USERNAME_TAG, mUsername.getText().toString());
        outState.putString(PASSWORD_TAG, mPassword.getText().toString());
        outState.putString(CONFIRM_PASSWORD_TAG, mConfirmPassword.getText().toString());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUsername.setText(savedInstanceState.getString(USERNAME_TAG));
        mPassword.setText(savedInstanceState.getString(PASSWORD_TAG));
        mConfirmPassword.setText(savedInstanceState.getString(CONFIRM_PASSWORD_TAG));
    }
}
