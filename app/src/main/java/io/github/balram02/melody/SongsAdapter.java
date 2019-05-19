package io.github.balram02.melody;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongListViewHolder> {

    private List<SongsModel> songs;
    private OnItemClickListener listener;

    public SongsAdapter() {
    }

    public SongsAdapter(List<SongsModel> songs) {
        this.songs = songs;
//        Log.d("TAGGG", this.songs.size() + " = size");
    }

    @NonNull
    @Override
    public SongListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        return new SongListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListViewHolder holder, int i) {

        SongsModel model = songs.get(i);
        holder.songName.setText(model.getTitle());
        holder.songArtist.setText(model.getArtist());
        holder.songDuration.setText(getDurationInMinutes(model.getDuration()));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSongs(List<SongsModel> songs) {
        this.songs = songs;
    }

//    TODO: complete below method

    public String getDurationInMinutes(long miliseconds) {

        return "02:59";
    }

    class SongListViewHolder extends RecyclerView.ViewHolder {

        private TextView songName;
        private TextView songArtist;
        private TextView songDuration;

        public SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
            songArtist = itemView.findViewById(R.id.song_artist);
            songDuration = itemView.findViewById(R.id.song_duration);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != 0) {
                    listener.onItemClick(songs.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SongsModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
