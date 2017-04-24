package ist.meic.cmu.locmess_client.authentication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.net.MalformedURLException;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.messages.inbox.InboxActivity;
import ist.meic.cmu.locmess_client.network.LocMessURL;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.WebRequestCallback;
import ist.meic.cmu.locmess_client.network.request_builders.UserRequestBuilder;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SignUpActivity extends AppCompatActivity implements WebRequestCallback{

    EditText mUsername;
    EditText mPassword;
    EditText mConfirmPassword;
    Button mSignupBtn;
    TextView mErrorView;

    private static final int MIN_USERNAME_LENGTH = 6;
    private static final String USERNAME_TAG = "USERNAME";
    private static final String PASSWORD_TAG = "PASSWORD";
    private static final String CONFIRM_PASSWORD_TAG = "CONFIRM_PASSWORD";
    private static final String TAG = "SignUpActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText) findViewById(R.id.signup_username);
        mPassword = (EditText) findViewById(R.id.signup_password);
        mConfirmPassword = (EditText) findViewById(R.id.signup_confirm_password);
        mSignupBtn = (Button) findViewById(R.id.signup_btn);
        mErrorView = (TextView) findViewById(R.id.error);
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
        String username = mUsername.getText().toString();
        if(!isValidUsername$Local(username)) {
            mUsername.setError(getString(R.string.username_too_short));
            mUsername.requestFocus();
            return;
        }
        String password = mPassword.getText().toString();
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

        try {
            RequestData data = (new UserRequestBuilder(username, password)).build(LocMessURL.SIGNUP, RequestData.POST);
            new SignupTask(this, data).execute();
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
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

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public void onNoNetworkConnectivity() {
        mErrorView.setVisibility(View.VISIBLE);
        mErrorView.setText(getString(R.string.no_network_connection));
        unfreeze();
    }

    @Override
    public void onWebRequestError(String message) {
        Log.d(TAG, message);
        unfreeze();
        mErrorView.setVisibility(View.VISIBLE);
        mErrorView.setText(message);
    }

    @Override
    public void onWebRequestSuccessful(String message) {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        try {
            RequestData data = (new UserRequestBuilder(username, password)).build(LocMessURL.LOGIN, RequestData.POST);
            new LoginTask(new LoginCallback(), data).execute();
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    private class LoginCallback implements WebRequestCallback {

        @Override
        public void onNoNetworkConnectivity() {
            String message = getString(R.string.no_network_connection) + " " +
                    getString(R.string.error_login_after_signup);
            SignUpActivity.this.onWebRequestError(message);
        }

        @Override
        public NetworkInfo getActiveNetworkInfo() {
            return SignUpActivity.this.getActiveNetworkInfo();
        }

        @Override
        public void onWebRequestError(String message) {
            SignUpActivity.this.onWebRequestError(getString(R.string.error_login_after_signup));
        }

        @Override
        public void onWebRequestSuccessful(String message) {
            Log.d(TAG, message);
            Intent intent = new Intent(SignUpActivity.this, InboxActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            finish();
        }

        @Override
        public Context getContext() {
            return SignUpActivity.this;
        }
    }
}
