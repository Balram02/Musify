package io.github.balram02.melody.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.github.balram02.melody.R;
import io.github.balram02.melody.ViewModels.AllSongsViewModel;

import static io.github.balram02.melody.constants.Constants.IS_PLAYING;

public class AllSongsFragment extends Fragment {

    private AllSongsViewModel mViewModel;
    private RecyclerView recyclerView;
    private SongsAdapter songsAdapter;
    private SwipeRefreshLayout refreshLayout;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_songs_fragment, container, false);

        recyclerView = v.findViewById(R.id.recycler_view);
        refreshLayout = v.findViewById(R.id.refresh_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        songsAdapter = new SongsAdapter();
        recyclerView.setAdapter(songsAdapter);

        songsAdapter.setOnItemClickListener(model -> {
            Intent intent = new Intent(getActivity(), PlayerService.class);
            intent.putExtra("song_name", model.getTitle());
            intent.putExtra("song_artist", model.getArtist());
            intent.putExtra("song_path", model.getPath());
            if (IS_PLAYING) {
                context.stopService(intent);
                IS_PLAYING = true;
                context.startService(intent);
            } else {
                IS_PLAYING = true;
                context.startService(intent);
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
            songsAdapter.setSongs(songsModels);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }
}
