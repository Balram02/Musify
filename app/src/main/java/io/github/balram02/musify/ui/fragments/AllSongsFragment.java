package io.github.balram02.musify.ui.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import io.github.balram02.musify.R;
import io.github.balram02.musify.adapters.SongsAdapter;
import io.github.balram02.musify.listeners.MusicPlayerServiceListener;
import io.github.balram02.musify.models.SongsModel;
import io.github.balram02.musify.viewModels.SharedViewModel;

import static io.github.balram02.musify.constants.Constants.TAG;


public class AllSongsFragment extends Fragment {

    private SharedViewModel mViewModel;
    private RecyclerView recyclerView;
    private SongsAdapter songsAdapter;
    //    private SwipeRefreshLayout refreshLayout;
    private Context context;
    private TextView totalSongs;
    private CardView totalSongsCard;
    private LinearLayout nothing;

    private MusicPlayerServiceListener musicPlayerServiceListener;

    private Bitmap favorite;
    private Bitmap unFavorite;
    private Paint paint;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            musicPlayerServiceListener = (MusicPlayerServiceListener) context;
        } catch (Exception e) {
            Log.d(TAG, "onAttach: " + e.toString());
            Toast.makeText(context, "Must Implement Service Listener", Toast.LENGTH_SHORT).show();
        }

        paint = new Paint();
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_songs_fragment, container, false);

        recyclerView = v.findViewById(R.id.recycler_view);
        totalSongsCard = v.findViewById(R.id.total_songs_card_view);
        totalSongs = v.findViewById(R.id.total_songs);
        nothing = v.findViewById(R.id.nothing_layout);
//        refreshLayout = v.findViewById(R.id.refresh_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        songsAdapter = new SongsAdapter(context);
        recyclerView.setAdapter(songsAdapter);

        songsAdapter.setOnItemClickListener(model -> {
            musicPlayerServiceListener.onUpdateService(model);
        });

/*        refreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                refreshLayout.setRefreshing(false);
            }, 1500);
        });*/

        favorite = drawableToBitmap(true);
        unFavorite = drawableToBitmap(false);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;

                    final float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    final float width = height / 3;

                    TypedValue typedValue = new TypedValue();
                    context.getTheme().resolveAttribute(R.attr.colorOverlay, typedValue, true);
                    if (dX > 0) {
                        paint.setColor(typedValue.data);
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        RectF icon = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(favorite, null, icon, paint);

                    } else {
                        paint.setColor(typedValue.data);
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        RectF icon = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(unFavorite, null, icon, paint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX / 3, dY, actionState, isCurrentlyActive);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                if (direction == ItemTouchHelper.RIGHT) {
                    SongsModel model = ((SongsAdapter.SongListViewHolder) viewHolder).getItem();
                    model.setFavorite(true);
                    mViewModel.update(model);
                } else {
                    SongsModel model = ((SongsAdapter.SongListViewHolder) viewHolder).getItem();
                    model.setFavorite(false);
                    mViewModel.update(model);
                }

                songsAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(recyclerView);

        return v;
    }

    private Bitmap drawableToBitmap(boolean favorite) {

        Drawable drawable = context.getDrawable(favorite ?
                R.drawable.ic_favorite_filled_white_24dp : R.drawable.ic_favorite_border_white_24dp);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        mViewModel.getAllSongs().observe(getViewLifecycleOwner(), songsModels -> {
            songsAdapter.submitList(songsModels);
            songsAdapter.onAttachedToRecyclerView(recyclerView);
            int size = songsModels.size();
            totalSongs.setText(getString(R.string.songs_found_text, size));
            if (size == 0 && nothing.getVisibility() == View.GONE) {
                nothing.setVisibility(View.VISIBLE);
            } else if (size != 0 && nothing.getVisibility() == View.VISIBLE) {
                nothing.setVisibility(View.GONE);
            }
        });
        startTotalSongsCardAnimation();
    }

    private void startTotalSongsCardAnimation() {
        ObjectAnimator animatorOut = ObjectAnimator.ofFloat(totalSongsCard, "translationY", -100f);
        animatorOut.setStartDelay(4000);
        animatorOut.setDuration(5000);
        animatorOut.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        musicPlayerServiceListener = null;
        context = null;
    }
}
