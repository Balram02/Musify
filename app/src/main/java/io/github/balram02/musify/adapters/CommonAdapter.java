package io.github.balram02.musify.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.balram02.musify.R;
import io.github.balram02.musify.constants.Constants;
import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.ui.activities.CommonActivity;

public class CommonAdapter extends ListAdapter<SongsModel, CommonAdapter.SongListViewHolder> {

    private Activity context;
    private boolean isAlbum;

    private static DiffUtil.ItemCallback<SongsModel> diffCallback = new DiffUtil.ItemCallback<SongsModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull SongsModel oldItem, @NonNull SongsModel newItem) {
            return oldItem.getPath().equals(newItem.getPath());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SongsModel oldItem, @NonNull SongsModel newItem) {
            return oldItem.equals(newItem);
        }
    };

    public CommonAdapter(Activity context) {
        super(diffCallback);
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

        SongsModel model = getItem(i);
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

    public void setList(List<SongsModel> songs, boolean isAlbum) {
        submitList(songs);
        this.isAlbum = isAlbum;
    }

    class SongListViewHolder extends RecyclerView.ViewHolder {

        private TextView commonName;
        private ImageView albumArt;

        SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            commonName = itemView.findViewById(R.id.common_name);
            albumArt = itemView.findViewById(R.id.album_art);

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, CommonActivity.class);
                intent.setAction(isAlbum ? "album" : "artist");
                intent.putExtra(isAlbum ? "album_name" : "artist_name", isAlbum ? getItem().getAlbum() : getItem().getArtist());
                intent.putExtra("album_id", getItem().getAlbumId());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(context, albumArt, "album_art");
                context.startActivity(intent, optionsCompat.toBundle());
            });
        }

        private SongsModel getItem() {
            return CommonAdapter.this.getItem(getAdapterPosition());
        }
    }
}
