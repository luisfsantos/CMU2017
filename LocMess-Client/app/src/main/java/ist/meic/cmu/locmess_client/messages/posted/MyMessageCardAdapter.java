package ist.meic.cmu.locmess_client.messages.posted;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import ist.meic.cmu.locmess_client.R;
import ist.meic.cmu.locmess_client.data.Message;
import ist.meic.cmu.locmess_client.messages.OnRecyclerCardClicked;

/**
 * Created by lads on 05/04/2017.
 */

public class MyMessageCardAdapter extends RecyclerView.Adapter<MyMessageCardAdapter.ViewHolder>  {
    List<Message> mDataset;
    OnRecyclerCardClicked mCardListener;

    public MyMessageCardAdapter(List<Message> dataset, OnRecyclerCardClicked cardListener) {
        mDataset = dataset;
        mCardListener = cardListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyMessageCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_my_messages, parent, false);
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
        Message msg = mDataset.get(position);
        holder.mPostTitle.setText(msg.title);
        holder.mPostText.setText(msg.text);
        holder.mPostLocation.setText(msg.location);
        holder.mPostTime.setText(SimpleDateFormat.getInstance().format(msg.time));
        holder.mPostTitle.setTypeface(null, Typeface.BOLD);

        holder.mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                showRemoveDialog(view, position);
            }
        });

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

    private void showRemoveDialog(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(R.string.remove_message)
                .setPositiveButton(R.string.remove_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDataset.remove(position);
                        notifyItemRemoved(position);
                    }
                }).setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mPostTitle;
        public TextView mPostText;
        public TextView mPostLocation;
        public TextView mPostTime;
        public ImageButton mRemoveBtn;

        public ViewHolder(View itemView) {
            // each dummy data item is just a string
            super(itemView);
            mCardView = (CardView)itemView.findViewById(R.id.card_view);
            mPostTitle = (TextView)itemView.findViewById(R.id.post_title);
            mPostText = (TextView)itemView.findViewById(R.id.post_text);
            mPostLocation = (TextView)itemView.findViewById(R.id.post_location);
            mPostTime = (TextView)itemView.findViewById(R.id.post_time);
            mRemoveBtn = (ImageButton)itemView.findViewById(R.id.remove_btn);
        }
    }
}
