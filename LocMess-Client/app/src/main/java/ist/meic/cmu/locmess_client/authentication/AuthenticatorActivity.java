package ist.meic.cmu.locmess_client.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 08/05/2017.
 */

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String PARAM_USER_PASS = "USER_PASS";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    private final int REQUEST_SIGNUP = 1;

    EditText mPassword;
    EditText mUsername;
    Button mLoginBtn;
    TextView mErrorView;

    AccountManager mAccountManager;
    String mAuthTokenType;
    private static final String TAG = "AuthenticatorActivity";

    private static final String USERNAME_TAG = "USERNAME";
    private static final String PASSWORD_TAG = "PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAccountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (accountName != null) {
            Log.d(TAG, accountName);
        }
        if (mAuthTokenType == null) {
            mAuthTokenType = GenericAccountService.AUTH_TOKEN_TYPE;
        }

        mUsername = (EditText) findViewById(R.id.login_username);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mErrorView = (TextView) findViewById(R.id.error);
    }

    public void switchToSignUp(View view) {
        // Since there can only be one AuthenticatorActivity, we call the sign up activity, get its results,
        // and return them in setAccountAuthenticatorResult(). See finishLogin().
        Intent signUp = new Intent(getBaseContext(), NewSignUpActivity.class);
        signUp.putExtras(getIntent().getExtras());
        startActivityForResult(signUp, REQUEST_SIGNUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP && resultCode == RESULT_OK){
            finishLogin(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void unfreeze() {
        mUsername.setEnabled(true);
        mPassword.setEnabled(true);
        mLoginBtn.setEnabled(true);
    }

    private void freeze() {
        mUsername.setEnabled(false);
        mPassword.setEnabled(false);
        mLoginBtn.setEnabled(false);
    }

    public void doLogin(View view) {
        final String username = mUsername.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.username_missing));
            mUsername.requestFocus();
            return;
        }
        final String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.password_missing));
            mPassword.requestFocus();
            return;
        }
        mErrorView.setVisibility(View.GONE);
        freeze();

        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        Log.d(TAG, "Account type="+accountType);

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... strings) {
                Log.i(TAG, "Started authenticating...");
                String authToken = null;
                Bundle data = new Bundle();

                try {
                    authToken = AuthUtils.userLogin(username, password, mAuthTokenType);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                    data.putString(PARAM_USER_PASS, password);
                } catch (Exception e) {
                    Log.d(TAG, "Caught IOException");
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }
                final Intent res = new Intent();
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
                    finishLogin(intent);
                }
            }
        }.execute();
    }


    private void finishLogin(Intent intent) {
        Log.d(TAG, "Finishing login...");
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName,
                intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d(TAG, "Adding account explicitly");
            String authToken = getIntent().getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authTokenType = mAuthTokenType;

            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authTokenType, authToken);
        } else {
            Log.d(TAG, "Setting password");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
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
