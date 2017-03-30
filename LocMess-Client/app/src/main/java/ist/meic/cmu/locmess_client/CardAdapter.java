package ist.meic.cmu.locmess_client;

import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import ist.meic.cmu.locmess_client.data.LocMessage;

/**
 * Created by Catarina on 29/03/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<LocMessage> mDataset;
    private int mCardLayout;

    public CardAdapter(List<LocMessage> dataset, int cardLayout) {
        mDataset = dataset;
        mCardLayout = cardLayout;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
            .inflate(mCardLayout, parent, false);
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        LocMessage msg = mDataset.get(position);
        holder.mPostAuthor.setText(msg.author);
        holder.mPostTitle.setText(msg.title);
        holder.mPostText.setText(msg.text);
        holder.mPostLocation.setText(msg.location);
        holder.mPostTime.setText(SimpleDateFormat.getInstance().format(msg.time));
        if (!msg.isRead()) {
            holder.mPostAuthor.setTypeface(null, Typeface.BOLD);
            holder.mPostTitle.setTypeface(null, Typeface.BOLD);
        } else {
            // need to check both if and else bc of RecyclerView properties
            holder.mPostAuthor.setTypeface(null, Typeface.NORMAL);
            holder.mPostTitle.setTypeface(null, Typeface.NORMAL);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mPostAuthor;
        public TextView mPostTitle;
        public TextView mPostText;
        public TextView mPostLocation;
        public TextView mPostTime;

        public ViewHolder(View itemView) {
            // each dummy data item is just a string
            super(itemView);
            mCardView = (CardView)itemView.findViewById(R.id.card_view);
            mPostAuthor = (TextView)itemView.findViewById(R.id.post_author);
            mPostTitle = (TextView)itemView.findViewById(R.id.post_title);
            mPostText = (TextView)itemView.findViewById(R.id.post_text);
            mPostLocation = (TextView)itemView.findViewById(R.id.post_location);
            mPostTime = (TextView)itemView.findViewById(R.id.post_time);
        }
    }
}
