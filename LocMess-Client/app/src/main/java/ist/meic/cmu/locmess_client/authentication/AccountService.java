package ist.meic.cmu.locmess_client.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.NetworkErrorException;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Catarina on 25/04/2017.
 */

public class AccountService extends Service {
    private static final String TAG = "AccountService";
    public static final String ACCOUNT_TYPE = "ist.meic.cmu.locmess_client.network.sync.basicsyncadapter";
    public static final String AUTH_TOKEN_TYPE = "JWT";
    private LocMessAuthenticator mAuthenticator;

    public static Account getAccount(String accountName) {
        return new Account(accountName, ACCOUNT_TYPE);
    }

    public static int getActiveAccountHash(Context context) {
        AccountManager manager = AccountManager.get(context);
        Account account = getActiveAccount(manager);
        assert account != null;
        return account.name.hashCode();
    }

    public static Account getActiveAccount(AccountManager am) {
        Account[] accounts = am.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length > 1) {
            // TODO: 08/05/2017 prompt user for account, save account chosen if "always use this"
            return accounts[0];
        } else if (accounts.length == 1){
            return accounts[0];
        } else {
            Log.e(TAG, "No accounts");
//            am.addAccount()
            return null;
        }
    }

    public static String refreshAuthToken(@NonNull Context context, @NonNull Account account,
                                          @NonNull String authTokenType, @NonNull String oldToken)
            throws AuthenticatorException, OperationCanceledException, IOException {
        AccountManager am = AccountManager.get(context);
        am.invalidateAuthToken(account.type, oldToken);
        String authToken = am.blockingGetAuthToken(account, authTokenType, true);
        am.setAuthToken(account, authTokenType, authToken);
        return authToken;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service created");
        mAuthenticator = new LocMessAuthenticator(this);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    public class LocMessAuthenticator extends AbstractAccountAuthenticator {
        private static final String TAG = "LocMessAuthenticator";
        private final Context mContext;

        public LocMessAuthenticator(Context context) {
            super(context);
            this.mContext = context;
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                                 String authTokenType, String[] requiredFeatures, Bundle options)
                throws NetworkErrorException {
            Log.d(TAG, "Add account...");

            final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
            intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
            intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
            intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response,
                                   Account account, String authTokenType, Bundle options)
                throws NetworkErrorException {
            Log.d(TAG, "Getting auth token...");

            if (!authTokenType.equals(AUTH_TOKEN_TYPE)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ERROR_MESSAGE, "Invalid auth token type");
                return result;
            }

            final AccountManager am = AccountManager.get(mContext);
            String authToken = am.peekAuthToken(account, authTokenType);

            if (TextUtils.isEmpty(authToken)) {
                String password = am.getPassword(account);
                if (password != null) {
                    try {
                        authToken = AuthUtils.userLogin(getBaseContext(), account.name, password, authTokenType);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading from network: ", e);
                        throw new NetworkErrorException("No network connection");
                    }
                }
            }

            //if we get an auth token, we return it
            if (!TextUtils.isEmpty(authToken)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                return result;
            }

            // If we get here, then we couldn't access the user's password - so we
            // need to re-prompt them for their credentials. We do that by creating
            // an intent to display our AuthenticatorActivity.
            final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
            intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
            intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, account.name);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;
        }

        @Override
        public String getAuthTokenLabel(String authTokenType) {
            return authTokenType + " (Label)";
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                  Account account, String[] strings)
                throws NetworkErrorException {
            final Bundle result = new Bundle();
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
            return result;
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                     String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                        Account account, String s, Bundle bundle)
                throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse,
                                         Account account, Bundle bundle)
                throws NetworkErrorException {
            return null;
        }
    }
}