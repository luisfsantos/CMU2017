package ist.meic.cmu.locmess_client.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ist.meic.cmu.locmess_client.R;

/**
 * Created by Catarina on 02/04/2017.
 */

public class KeyPairDialogFragment extends DialogFragment {

    KeyPairDialogListener mListener;
    AlertDialog mDialog;
    EditText mKeyEditText;
    EditText mValueEditText;

    public interface KeyPairDialogListener {
        void onAddKeyPairClicked(String key, String value);
    }

    public static KeyPairDialogFragment newInstance() {
        return new KeyPairDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (KeyPairDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement KeyPairDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View v = LayoutInflater.from(getContext())
                .inflate(R.layout.add_key_dialog, null);

        mKeyEditText = (EditText)v.findViewById(R.id.new_key);
        mValueEditText = (EditText)v.findViewById(R.id.new_value);

        builder.setTitle(R.string.new_keypair)
                .setView(v)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String key = mKeyEditText.getText().toString().trim();
                        String value = mValueEditText.getText().toString().trim();
                        mListener.onAddKeyPairClicked(key, value);
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        mDialog = builder.create();

        // TextWatcher to enable/disable positive ("ADD") button if EditTexts are empty
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mKeyEditText.getText().toString().trim().isEmpty() ||
                        mValueEditText.getText().toString().trim().isEmpty()) {
                    mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };
        mKeyEditText.addTextChangedListener(watcher);
        mValueEditText.addTextChangedListener(watcher);
        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mKeyEditText.getText().toString().trim().isEmpty() ||
                mValueEditText.getText().toString().trim().isEmpty()) {
            mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false); //initially disabled
        }
    }
}
