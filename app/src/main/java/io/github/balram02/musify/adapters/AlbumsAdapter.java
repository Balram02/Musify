package io.github.balram02.musify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.balram02.musify.Models.AlbumsModel;
import io.github.balram02.musify.R;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.SongListViewHolder> {

    private List<AlbumsModel> songs = new ArrayList<>();
    private OnItemClickListener listener;

    public AlbumsAdapter() {
    }

    public AlbumsAdapter(List<AlbumsModel> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_albums_item, viewGroup, false);
        return new SongListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListViewHolder holder, int i) {

        AlbumsModel model = songs.get(i);
        holder.songName.setText(model.getAlbum());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSongs(List<AlbumsModel> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

//    TODO: complete below method

    public String getDurationInMinutes(long miliseconds) {

        return "02:59";
    }

    class SongListViewHolder extends RecyclerView.ViewHolder {

        private TextView songName;
/*        private TextView songArtist;
        private TextView songDuration;*/

        public SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != 0) {
                    listener.onItemClick(songs.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AlbumsModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
