package net.validcat.popularmoviesapp.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.validcat.popularmoviesapp.R;
import net.validcat.popularmoviesapp.model.VideoItem;

import java.util.List;

/**
 * Created by Dobrunov on 19.08.2015.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private List<VideoItem> videos;

    public void addItems(List<VideoItem> video) {
        videos.addAll(video);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        private String movieKey;
        public ViewHolder(final View parent) {
            super(parent);
            name = (TextView) parent.findViewById(R.id.tv_video_name);
            parent.setOnClickListener(this);
        }

        public void setMovieKey(String movieKey) {
            this.movieKey = movieKey;
        }

        @Override
        public void onClick(View v) {
            if (movieKey == null)
                return;
//            v.getContext().startActivity(
//                    new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://www.youtube.com/watch?v=" + movieKey)));
            watchYoutubeVideo(v.getContext());
        }

        public void watchYoutubeVideo(Context c) {
            try{
                c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + movieKey)));
            }catch (ActivityNotFoundException ex){
                c.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + movieKey)));
            }
        }
    }

    public VideoAdapter(List<VideoItem> videos) {
        this.videos = videos;
    }

    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(videos.get(position).name);
        holder.setMovieKey(videos.get(position).key);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
