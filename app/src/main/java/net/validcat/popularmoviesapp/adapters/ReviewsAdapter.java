package net.validcat.popularmoviesapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.validcat.popularmoviesapp.R;
import net.validcat.popularmoviesapp.model.ReviewItem;

import java.util.List;

/**
 * Created by Dobrunov on 19.08.2015.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<ReviewItem> reviewsList;

    public void addItems(List<ReviewItem> reviews) {
        reviewsList.addAll(reviews);
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView author;
        public TextView content;
        public ViewHolder(View parent) {
            super(parent);
            author = (TextView) parent.findViewById(R.id.tv_author);
            content = (TextView) parent.findViewById(R.id.tv_content);
        }
    }

    public ReviewsAdapter(List<ReviewItem> reviewItems) {
        reviewsList = reviewItems;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.author.setText(reviewsList.get(position).author);
        holder.content.setText(reviewsList.get(position).content);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
