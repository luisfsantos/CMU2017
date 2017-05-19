package ist.meic.cmu.locmess_client.authentication;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ist.meic.cmu.locmess_client.R;

import static ist.meic.cmu.locmess_client.authentication.AuthenticatorActivity.ARG_ACCOUNT_TYPE;
import static ist.meic.cmu.locmess_client.authentication.AuthenticatorActivity.KEY_ERROR_MESSAGE;
import static ist.meic.cmu.locmess_client.authentication.AuthenticatorActivity.PARAM_USER_PASS;

/**
 * Created by Catarina on 08/05/2017.
 */

public class NewSignUpActivity extends AppCompatActivity {
    private static final String TAG = "NewSignUpActivity";
    private String mAccountType;

    EditText mUsername;
    EditText mPassword;
    EditText mConfirmPassword;
    Button mSignupBtn;
    TextView mErrorView;

    private static final int MIN_USERNAME_LENGTH = 6;
    private static final String USERNAME_TAG = "USERNAME";
    private static final String PASSWORD_TAG = "PASSWORD";
    private static final String CONFIRM_PASSWORD_TAG = "CONFIRM_PASSWORD";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText) findViewById(R.id.signup_username);
        mPassword = (EditText) findViewById(R.id.signup_password);
        mConfirmPassword = (EditText) findViewById(R.id.signup_confirm_password);
        mSignupBtn = (Button) findViewById(R.id.signup_btn);
        mErrorView = (TextView) findViewById(R.id.error);

    }

    private void unfreeze() {
        mUsername.setEnabled(true);
        mPassword.setEnabled(true);
        mConfirmPassword.setEnabled(true);
        mSignupBtn.setEnabled(true);
    }

    private void freeze() {
        mUsername.setEnabled(false);
        mPassword.setEnabled(false);
        mConfirmPassword.setEnabled(false);
        mSignupBtn.setEnabled(false);
    }

    private boolean isValidUsername$Local(String username) {
        return username.length() >= MIN_USERNAME_LENGTH;
    }

    private boolean passwordsMatch(String password, String confirm) {
        return password != null && confirm != null && TextUtils.equals(password, confirm);
    }

    public void doSignUp(View view) {
        final String username = mUsername.getText().toString();
        if (!isValidUsername$Local(username)) {
            mUsername.setError(getString(R.string.username_too_short));
            mUsername.requestFocus();
            return;
        }
        final String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.password_missing));
            mPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mConfirmPassword.getText().toString())) {
            mConfirmPassword.setError(getString(R.string.confirm_password_missing));
            mConfirmPassword.requestFocus();
            return;
        }
        if (!passwordsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())) {
            mConfirmPassword.setError(getString(R.string.password_mismatch));
            mConfirmPassword.requestFocus();
            return;
        }

        mErrorView.setVisibility(View.GONE);
        freeze();

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... strings) {
                String authToken;
                Bundle data = new Bundle();
                try {
                    authToken = AuthUtils.userSignUp(username, password, mAccountType);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                    data.putString(PARAM_USER_PASS, password);
                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }
                Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    mErrorView.setText(intent.getStringExtra(KEY_ERROR_MESSAGE));
                    mErrorView.setVisibility(View.VISIBLE);
                    unfreeze();
                } else {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    public void switchToLogin(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
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
