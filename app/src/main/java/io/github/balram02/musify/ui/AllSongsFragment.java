package io.github.balram02.musify.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.github.balram02.musify.Models.SongsModel;
import io.github.balram02.musify.R;
import io.github.balram02.musify.ViewModels.AllSongsViewModel;
import io.github.balram02.musify.adapters.SongsAdapter;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class AllSongsFragment extends Fragment {

    private AllSongsViewModel mViewModel;
    private RecyclerView recyclerView;
    private SongsAdapter songsAdapter;
    private SwipeRefreshLayout refreshLayout;
    private Context context;
    private TextView totalSongs;
    private CardView totalSongsCard;

    private MusicPlayerServiceListener musicPlayerServiceListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            musicPlayerServiceListener = (MusicPlayerServiceListener) context;
        } catch (Exception e) {
            Log.d(TAG, "onAttach: " + e.toString());
            Toast.makeText(context, "Must Implement Service Listener", Toast.LENGTH_SHORT).show();
        }

        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_songs_fragment, container, false);

        recyclerView = v.findViewById(R.id.recycler_view);
        totalSongsCard = v.findViewById(R.id.total_songs_card_view);
        totalSongs = v.findViewById(R.id.total_songs);
        refreshLayout = v.findViewById(R.id.refresh_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        songsAdapter = new SongsAdapter();
        recyclerView.setAdapter(songsAdapter);

        songsAdapter.setOnItemClickListener(new SongsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SongsModel model) {
            }

            @Override
            public void onItemClick(SongsModel previousModel, SongsModel currentModel, SongsModel nextModel) {
                musicPlayerServiceListener.onUpdateService(previousModel, currentModel, nextModel, mViewModel);
            }

        });

        refreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                refreshLayout.setRefreshing(false);
            }, 1500);
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(AllSongsViewModel.class);
        mViewModel.getAllSongs().observe(getViewLifecycleOwner(), songsModels -> {
            songsAdapter.updateSongsList(songsModels);
            totalSongs.setText(getString(R.string.songs_found_text, songsModels.size()));
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
