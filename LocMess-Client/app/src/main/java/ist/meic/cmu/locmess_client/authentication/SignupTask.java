package ist.meic.cmu.locmess_client.authentication;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.network.BaseWebTask;
import ist.meic.cmu.locmess_client.network.RequestData;
import ist.meic.cmu.locmess_client.network.WebRequest;
import ist.meic.cmu.locmess_client.network.WebRequestCallback;
import ist.meic.cmu.locmess_client.network.WebRequestResult;

/**
 * Created by Catarina on 23/04/2017.
 */

public class SignupTask extends BaseWebTask {

    public SignupTask(WebRequestCallback callback, RequestData requestData) {
        super(callback, requestData);
    }

    @Override
    protected WebRequestResult doInBackground(RequestData... requestData) {
        try {
            return new WebRequest(mRequestData).execute();
        } catch (Exception e) {
            e.printStackTrace();
            WebRequestResult result = new WebRequestResult();
            result.setException(e);
            return result;
        }
    }

    @Override
    protected void onPostExecute(WebRequestResult result) {
        if (result != null && mCallback != null) {
            if (result.getException() != null) {
                mCallback.onWebRequestError(mCallback.getContext().getString(R.string.something_went_wrong));
            } else if (result.getError() != null) {
                String message = result.getErrorMessages();
                if (message == null) {
                    message = mCallback.getContext().getString(R.string.something_went_wrong);
                }
                mCallback.onWebRequestError(message);
            } else if (result.getResult() != null) {
                mCallback.onWebRequestSuccessful(result.getResultStatusMessage());
            }
        }
    }
}
