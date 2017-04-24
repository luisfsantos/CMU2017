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

public class LoginActivity extends AppCompatActivity implements WebRequestCallback {

    EditText mPassword;
    EditText mUsername;
    Button mLoginBtn;
    TextView mErrorView;

    private static final String USERNAME_TAG = "USERNAME";
    private static final String PASSWORD_TAG = "PASSWORD";
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.login_username);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mErrorView = (TextView) findViewById(R.id.error);

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
        String username = mUsername.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.username_missing));
            mUsername.requestFocus();
            return;
        }
        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.password_missing));
            mPassword.requestFocus();
            return;
        }

        mErrorView.setVisibility(View.GONE);
        freeze();
        try {
            RequestData data = (new UserRequestBuilder(username, password)).build(LocMessURL.LOGIN, RequestData.POST);
            new LoginTask(this, data).execute();
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onNoNetworkConnectivity() {
        mErrorView.setVisibility(View.VISIBLE);
        mErrorView.setText(getString(R.string.no_network_connection));
        unfreeze();
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
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
        Log.d(TAG, message);
        Intent intent = new Intent(this, InboxActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
