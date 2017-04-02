package ist.meic.cmu.locmess_client.profile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.LocKeyPair;

/**
 * Created by Catarina on 02/04/2017.
 */

public class KeyPairsAdapter extends RecyclerView.Adapter<KeyPairsAdapter.ViewHolder> {
    List<LocKeyPair> mKeyPairs;

    public KeyPairsAdapter(List<LocKeyPair> keyPairs) {
        mKeyPairs = keyPairs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.key_pair_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LocKeyPair keyPair = mKeyPairs.get(position);
        holder.mKey.setText(keyPair.key);
        holder.mValue.setText(keyPair.value);
        holder.mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.mItemView.getContext(), "Clicked remove", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mKeyPairs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mItemView;
        ImageButton mRemoveBtn;
        TextView mKey;
        TextView mValue;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mKey = (TextView)itemView.findViewById(R.id.key);
            mValue = (TextView)itemView.findViewById(R.id.value);
            mRemoveBtn = (ImageButton)itemView.findViewById(R.id.remove_btn);
        }
    }
}
