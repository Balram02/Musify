package io.github.balram02.musify.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.github.balram02.musify.R;
import io.github.balram02.musify.utils.Constants;
import io.github.balram02.musify.listeners.OnAdapterItemClickListener;
import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.viewModels.SharedViewModel;

public class FavoritesAdapter extends ListAdapter<SongsModel, FavoritesAdapter.FavoritesViewHolder> {

    private Context context;
    private OnAdapterItemClickListener listener;

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

    public FavoritesAdapter(Context context) {
        super(diffCallback);
        this.context = context;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcv_song, parent, false);
        return new FavoritesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int i) {

        SongsModel model = getItem(i);
        holder.songName.setText(model.getTitle());
        holder.songArtist.setText(model.getArtist());
        holder.songDuration.setText(Constants.convertMilliseconds(model.getDuration()));

        Uri uri = Constants.getAlbumArtUri(model.getAlbumId());

        Picasso.get().load(uri).into(holder.songAlbumArt, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                holder.songAlbumArt.setImageResource(R.drawable.ic_music_placeholder_white);
            }
        });

    }


    class FavoritesViewHolder extends RecyclerView.ViewHolder {

        private TextView songName;
        private TextView songArtist;
        private TextView songDuration;
        private RelativeLayout songItem;
        private ImageView songMenu;
        private SharedViewModel sharedViewModel;
        private ImageView songAlbumArt;

        FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            songItem = itemView.findViewById(R.id.song_item);
            songName = itemView.findViewById(R.id.song_name);
            songArtist = itemView.findViewById(R.id.song_artist);
            songDuration = itemView.findViewById(R.id.song_duration);
            songMenu = itemView.findViewById(R.id.song_menu);
            songAlbumArt = itemView.findViewById(R.id.song_album_art_icon);

            songItem.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != -1) {
                    listener.onItemClick(getItem());
                }
            });

            if (sharedViewModel == null)
                sharedViewModel = ViewModelProviders.of((FragmentActivity) context).get(SharedViewModel.class);

            songMenu.setOnClickListener(v -> {

                BottomSheetDialog dialogFragment = new BottomSheetDialog(itemView.getContext());
                dialogFragment.setContentView(R.layout.view_song_menu);

                ((TextView) dialogFragment.findViewById(R.id.title)).setText(getItem().getTitle());
                ImageView favImage = dialogFragment.findViewById(R.id.fav_img);
                TextView favText = dialogFragment.findViewById(R.id.fav_text);

                favText.setText("Remove from favorites");
                favImage.setImageResource((R.drawable.ic_favorite_border_white_24dp));

                dialogFragment.findViewById(R.id.add_to_fav).setOnClickListener(v1 -> {
                    SongsModel model = getItem();
                    model.setFavorite(false);
                    sharedViewModel.update(model);
                    dialogFragment.dismiss();
                });

                dialogFragment.findViewById(R.id.song_info_layout).setOnClickListener(v1 -> {

                    BottomSheetDialog infoDialogFragment = new BottomSheetDialog(itemView.getContext());
                    infoDialogFragment.setContentView(R.layout.view_song_info);

                    SongsModel model = getItem();

                    Uri uri = Constants.getAlbumArtUri(model.getAlbumId());

                    Picasso.get().load(uri).into(infoDialogFragment.findViewById(R.id.info_album_art), new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            ((ImageView) infoDialogFragment.findViewById(R.id.info_album_art))
                                    .setImageResource(R.drawable.ic_music_placeholder_white);
                        }
                    });

                    ((TextView) infoDialogFragment.findViewById(R.id.info_song_album)).setText(model.getAlbum());
                    ((TextView) infoDialogFragment.findViewById(R.id.info_song_title)).setText(model.getTitle());
                    ((TextView) infoDialogFragment.findViewById(R.id.info_song_artist)).setText(model.getArtist());
                    ((TextView) infoDialogFragment.findViewById(R.id.info_song_path)).setText(model.getPath());
                    infoDialogFragment.findViewById(R.id.info_back_arrow).setOnClickListener(v2 -> infoDialogFragment.dismiss());

                    infoDialogFragment.show();

                });

                dialogFragment.show();
            });
        }

        public SongsModel getItem() {
            return FavoritesAdapter.this.getItem(getAdapterPosition());
        }
    }

    public void setOnItemClickerListener(OnAdapterItemClickListener listener) {
        this.listener = listener;
    }

}
