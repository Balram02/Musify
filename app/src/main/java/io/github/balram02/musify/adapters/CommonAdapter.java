package io.github.balram02.musify.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.github.balram02.musify.R;
import io.github.balram02.musify.constants.Constants;
import io.github.balram02.musify.models.SongsModel;

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.SongListViewHolder> {

    private List<SongsModel> songs = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;
    private boolean isAlbum;

    public CommonAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SongListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_common_item, viewGroup, false);
        return new SongListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListViewHolder holder, int i) {

        SongsModel model = songs.get(i);
        holder.commonName.setText(isAlbum ? model.getAlbum() : model.getArtist());

        Uri uri = Constants.getAlbumArtUri(model.getAlbumId());

        Picasso.get().load(uri).into(holder.albumArt, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                holder.albumArt.setImageResource(R.drawable.ic_music_placeholder_white);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setList(List<SongsModel> songs, boolean isAlbum) {
        this.songs = songs;
        this.isAlbum = isAlbum;
        notifyDataSetChanged();
    }

    class SongListViewHolder extends RecyclerView.ViewHolder {

        private TextView commonName;
        private ImageView albumArt;

        SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            commonName = itemView.findViewById(R.id.common_name);
            albumArt = itemView.findViewById(R.id.album_art);

/*            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != 0) {
                    listener.onItemClick(songs.get(getAdapterPosition()));
                }
            });*/
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SongsModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
