package ist.meic.cmu.locmess_client.inbox.available;

import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.LocMessage;
import ist.meic.cmu.locmess_client.inbox.OnRecyclerCardClicked;

/**
 * Created by Catarina on 29/03/2017.
 */

public class AvailableCardAdapter extends RecyclerView.Adapter<AvailableCardAdapter.ViewHolder> {

    List<LocMessage> mDataset;
    OnRecyclerCardClicked mCardListener;

    public AvailableCardAdapter(List<LocMessage> dataset, OnRecyclerCardClicked cardListener) {
        mDataset = dataset;
        mCardListener = cardListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AvailableCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
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

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(holder.getAdapterPosition());
                mCardListener.onRecyclerCardClicked(view);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
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
