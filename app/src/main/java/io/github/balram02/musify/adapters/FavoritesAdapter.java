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
import io.github.balram02.musify.listeners.OnAdapterItemClickListener;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private List<SongsModel> favorites = new ArrayList<>();
    private OnAdapterItemClickListener listener;

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_all_songs_item, parent, false);
        return new FavoritesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int i) {

        SongsModel model = favorites.get(i);
        holder.songName.setText(model.getTitle());
        holder.songArtist.setText(model.getArtist());
        holder.songDuration.setText(Constants.millisecondsToMinutesAndSeconds(model.getDuration()));
    }

    public void setFavorites(List<SongsModel> favorites) {
        this.favorites = favorites;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    class FavoritesViewHolder extends RecyclerView.ViewHolder {

        private TextView songName;
        private TextView songArtist;
        private TextView songDuration;

        FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            songArtist = itemView.findViewById(R.id.song_artist);
            songDuration = itemView.findViewById(R.id.song_duration);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != -1) {
                    int position = getAdapterPosition();
                    listener.onItemClick(favorites.get(position));
                }
            });
        }
    }

    public void setOnItemClickerListsner(OnAdapterItemClickListener listener) {
        this.listener = listener;
    }

}
