package io.github.balram02.musify.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

import io.github.balram02.musify.R;
import io.github.balram02.musify.constants.Constants;
import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.ui.MainActivity;

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.SongListViewHolder> {

    private List<SongsModel> songs = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;
    private boolean isAlbum;

    public CommonAdapter(Context context) {
        this.context = context;
    }

    public CommonAdapter(List<SongsModel> songs) {
        this.songs = songs;
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
        holder.songName.setText(isAlbum ? model.getAlbum() : model.getArtist());

        ((MainActivity) context).runOnUiThread(() -> {
            Bitmap img = Constants.getAlbumArt(context, model.getAlbumId());
            if (img != null)
                holder.albumArt.setImageBitmap(img);
            else
                holder.albumArt.setImageResource(R.drawable.ic_music_placeholder_white);
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

        private TextView songName;
        private ImageView albumArt;
/*        private TextView songArtist;
        private TextView songDuration;*/

        SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.common_name);
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


    public Bitmap getAlbumArt(Long album_id) {

        Bitmap bm = null;
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

}
