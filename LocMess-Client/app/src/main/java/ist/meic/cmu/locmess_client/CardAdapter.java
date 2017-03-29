package ist.meic.cmu.locmess_client;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by Catarina on 29/03/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private String[] mDummyDataset;
    private int mCardLayout;

    public CardAdapter(String[] dataset, int cardLayout ) {
        mDummyDataset = dataset;
        mCardLayout = cardLayout;
        Log.i(getClass().getSimpleName(), Arrays.toString(mDummyDataset));
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.available_msg_card, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(getClass().getSimpleName(), "onBindViewHolder");
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDummyDataset[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDummyDataset.length;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mTextView;
        public ViewHolder(View itemView) {
            // each dummy data item is just a string
            super(itemView);
            mCardView = (CardView)itemView.findViewById(R.id.card_view);
            mTextView = (TextView)itemView.findViewById(R.id.dummy_text);
        }
    }
}
