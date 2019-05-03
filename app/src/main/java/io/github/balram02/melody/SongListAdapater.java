package io.github.balram02.melody;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SongListAdapater extends RecyclerView.Adapter<SongListAdapater.SongListViewHolder> {

    List<SongListModel> songs;

    public SongListAdapater(List<SongListModel> songs) {
        this.songs = songs;
        Log.d("TAGGG", this.songs.size() + " = size");
    }

    @NonNull
    @Override
    public SongListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        return new SongListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListViewHolder holder, int i) {

        SongListModel model = songs.get(i);
        holder.songName.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class SongListViewHolder extends RecyclerView.ViewHolder {

        private TextView songName;

        public SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_name);
        }
    }
}
