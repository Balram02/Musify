package io.github.balram02.musify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.R;
import io.github.balram02.musify.constants.Constants;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongListViewHolder> {

    private List<SongsModel> songs = new ArrayList<>();
    private OnItemClickListener listener;

    public SongsAdapter() {
    }

    public SongsAdapter(List<SongsModel> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_all_songs_item, viewGroup, false);
        return new SongListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListViewHolder holder, int i) {

        SongsModel model = songs.get(i);
        holder.songName.setText(model.getTitle());
        holder.songArtist.setText(model.getArtist());
        holder.songDuration.setText(Constants.millisecondsToMinutesAndSeconds(model.getDuration()));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateSongsList(List<SongsModel> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }


    public class SongListViewHolder extends RecyclerView.ViewHolder {

        private TextView songName;
        private TextView songArtist;
        private TextView songDuration;

        SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            songArtist = itemView.findViewById(R.id.song_artist);
            songDuration = itemView.findViewById(R.id.song_duration);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != 0) {
                    int position = getAdapterPosition();
                    listener.onItemClick(songs.get(position));
                    listener.onItemClick(
                            getAdapterPosition() == 0 ? songs.get(songs.size() - 1) : songs.get(position - 1),
                            songs.get(position),
                            songs.get(position + 1)
                    );
                }
            });
        }

        public SongsModel getItem() {
            return songs.get(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SongsModel model);

        void onItemClick(SongsModel previousModel, SongsModel currentModel, SongsModel nextModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
