package io.github.balram02.musify.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
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

import io.github.balram02.musify.R;
import io.github.balram02.musify.constants.Constants;
import io.github.balram02.musify.listeners.OnAdapterItemClickListener;
import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.viewModels.SharedViewModel;

public class SongsAdapter extends ListAdapter<SongsModel, SongsAdapter.SongListViewHolder> {

    private OnAdapterItemClickListener listener;
    private Activity activity;

    private static DiffUtil.ItemCallback<SongsModel> diffCallback = new DiffUtil.ItemCallback<SongsModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull SongsModel oldItem, @NonNull SongsModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull SongsModel oldItem, @NonNull SongsModel newItem) {
            return oldItem.equals(newItem);
        }
    };

    public SongsAdapter(Activity activity) {
        super(diffCallback);
        this.activity = activity;
    }


    @NonNull
    @Override
    public SongListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_song_list_item, viewGroup, false);
        return new SongListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListViewHolder holder, int i) {

        SongsModel model = getItem(i);
        holder.songName.setText(model.getTitle());
        holder.songArtist.setText(model.getArtist());
        holder.songDuration.setText(Constants.convertMilliseconds(model.getDuration()));
    }

    public class SongListViewHolder extends RecyclerView.ViewHolder {

        private TextView songName;
        private TextView songArtist;
        private TextView songDuration;
        private RelativeLayout songItem;
        private ImageView songMenu;
        private SharedViewModel sharedViewModel;

        SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            songItem = itemView.findViewById(R.id.song_item);
            songName = itemView.findViewById(R.id.song_name);
            songArtist = itemView.findViewById(R.id.song_artist);
            songDuration = itemView.findViewById(R.id.song_duration);
            songMenu = itemView.findViewById(R.id.song_menu);

            if (sharedViewModel == null)
                sharedViewModel = ViewModelProviders.of((FragmentActivity) activity).get(SharedViewModel.class);

            songItem.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != -1) {
                    listener.onItemClick(getItem());
                }
            });

            songMenu.setOnClickListener(v -> {

                BottomSheetDialog dialogFragment = new BottomSheetDialog(itemView.getContext());
                dialogFragment.setContentView(R.layout.song_menu_layout);

                ((TextView) dialogFragment.findViewById(R.id.title)).setText(getItem().getTitle());
                ImageView favImage = dialogFragment.findViewById(R.id.fav_img);
                TextView favText = dialogFragment.findViewById(R.id.fav_text);

                boolean isFav = getItem().isFavorite();
                if (isFav) {
                    favText.setText("Remove from favorites");
                    favImage.setImageResource((R.drawable.ic_favorite_border_white_24dp));
                } else {
                    favText.setText("Add to favorites");
                    favImage.setImageResource(R.drawable.ic_favorite_filled_white_24dp);
                }

                dialogFragment.findViewById(R.id.add_to_fav).setOnClickListener(v1 -> {
                    SongsModel model = getItem();
                    model.setFavorite(!isFav);
                    sharedViewModel.update(model);
                    dialogFragment.dismiss();
                });

                dialogFragment.findViewById(R.id.song_info_layout).setOnClickListener(v1 -> {

                    BottomSheetDialog infoDialogFragment = new BottomSheetDialog(itemView.getContext());
                    infoDialogFragment.setContentView(R.layout.song_info_layout);

                    SongsModel model = getItem();

                    Bitmap art = Constants.getAlbumArt(itemView.getContext(), model.getAlbumId());
                    if (art != null)
                        ((ImageView) infoDialogFragment.findViewById(R.id.info_album_art)).setImageBitmap(art);
                    else {
                        ((ImageView) infoDialogFragment.findViewById(R.id.info_album_art)).setImageResource(R.drawable.ic_music_placeholder_white);
                        infoDialogFragment.findViewById(R.id.info_album_art).setBackgroundResource(R.drawable.background_square_stroke_white_6dp);
                    }
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
            return SongsAdapter.this.getItem(getAdapterPosition());
        }
    }

    public void setOnItemClickListener(OnAdapterItemClickListener listener) {
        this.listener = listener;
    }
}
